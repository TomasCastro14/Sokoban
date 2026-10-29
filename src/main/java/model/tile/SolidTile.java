package model.tile;

import model.box.Box;

/** A tile nothing can enter: neither the player nor a box. It also blocks vision and bounds the level. */
public abstract class SolidTile extends Tile {

    @Override
    public final boolean allowsPlayer() {
        return false;
    }

    @Override
    public final boolean allowsBox(Box box) {
        return false;
    }

    @Override
    public final boolean blocksVision() {
        return true;
    }

    @Override
    public final boolean isBoundary() {
        return true;
    }
}
