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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Builder + Creator (GRASP): collects the parsed cells and assembles the Level. */
public class LevelBuilder {
    private LevelHeader header;
    private final Map<Position, CellContent> contents = new LinkedHashMap<>();
    private int rows;
    private int cols;

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
        return new Level(header.name(), (int) header.timeLimit().toSeconds(), header.targetScore(), new Board(cells, playerPosition),
                List.of(new WrongStorageCondition(), new EarlyKeyStorageCondition()));
    }

    private void validate() throws InvalidLevelException {
        long players = contents.values().stream().filter(c -> c.occupant() instanceof Player).count();
        if (players != 1) {
            throw new InvalidLevelException("The level must have exactly one player, found " + players, -1);
        }
    }
}
