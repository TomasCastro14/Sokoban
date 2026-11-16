package factory;

import exception.InvalidLevelException;
import model.board.Board;
import model.board.Cell;
import model.board.Position;
import model.box.Box;
import model.box.OccupantVisitor;
import model.box.Player;
import model.game.Level;
import model.game.LevelHeader;
import model.game.loss.LossEvaluator;
import model.game.loss.LossRules;
import model.game.loss.MoveLossListener;
import model.game.loss.TimeLossListener;
import model.tile.Inaccessible;
import model.tile.Portal;
import model.tile.Tile;
import model.tile.TileVisitor;
import model.tile.mechanism.Door;
import model.tile.mechanism.DoorGroup;
import model.tile.mechanism.KeyTile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Builder + Creator (GRASP): collects the parsed cells and assembles the Level. */
public class LevelBuilder {
    private static final List<LevelRule> RULES = List.of(new BorderRule(), new OnePlayerRule(), new PortalPairRule(),
            new DoorKeyGroupRule());

    private static final TileVisitor<Optional<Door>> AS_DOOR = new TileVisitor<>() {
        @Override
        public Optional<Door> visitTile(Tile tile) {
            return Optional.empty();
        }

        @Override
        public Optional<Door> visitDoor(Door door) {
            return Optional.of(door);
        }
    };

    private static final TileVisitor<Optional<KeyTile>> AS_KEY_TILE = new TileVisitor<>() {
        @Override
        public Optional<KeyTile> visitTile(Tile tile) {
            return Optional.empty();
        }

        @Override
        public Optional<KeyTile> visitKeyTile(KeyTile keyTile) {
            return Optional.of(keyTile);
        }
    };

    private static final TileVisitor<Optional<Portal>> AS_PORTAL = new TileVisitor<>() {
        @Override
        public Optional<Portal> visitTile(Tile tile) {
            return Optional.empty();
        }

        @Override
        public Optional<Portal> visitPortal(Portal portal) {
            return Optional.of(portal);
        }
    };

    private static final OccupantVisitor<Optional<Player>> AS_PLAYER = new OccupantVisitor<>() {
        @Override
        public Optional<Player> visitPlayer(Player player) {
            return Optional.of(player);
        }

        @Override
        public Optional<Player> visitBox(Box box) {
            return Optional.empty();
        }
    };

    private final LossRules lossRules;
    private LevelHeader header;
    private final Map<Position, CellContent> contents = new LinkedHashMap<>();
    private int rows;
    private int cols;
    private int firstMapLine = 1;

    public LevelBuilder(LossRules lossRules) {
        this.lossRules = Objects.requireNonNull(lossRules, "lossRules");
    }

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
        Player player = null;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position p = new Position(row, col);
                CellContent content = contents.get(p);
                if (content == null) {
                    cells[row][col] = new Cell(p, new Inaccessible());
                    continue;
                }
                cells[row][col] = new Cell(p, content.tile());
                if (content.occupant() != null) {
                    cells[row][col].place(content.occupant());
                }
                Optional<Player> found = playerIn(content);
                if (found.isPresent()) {
                    player = found.get();
                    playerPosition = p;
                }
            }
        }
        Collection<DoorGroup> doorGroups = linkDoorGroups();
        linkPortals();
        Level level = new Level(header.name(), (int) header.timeLimit().toSeconds(), header.goals(),
                new Board(cells, player, playerPosition));
        doorGroups.forEach(group -> group.addReachabilityListener(level));
        subscribeLossListeners(level);
        return level;
    }

    private void subscribeLossListeners(Level level) {
        level.addBoardListener(new MoveLossListener(new LossEvaluator(level, lossRules.onPush(), level)));
        level.addTimeListener(new TimeLossListener(new LossEvaluator(level, lossRules.onTime(), level)));
    }

    /** Wires the Observer: every key tile notifies the door group of its letter. */
    private Collection<DoorGroup> linkDoorGroups() {
        Map<Character, DoorGroup> groups = new HashMap<>();
        for (CellContent content : contents.values()) {
            content.tile().accept(AS_DOOR)
                    .ifPresent(door -> groups.computeIfAbsent(door.getGroup(), DoorGroup::new).addDoor(door));
        }
        for (CellContent content : contents.values()) {
            content.tile().accept(AS_KEY_TILE)
                    .ifPresent(key -> key.addListener(groups.computeIfAbsent(key.getGroup(), DoorGroup::new)));
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

    /** Only called for positions that portalsByLetter returned, so the portal is always there. */
    private Portal portalAt(Position p) {
        return contents.get(p).tile().accept(AS_PORTAL).orElseThrow();
    }

    /** Portal positions grouped by letter, in reading order. Shared by the pair rule and the linking. */
    Map<Character, List<Position>> portalsByLetter() {
        return positionsByGroup(AS_PORTAL, Portal::getLetter);
    }

    /** Key tile positions grouped by group letter (upper case), in reading order. */
    Map<Character, List<Position>> keyTilesByGroup() {
        return positionsByGroup(AS_KEY_TILE, KeyTile::getGroup);
    }

    Map<Character, List<Position>> doorsByGroup() {
        return positionsByGroup(AS_DOOR, Door::getGroup);
    }

    private <T> Map<Character, List<Position>> positionsByGroup(TileVisitor<Optional<T>> as,
                                                               Function<T, Character> letterOf) {
        Map<Character, List<Position>> byLetter = new LinkedHashMap<>();
        for (Map.Entry<Position, CellContent> entry : contents.entrySet()) {
            entry.getValue().tile().accept(as).ifPresent(tile ->
                    byLetter.computeIfAbsent(letterOf.apply(tile), l -> new ArrayList<>()).add(entry.getKey()));
        }
        return byLetter;
    }

    /** The player at that content, if there is one (a padded cell has no content). */
    static Optional<Player> playerIn(CellContent content) {
        return content.occupant() == null ? Optional.empty() : content.occupant().accept(AS_PLAYER);
    }

    private void validate() throws InvalidLevelException {
        for (LevelRule rule : RULES) {
            rule.check(this);
        }
    }

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