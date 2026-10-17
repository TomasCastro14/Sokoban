package model.box;

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
    public boolean canBeStored() {
        return doorOpened;
    }

    @Override
    public PushResult push() {
        throw new UnsupportedOperationException("TODO");
    }
}
