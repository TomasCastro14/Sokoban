package model.board;

import model.box.Box;
import model.box.Occupant;
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
     * Every check is done first (including where an entry effect such as a portal sends
     * the player or the box); only then is anything moved. A blocked move leaves the board untouched.
     */
    public MoveResult movePlayer(Direction d) {
        Position entered = playerPosition.move(d);
        if (!isInside(entered) || !getTile(entered).allowsPlayer()) {
            return MoveResult.BLOCKED;
        }
        Position arrival = getTile(entered).arrivalFor(cellAt(playerPosition).getOccupant(), entered, d, this);
        Position boxArrival = cellAt(entered).isFree() ? null : boxArrival(entered, d);
        if (!cellAt(entered).isFree() && boxArrival == null) {
            return MoveResult.BLOCKED;
        }
        if (!canPlayerLand(arrival, entered, boxArrival)) {
            return MoveResult.BLOCKED;
        }
        PushResult push = boxArrival == null ? null : moveBox(entered, boxArrival);
        cellAt(arrival).place(cellAt(playerPosition).remove());
        playerPosition = arrival;
        if (push == null) {
            return MoveResult.OK;
        }
        return push == PushResult.BROKEN ? MoveResult.BROKEN : MoveResult.PUSHED;
    }

    /**
     * The player lands where the entry effect sent it if that cell admits it and is free.
     * The entered cell counts as free because its box is pushed away first.
     */
    private boolean canPlayerLand(Position arrival, Position entered, Position boxArrival) {
        boolean freeAfterPush = arrival.equals(entered) || cellAt(arrival).isFree();
        return getTile(arrival).allowsPlayer() && freeAfterPush && !arrival.equals(boxArrival);
    }

    /**
     * Where the box at the given position would end up, or null if it cannot be pushed:
     * the next cell must admit it and be free, and so must the cell an entry effect sends it to.
     * Never two boxes at once.
     */
    private Position boxArrival(Position from, Direction d) {
        Occupant occupant = cellAt(from).getOccupant();
        Position next = from.move(d);
        if (!occupant.isPushable() || !canEnter(occupant, next)) {
            return null;
        }
        Position landing = getTile(next).arrivalFor(occupant, next, d, this);
        return canEnter(occupant, landing) ? landing : null;
    }

    /**
     * Whether the occupant could step onto that cell: it is inside the board, its tile admits
     * this kind of occupant and nobody is standing there. Used by entry effects such as ice.
     */
    public boolean canEnter(Occupant occupant, Position p) {
        if (!isInside(p) || !cellAt(p).isFree()) {
            return false;
        }
        Tile tile = getTile(p);
        return occupant.isPushable() ? tile.allowsBox((Box) occupant) : tile.allowsPlayer();
    }

    /**
     * Moves an already validated box. A broken box is removed by the board,
     * the box never removes itself; a stored box is absorbed by its point.
     */
    private PushResult moveBox(Position from, Position to) {
        Box box = (Box) cellAt(from).remove();
        PushResult result = box.push();
        if (result != PushResult.BROKEN) {
            Tile tile = getTile(to);
            tile.onBoxEntered(box);
            if (tile.getStoredBox() != box) {
                cellAt(to).place(box);
            }
        }
        return result;
    }
}
