package model.tile;

import model.box.Box;

public class Wall extends Tile {

    @Override
    public boolean allowsPlayer() {
        return false;
    }

    @Override
    public boolean allowsBox(Box box) {
        return false;
    }

    @Override
    public boolean blocksVision() {
        return true;
    }

    @Override
    public boolean isBoundary() {
        return true;
    }
}
