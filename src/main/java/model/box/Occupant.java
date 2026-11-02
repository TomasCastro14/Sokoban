package model.box;
import model.tile.Tile;


public interface Occupant {
    boolean isPushable();
    boolean canEnter(Tile tile);
    PushResult pushOnto(Tile tile);
    Box boxIn(Tile tile);
}