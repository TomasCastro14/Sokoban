package model.box;

import model.tile.StoragePoint;
import model.tile.StorageResult;

/**
 * Template Method: {@link #push()} and {@link #storeAt(StoragePoint)} are fixed
 * skeletons and each box only redefines their hooks. The board moves the box;
 * the box never removes itself, it reports BROKEN instead.
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

    public final StorageResult storeAt(StoragePoint point) {
        if (point.getType() != getType()) {
            return StorageResult.WRONG_TYPE;
        }
        return onStored(point);
    }

    /** Hook for subclasses with their own storage rule. By default a matching box is stored. */
    protected StorageResult onStored(StoragePoint point) {
        return StorageResult.STORED;
    }
}
