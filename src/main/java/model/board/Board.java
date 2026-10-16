package model.board;

import model.box.Box;
import model.tile.Tile;

import java.util.List;

public class Board {
    private final int rows;
    private final int cols;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Tile getTile(Position position) {
        throw new UnsupportedOperationException("TODO");
    }

    public void setTile(Position position, Tile tile) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Returns the box at the given position, or null if there is none. */
    public Box getBoxAt(Position position) {
        throw new UnsupportedOperationException("TODO");
    }

    public void placeBox(Position position, Box box) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Box> getBoxes() {
        throw new UnsupportedOperationException("TODO");
    }

    public Position getPlayerPosition() {
        throw new UnsupportedOperationException("TODO");
    }
}
