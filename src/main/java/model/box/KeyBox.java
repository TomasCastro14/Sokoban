package model.box;

import model.history.Memento;
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
    public boolean activateKeyTile() {
        markDoorOpened();
        return true;
    }

    @Override
    public Memento save() {
        boolean savedDoorOpened = doorOpened;
        return () -> doorOpened = savedDoorOpened;
    }

    @Override
    public BoxType getType() {
        return BoxType.KEY;
    }

    @Override
    protected StorageResult onStored(StoragePoint point) {
        return doorOpened ? StorageResult.STORED : StorageResult.KEY_NOT_USED;
    }
}