package model.box;

public abstract class Box implements Occupant {

    public abstract BoxType getType();

    public abstract PushResult push();
}
