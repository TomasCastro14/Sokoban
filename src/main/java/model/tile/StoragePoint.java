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
        if (box.getType() != type) {
            return StorageResult.WRONG_TYPE;
        }
        if (!box.canBeStored()) {
            return StorageResult.EARLY_KEY;
        }
        return StorageResult.STORED;
    }
}
