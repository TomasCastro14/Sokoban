package model.tile;

import model.box.Box;

/**
 * Polymorphism (GRASP): each tile answers what it lets in, so the board
 * never asks for the concrete tile type.
 */
public abstract class Tile {

    public boolean allowsPlayer() {
        return true;
    }

    public boolean allowsBox(Box box) {
        return true;
    }

    /** True for tiles that close the map, so they can sit on its border. */
    public boolean isBoundary() {
        return false;
    }

    /** Called by the board once a pushed box finishes moving onto this tile. */
    public void onBoxEntered(Box box) {
    }

    /** Returns the box this tile absorbed (a filled storage point), or null if there is none. */
    public Box getStoredBox() {
        return null;
    }

    /** Result of the last box that entered this tile to be stored, or null if it does not store boxes. */
    public StorageResult getStorageResult() {
        return null;
    }
}
