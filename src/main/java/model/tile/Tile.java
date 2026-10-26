package model.tile;

import model.board.Position;
import model.box.Box;

import java.util.Set;

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

    /** True when the player cannot see past this tile; the reachable area stops here. */
    public boolean blocksVision() {
        return false;
    }

    /** Cells connected to this one besides its four neighbors (a portal's pair). None by default. */
    public Set<Position> linkedPositions() {
        return Set.of();
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
