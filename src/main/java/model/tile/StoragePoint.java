package model.tile;

import model.box.Box;
import model.box.BoxType;

public class StoragePoint extends Tile {
    private final BoxType type;
    private StorageResult result;

    public StoragePoint(BoxType type) {
        this.type = type;
    }

    public BoxType getType() {
        return type;
    }

    public StorageResult receive(Box box) {
        return box.storeAt(this);
    }

    /** The box decides whether it can be stored here (Template Method in Box). */
    @Override
    public void onBoxEntered(Box box) {
        result = receive(box);
    }

    @Override
    public boolean holdsWrongBox() {
        return result == StorageResult.WRONG_BOX;
    }
}
