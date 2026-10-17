package model.box;

import model.tile.StoragePoint;
import model.tile.StorageResult;

public class KeyBox extends Box {
    private boolean doorOpened;

    public boolean hasOpenedDoor() {
        return doorOpened;
    }

    public void markDoorOpened() {
        this.doorOpened = true;
    }

    @Override
    public BoxType getType() {
        return BoxType.KEY;
    }

    /** A key box can only be stored after it has opened its door. */
    @Override
    protected StorageResult onStored(StoragePoint point) {
        return doorOpened ? StorageResult.STORED : StorageResult.KEY_NOT_USED;
    }
}
