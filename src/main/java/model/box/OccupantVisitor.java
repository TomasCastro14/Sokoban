package model.box;

/** Visitor: lets other layers act on the player or on a box without asking for its type. */
public interface OccupantVisitor<R> {

    R visitPlayer(Player player);

    R visitBox(Box box);

    /** Weak boxes are boxes too: unless overridden, they are visited like any other box. */
    default R visitWeakBox(WeakBox box) {
        return visitBox(box);
    }
}
