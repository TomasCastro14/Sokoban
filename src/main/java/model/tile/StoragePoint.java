package model.tile;

import model.box.Box;
import model.box.BoxType;

public class StoragePoint extends Tile {
    private final BoxType type;
    private StorageResult result;
    private Box storedBox;

    public StoragePoint(BoxType type) {
        this.type = type;
    }

    public BoxType getType() {
        return type;
    }

    public StorageResult receive(Box box) {
        return box.storeAt(this);
    }

    public boolean isFilled() {
        return storedBox != null;
    }

    /** An empty point is a hole; once filled it works as a bridge. */
    @Override
    public boolean allowsPlayer() {
        return isFilled();
    }

    /**
     * The box decides whether it can be stored here (Template Method in Box).
     * A stored box is absorbed by the point and stays fixed; a filled point behaves like floor.
     */
    @Override
    public void onBoxEntered(Box box) {
        if (isFilled()) {
            return;
        }
        result = receive(box);
        if (result == StorageResult.STORED) {
            storedBox = box;
        }
    }

    @Override
    public Box getStoredBox() {
        return storedBox;
    }

    @Override
    public boolean holdsWrongBox() {
        return result == StorageResult.WRONG_BOX;
    }
}
