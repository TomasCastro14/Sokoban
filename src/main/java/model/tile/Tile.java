package model.tile;

import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.Box;
import model.box.Occupant;
import model.tile.effect.EntryEffect;
import model.tile.effect.NoEffect;

import java.util.Set;

/**
 * Polymorphism (GRASP): each tile answers what it lets in, so the board
 * never asks for the concrete tile type.
 */
public abstract class Tile {
    private static final EntryEffect STAY = new NoEffect();

    public boolean allowsPlayer() {
        return true;
    }

    public boolean allowsBox(Box box) {
        return true;
    }

    /** True for tiles that close the map, so they can sit on its border. */
    public boolean isBoundary() {
        return false;
    }

    /** True when the player cannot see past this tile; the reachable area stops here. */
    public boolean blocksVision() {
        return false;
    }

    /** Cells connected to this one besides its four neighbors (a portal's pair). None by default. */
    public Set<Position> linkedPositions() {
        return Set.of();
    }

    /** Where an occupant ends up after entering this tile; the tile's EntryEffect decides. */
    public final Position arrivalFor(Occupant occupant, Position entered, Direction direction, Board board) {
        return entryEffect().apply(occupant, entered, direction, board);
    }

    /** Strategy used by arrivalFor. By default the occupant stays where it entered. */
    protected EntryEffect entryEffect() {
        return STAY;
    }

    /** Called by the board once a pushed box finishes moving onto this tile. */
    public void onBoxEntered(Box box) {
    }

    /** True once the tile has done its job and the board must leave plain floor in its place. */
    public boolean isConsumed() {
        return false;
    }

    /** Returns the box this tile absorbed (a filled storage point), or null if there is none. */
    public Box getStoredBox() {
        return null;
    }

    /** Result of the last box that entered this tile to be stored, or null if it does not store boxes. */
    public StorageResult getStorageResult() {
        return null;
    }
}
