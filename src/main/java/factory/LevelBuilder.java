package factory;

import exception.InvalidLevelException;
import model.board.Board;
import model.board.Cell;
import model.board.Position;
import model.box.Player;
import model.game.Level;
import model.game.loss.EarlyKeyStorageCondition;
import model.game.loss.WrongStorageCondition;
import model.tile.Void;
import model.tile.mechanism.Door;
import model.tile.mechanism.DoorGroup;
import model.tile.mechanism.KeyTile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Builder + Creator (GRASP): collects the parsed cells and assembles the Level. */
public class LevelBuilder {
    private String name;
    private int timeLimit;
    private int targetScore;
    private final Map<Position, CellContent> contents = new LinkedHashMap<>();
    private int rows;
    private int cols;

    public LevelBuilder withHeader(String name, int timeLimit, int targetScore) {
        this.name = name;
        this.timeLimit = timeLimit;
        this.targetScore = targetScore;
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
                    cells[row][col] = new Cell(p, new Void()); // short rows are padded with void
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
        linkDoorGroups();
        return new Level(name, timeLimit, targetScore, new Board(cells, playerPosition),
                List.of(new WrongStorageCondition(), new EarlyKeyStorageCondition()));
    }

    /** Wires the Observer: every key tile notifies the door group of its letter. */
    private void linkDoorGroups() {
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
    }

    private void validate() throws InvalidLevelException {
        long players = contents.values().stream().filter(c -> c.occupant() instanceof Player).count();
        if (players != 1) {
            throw new InvalidLevelException("The level must have exactly one player, found " + players, -1);
        }
    }
}
