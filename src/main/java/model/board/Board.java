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

    /** Returns the box at the given position, or null if there is none. */
    public Box getBoxAt(Position position) {
        return cellAt(position).getOccupant() instanceof Box box ? box : null;
    }

    public void placeBox(Position position, Box box) {
        cellAt(position).place(box);
    }

    public Position getPlayerPosition() {
        return playerPosition;
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
        return push == PushResult.BROKEN ? MoveResult.BROKEN : MoveResult.OK;
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
            toCell.place(box);
            toCell.getTile().onBoxEntered(box);
        }
        return result;
    }
}
