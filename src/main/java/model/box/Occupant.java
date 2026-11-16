package model.box;
import model.tile.Tile;


public interface Occupant {
    boolean isPushable();
    boolean canEnter(Tile tile);
    PushResult pushOnto(Tile tile);
    Box boxIn(Tile tile);

    /** Double dispatch: the player and each kind of box call their own visit method. */
    <R> R accept(OccupantVisitor<R> visitor);
}