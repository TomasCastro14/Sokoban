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

    /** Called by the board once a pushed box finishes moving onto this tile. */
    public void onBoxEntered(Box box) {
    }

    /** True while this tile holds a box it does not accept (wrong storage). */
    public boolean holdsWrongBox() {
        return false;
    }
}
