package model.tile;

import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;

/**
 * Visitor: lets other layers (view, factory) act on each concrete tile without asking for its type.
 * Every visit defaults to visitTile, so a visitor only overrides the tiles it cares about.
 */
public interface TileVisitor<R> {

    /** What to do with any tile that has no specific visit overridden. */
    R visitTile(Tile tile);

    default R visitFloor(Floor floor) {
        return visitTile(floor);
    }

    default R visitWall(Wall wall) {
        return visitTile(wall);
    }

    default R visitInaccessible(Inaccessible inaccessible) {
        return visitTile(inaccessible);
    }

    default R visitIce(Ice ice) {
        return visitTile(ice);
    }

    default R visitStoragePoint(StoragePoint point) {
        return visitTile(point);
    }

    default R visitPortal(Portal portal) {
        return visitTile(portal);
    }

    default R visitDoor(Door door) {
        return visitTile(door);
    }

    default R visitKeyTile(KeyTile keyTile) {
        return visitTile(keyTile);
    }
}
