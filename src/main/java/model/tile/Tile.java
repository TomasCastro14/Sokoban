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
}
