package model.tile.mechanism;

import model.tile.Tile;

public class KeyTile extends Tile {
    private final char group;

    public KeyTile(char group) {
        this.group = Character.toUpperCase(group);
    }

    /** Group letter in upper case: key tile 'a' opens doors 'A'. */
    public char getGroup() {
        return group;
    }
}
