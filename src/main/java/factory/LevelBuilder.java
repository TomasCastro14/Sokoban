package factory;

import exception.InvalidLevelException;
import model.board.Board;
import model.board.Cell;
import model.board.Position;
import model.box.Player;
import model.game.Level;
import model.game.LevelHeader;
import model.game.loss.EarlyKeyStorageCondition;
import model.game.loss.WrongStorageCondition;
import model.tile.Inaccessible;
import model.tile.Portal;
import model.tile.mechanism.Door;
import model.tile.mechanism.DoorGroup;
import model.tile.mechanism.KeyTile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Builder + Creator (GRASP): collects the parsed cells and assembles the Level. */
public class LevelBuilder {
    /** Checked in this order before building; the first broken rule is reported. */
    private static final List<LevelRule> RULES = List.of(new BorderRule(), new OnePlayerRule(), new PortalPairRule(),
            new DoorKeyGroupRule());

    private LevelHeader header;
    private final Map<Position, CellContent> contents = new LinkedHashMap<>();
    private int rows;
    private int cols;
    private int firstMapLine = 1;

    /** 1-based line of the file where the first map row is, so rules can report file lines. */
    public LevelBuilder withMapStartLine(int line) {
        this.firstMapLine = line;
        return this;
    }

    public LevelBuilder withHeader(LevelHeader header) {
        this.header = header;
        return this;
    }

    public LevelBuilder addCell(Position p, CellContent c) {
        contents.put(p, c);
        rows = Math.max(rows, p.row() + 1);
        cols = Math.max(cols, p.col() + 1);
        return this;
    }

    public Level build() throws InvalidLevelException {
        validate();
        Cell[][] cells = new Cell[rows][cols];
        Position playerPosition = null;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position p = new Position(row, col);
                CellContent content = contents.get(p);
                if (content == null) {
                    cells[row][col] = new Cell(p, new Inaccessible()); // short rows are padded with inaccessible tiles
                    continue;
                }
                cells[row][col] = new Cell(p, content.tile());
                if (content.occupant() != null) {
                    cells[row][col].place(content.occupant());
                }
                if (content.occupant() instanceof Player) {
                    playerPosition = p;
                }
            }
        }
        Collection<DoorGroup> doorGroups = linkDoorGroups();
        linkPortals();
        Level level = new Level(header.name(), (int) header.timeLimit().toSeconds(), header.targetScore(), new Board(cells, playerPosition),
                List.of(new WrongStorageCondition(), new EarlyKeyStorageCondition()));
        doorGroups.forEach(group -> group.addReachabilityListener(level));
        return level;
    }

    /** Wires the Observer: every key tile notifies the door group of its letter. */
    private Collection<DoorGroup> linkDoorGroups() {
        Map<Character, DoorGroup> groups = new HashMap<>();
        for (CellContent content : contents.values()) {
            if (content.tile() instanceof Door door) {
                groups.computeIfAbsent(door.getGroup(), DoorGroup::new).addDoor(door);
            }
        }
        for (CellContent content : contents.values()) {
            if (content.tile() instanceof KeyTile keyTile) {
                keyTile.addListener(groups.computeIfAbsent(keyTile.getGroup(), DoorGroup::new));
            }
        }
        return groups.values();
    }

    /** Each portal letter appears twice; each portal learns where the other one is. */
    private void linkPortals() {
        for (List<Position> pair : portalsByLetter().values()) {
            Position first = pair.get(0);
            Position second = pair.get(1);
            Portal.link(portalAt(first), first, portalAt(second), second);
        }
    }

    private Portal portalAt(Position p) {
        return (Portal) contents.get(p).tile();
    }

    /** Portal positions grouped by letter, in reading order. Shared by the pair rule and the linking. */
    Map<Character, List<Position>> portalsByLetter() {
        return positionsByGroup(Portal.class, Portal::getLetter);
    }

    /** Key tile positions grouped by group letter (upper case), in reading order. */
    Map<Character, List<Position>> keyTilesByGroup() {
        return positionsByGroup(KeyTile.class, KeyTile::getGroup);
    }

    /** Door positions grouped by group letter (upper case), in reading order. */
    Map<Character, List<Position>> doorsByGroup() {
        return positionsByGroup(Door.class, Door::getGroup);
    }

    private <T> Map<Character, List<Position>> positionsByGroup(Class<T> type, Function<T, Character> letterOf) {
        Map<Character, List<Position>> byLetter = new LinkedHashMap<>();
        for (Map.Entry<Position, CellContent> entry : contents.entrySet()) {
            if (type.isInstance(entry.getValue().tile())) {
                char letter = letterOf.apply(type.cast(entry.getValue().tile()));
                byLetter.computeIfAbsent(letter, l -> new ArrayList<>()).add(entry.getKey());
            }
        }
        return byLetter;
    }

    private void validate() throws InvalidLevelException {
        for (LevelRule rule : RULES) {
            rule.check(this);
        }
    }

    // Read-only view of the parsed map for the rules.

    int rows() {
        return rows;
    }

    int cols() {
        return cols;
    }

    /** Returns what was parsed at that position, or null where a short row was padded. */
    CellContent contentAt(Position p) {
        return contents.get(p);
    }

    Collection<CellContent> contents() {
        return contents.values();
    }

    int lineOf(Position p) {
        return firstMapLine + p.row();
    }
}
