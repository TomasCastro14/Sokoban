package model.box;

import model.history.Memento;
import model.tile.StoragePoint;
import model.tile.StorageResult;
import model.tile.Tile;

public abstract class Box implements Occupant {

    public abstract BoxType getType();

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public final boolean canEnter(Tile tile) {
        return tile.allowsBox(this);
    }

    @Override
    public final PushResult pushOnto(Tile tile) {
        PushResult result = push();
        if (result != PushResult.BROKEN) {
            tile.onBoxEntered(this);
        }
        return result;
    }

    @Override
    public final Box boxIn(Tile tile) {
        return this;
    }

    public final PushResult push() {
        return onPushed();
    }

    protected PushResult onPushed() {
        return PushResult.PUSHED;
    }

    public boolean activateKeyTile() {
        return false;
    }

    public Memento save() {
        return Memento.NOTHING_TO_RESTORE;
    }

    public final StorageResult storeAt(StoragePoint point) {
        if (point.getType() != getType()) {
            return StorageResult.WRONG_BOX;
        }
        return onStored(point);
    }

    protected StorageResult onStored(StoragePoint point) {
        return StorageResult.STORED;
    }
}