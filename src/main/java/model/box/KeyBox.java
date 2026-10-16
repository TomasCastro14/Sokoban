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

    @Override
    public PushResult push() {
        throw new UnsupportedOperationException("TODO");
    }
}
