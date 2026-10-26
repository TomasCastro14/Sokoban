package model.tile;

import model.box.Box;

/** A tile nothing can enter: neither the player nor a box. */
public abstract class SolidTile extends Tile {

    @Override
    public final boolean allowsPlayer() {
        return false;
    }

    @Override
    public final boolean allowsBox(Box box) {
        return false;
    }
}
