package model.box;

/**
 * Template Method: {@link #push()} is the fixed skeleton and each box only
 * redefines {@link #onPushed()}. The board moves the box; the box never
 * removes itself, it reports BROKEN instead.
 */
public abstract class Box implements Occupant {

    public abstract BoxType getType();

    public final PushResult push() {
        return onPushed();
    }

    /** Hook for subclasses. A box without extra behaviour is simply pushed. */
    protected PushResult onPushed() {
        return PushResult.PUSHED;
    }
}
