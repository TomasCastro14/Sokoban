package model.board;

import model.box.Box;
import model.box.PushResult;
import model.tile.Tile;

/**
 * Information Expert (GRASP): the board knows every cell, so it validates
 * and executes the player's moves.
 */
public class Board {
    private final Cell[][] cells;
    private Position playerPosition;

    public Board(Cell[][] cells, Position playerPosition) {
        this.cells = cells;
        this.playerPosition = playerPosition;
    }

    public int getRows() {
        return cells.length;
    }

    public int getCols() {
        return cells.length == 0 ? 0 : cells[0].length;
    }

    public Cell cellAt(Position p) {
        return cells[p.row()][p.col()];
    }

    public boolean isInside(Position p) {
        return p.row() >= 0 && p.row() < getRows() && p.col() >= 0 && p.col() < getCols();
    }

    public Tile getTile(Position position) {
        return cellAt(position).getTile();
    }

    /** Returns the box at the given position (pushable or stored in its point), or null if there is none. */
    public Box getBoxAt(Position position) {
        Cell cell = cellAt(position);
        return cell.getOccupant() instanceof Box box ? box : cell.getTile().getStoredBox();
    }

    public void placeBox(Position position, Box box) {
        cellAt(position).place(box);
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    /** Number of boxes that can still be pushed (boxes already stored in a point are not occupants). */
    public int countBoxes() {
        int boxes = 0;
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (!cell.isFree() && cell.getOccupant().isPushable()) {
                    boxes++;
                }
            }
        }
        return boxes;
    }

    /**
     * Moves the player one cell, pushing the box in front if there is one.
     * A blocked move leaves the board untouched.
     */
    public MoveResult movePlayer(Direction d) {
        Position target = playerPosition.move(d);
        if (!isInside(target) || !getTile(target).allowsPlayer()) {
            return MoveResult.BLOCKED;
        }
        PushResult push = cellAt(target).isFree() ? null : pushBox(target, d);
        if (push == PushResult.BLOCKED) {
            return MoveResult.BLOCKED;
        }
        cellAt(target).place(cellAt(playerPosition).remove());
        playerPosition = target;
        if (push == null) {
            return MoveResult.OK;
        }
        return push == PushResult.BROKEN ? MoveResult.BROKEN : MoveResult.PUSHED;
    }

    /**
     * Pushes the box only if the next cell admits it and is free; never two boxes at once.
     * A broken box is removed by the board, the box never removes itself.
     */
    private PushResult pushBox(Position from, Direction d) {
        Cell fromCell = cellAt(from);
        Position to = from.move(d);
        if (!fromCell.getOccupant().isPushable() || !isInside(to)) {
            return PushResult.BLOCKED;
        }
        Box box = (Box) fromCell.getOccupant();
        Cell toCell = cellAt(to);
        if (!toCell.getTile().allowsBox(box) || !toCell.isFree()) {
            return PushResult.BLOCKED;
        }
        fromCell.remove();
        PushResult result = box.push();
        if (result != PushResult.BROKEN) {
            toCell.getTile().onBoxEntered(box);
            if (toCell.getTile().getStoredBox() != box) {
                toCell.place(box); // a stored box is absorbed by its point, it is no longer an occupant
            }
        }
        return result;
    }
}
