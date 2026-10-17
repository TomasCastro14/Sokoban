package model.board;

import model.box.Occupant;
import model.tile.Tile;

public class Cell {
    private final Position position;
    private final Tile tile;
    private Occupant occupant;

    public Cell(Position position, Tile tile) {
        this.position = position;
        this.tile = tile;
    }

    public Position getPosition() {
        return position;
    }

    public Tile getTile() {
        return tile;
    }

    /** Returns the occupant of the cell, or null if it is free. */
    public Occupant getOccupant() {
        return occupant;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public void place(Occupant o) {
        this.occupant = o;
    }

    /** Takes the occupant out of the cell and returns it. */
    public Occupant remove() {
        Occupant removed = occupant;
        occupant = null;
        return removed;
    }
}
