package model.tile;

import model.box.Box;
import model.box.BoxType;
import model.history.Memento;

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

    @Override
    public boolean allowsPlayer() {
        return isFilled();
    }

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
    public TileLook look() {
        return new TileLook(TileKind.STORAGE, type, isFilled());
    }

    @Override
    public Memento save() {
        Box savedBox = storedBox;
        StorageResult savedResult = result;
        return () -> {
            storedBox = savedBox;
            result = savedResult;
        };
    }

    @Override
    public Box getStoredBox() {
        return storedBox;
    }

    @Override
    public StorageResult getStorageResult() {
        return result;
    }
}