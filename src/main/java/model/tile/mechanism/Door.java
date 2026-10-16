package model.tile.mechanism;

import model.tile.Tile;

public class Door extends Tile {
    private final char group;
    private boolean open;

    public Door(char group) {
        this.group = Character.toUpperCase(group);
    }

    public char getGroup() {
        return group;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }
}
