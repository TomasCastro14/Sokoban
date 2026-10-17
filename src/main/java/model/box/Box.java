package model.box;

public abstract class Box implements Occupant {

    public abstract BoxType getType();

    public abstract PushResult push();

    /** Whether the box may be stored right now. Boxes with their own storage rule override it. */
    public boolean canBeStored() {
        return true;
    }
}
