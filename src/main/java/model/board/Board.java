package model.board;

import model.box.Box;
import model.box.PushResult;
import model.tile.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Information Expert (GRASP): the board knows every cell, so it validates
 * and executes the player's moves.
 * Coordinates: Position(row, col), (0,0) is the top-left corner, rows grow down and columns grow right.
 * The grid is always a full rectangle (the builder pads short rows with void).
 */
public class Board {
    private final Cell[][] cells;
    private final List<Cell> cellsInReadingOrder;
    private Position playerPosition;

    /** @param cells a full rectangle of cells; it is copied, later changes to the array do not affect the board */
    public Board(Cell[][] cells, Position playerPosition) {
        this.cells = copyOf(Objects.requireNonNull(cells, "cells"));
        this.cellsInReadingOrder = readingOrder(this.cells);
        this.playerPosition = Objects.requireNonNull(playerPosition, "playerPosition");
        requireInside(playerPosition);
    }

    private static Cell[][] copyOf(Cell[][] cells) {
        if (cells.length == 0 || cells[0] == null || cells[0].length == 0) {
            throw new IllegalArgumentException("The board needs at least one cell");
        }
        int cols = cells[0].length;
        Cell[][] copy = new Cell[cells.length][];
        for (int row = 0; row < cells.length; row++) {
            if (cells[row] == null || cells[row].length != cols) {
                throw new IllegalArgumentException("Row " + row + " must have " + cols + " cells");
            }
            if (Arrays.asList(cells[row]).contains(null)) {
                throw new IllegalArgumentException("Row " + row + " has a missing cell");
            }
            copy[row] = cells[row].clone();
        }
        return copy;
    }

    private static List<Cell> readingOrder(Cell[][] cells) {
        List<Cell> all = new ArrayList<>();
        for (Cell[] row : cells) {
            all.addAll(Arrays.asList(row));
        }
        return Collections.unmodifiableList(all);
    }

    public int getRows() {
        return cells.length;
    }

    public int getCols() {
        return cells.length == 0 ? 0 : cells[0].length;
    }

    /** Out of the grid is a programming error: the border rule guarantees walls around every level. */
    public Cell cellAt(Position p) {
        requireInside(p);
        return cells[p.row()][p.col()];
    }

    /** The cell next to the given position in that direction. */
    public Cell neighbor(Position p, Direction d) {
        return cellAt(p.move(d));
    }

    /** Every cell once, in reading order (row by row, left to right). The list cannot be modified. */
    public List<Cell> cells() {
        return cellsInReadingOrder;
    }

    private void requireInside(Position p) {
        if (!isInside(p)) {
            throw new IllegalArgumentException(p + " is outside the " + getRows() + "x" + getCols() + " board");
        }
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
        return (int) cells().stream().filter(cell -> !cell.isFree() && cell.getOccupant().isPushable()).count();
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
