package model.tile;

import model.box.Box;
import model.box.BoxType;

public class StoragePoint extends Tile {
    private final BoxType type;

    public StoragePoint(BoxType type) {
        this.type = type;
    }

    public BoxType getType() {
        return type;
    }

    public StorageResult receive(Box box) {
        return box.storeAt(this);
    }
}
