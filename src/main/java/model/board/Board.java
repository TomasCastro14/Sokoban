package model.board;

import model.box.Box;
import model.box.Occupant;
import model.box.Player;
import model.box.PushResult;
import model.tile.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Board {
    private final Cell[][] cells;
    private final List<Cell> cellsInReadingOrder;
    private final Player player;
    private Position playerPosition;

    public Board(Cell[][] cells, Player player, Position playerPosition) {
        this.cells = copyOf(Objects.requireNonNull(cells, "cells"));
        this.cellsInReadingOrder = readingOrder(this.cells);
        this.player = Objects.requireNonNull(player, "player");
        this.playerPosition = Objects.requireNonNull(playerPosition, "playerPosition");
        requireInside(playerPosition);
        if (cellAt(playerPosition).getOccupant() != player) {
            throw new IllegalArgumentException("The player is not standing on " + playerPosition);
        }
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

    public Cell cellAt(Position p) {
        requireInside(p);
        return cells[p.row()][p.col()];
    }

    public Cell neighbor(Position p, Direction d) {
        return cellAt(p.move(d));
    }

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

    public Box getBoxAt(Position position) {
        Cell cell = cellAt(position);
        return cell.isFree() ? cell.getTile().getStoredBox() : cell.getOccupant().boxIn(cell.getTile());
    }

    public void placeBox(Position position, Box box) {
        cellAt(position).place(box);
    }

    public Player getPlayer() {
        return player;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    public int countBoxes() {
        return (int) cells().stream().filter(cell -> !cell.isFree() && cell.getOccupant().isPushable()).count();
    }

    public MoveResult movePlayer(Direction d) {
        Position entered = playerPosition.move(d);
        if (!isInside(entered) || !player.canEnter(getTile(entered))) {
            return MoveResult.BLOCKED;
        }
        Position boxArrival = cellAt(entered).isFree() ? null : boxArrival(entered, d);
        if (!cellAt(entered).isFree() && boxArrival == null) {
            return MoveResult.BLOCKED;
        }
        Position arrival = playerArrival(entered, d, boxArrival);
        if (!isInside(arrival) || !canPlayerLand(arrival, entered, boxArrival)) {
            return MoveResult.BLOCKED;
        }
        if (!cellAt(arrival).isFree() && !arrival.equals(entered)) {
            shove(arrival);
        }
        PushResult push = boxArrival == null ? null : moveBox(entered, boxArrival);
        cellAt(playerPosition).remove();
        cellAt(arrival).place(player);
        playerPosition = arrival;
        if (push == null) {
            return MoveResult.OK;
        }
        return push == PushResult.BROKEN ? MoveResult.BROKEN : MoveResult.PUSHED;
    }

    private Position playerArrival(Position entered, Direction d, Position boxArrival) {
        if (boxArrival == null) {
            return getTile(entered).arrivalFor(player, entered, d, this);
        }
        Occupant box = cellAt(entered).remove();
        cellAt(boxArrival).place(box);
        try {
            return getTile(entered).arrivalFor(player, entered, d, this);
        } finally {
            cellAt(boxArrival).remove();
            cellAt(entered).place(box);
        }
    }

    private boolean canPlayerLand(Position arrival, Position entered, Position boxArrival) {
        boolean freeAfterPush = arrival.equals(entered) || cellAt(arrival).isFree() || canShove(arrival, boxArrival);
        return player.canEnter(getTile(arrival)) && freeAfterPush && !arrival.equals(boxArrival);
    }

    private boolean canShove(Position at, Position boxArrival) {
        Occupant occupant = cellAt(at).getOccupant();
        Position below = at.move(Direction.DOWN);
        return occupant.isPushable() && !below.equals(boxArrival)
                && canEnter(occupant, below) && getTile(below).acceptsEjectedBox();
    }

    private void shove(Position at) {
        cellAt(at.move(Direction.DOWN)).place(cellAt(at).remove());
    }

    private Position boxArrival(Position from, Direction d) {
        Occupant occupant = cellAt(from).getOccupant();
        Position next = from.move(d);
        if (!occupant.isPushable() || !canEnter(occupant, next)) {
            return null;
        }
        Position landing = getTile(next).arrivalFor(occupant, next, d, this);
        return canEnter(occupant, landing) ? landing : null;
    }

    public Position boxDestination(Direction d) {
        Position entered = playerPosition.move(d);
        if (!isInside(entered) || cellAt(entered).isFree()) {
            return null;
        }
        return boxArrival(entered, d);
    }

    public boolean canEnter(Occupant occupant, Position p) {
        if (!isInside(p) || !cellAt(p).isFree()) {
            return false;
        }
        return occupant.canEnter(getTile(p));
    }

    private PushResult moveBox(Position from, Position to) {
        Occupant box = cellAt(from).remove();
        Tile tile = getTile(to);
        PushResult result = box.pushOnto(tile);
        if (result != PushResult.BROKEN) {
            if (tile.getStoredBox() != box) {
                cellAt(to).place(box);
            }
        }
        return result;
    }
}