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

    /** A key box opens the doors of the key tile it lands on, which also lets it be stored later. */
    @Override
    public boolean activateKeyTile() {
        markDoorOpened();
        return true;
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
