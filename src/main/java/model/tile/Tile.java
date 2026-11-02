package model.tile;

import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.Box;
import model.box.Occupant;
import model.tile.effect.EntryEffect;
import model.tile.effect.NoEffect;

import java.util.Set;


public abstract class Tile {
    private static final EntryEffect STAY = new NoEffect();

    public boolean allowsPlayer() {
        return true;
    }

    public boolean allowsBox(Box box) {
        return true;
    }

    public boolean acceptsEjectedBox() {
        return false;
    }


    public boolean isBoundary() {
        return false;
    }

    public boolean blocksVision() {
        return false;
    }

    
    public Set<Position> linkedPositions() {
        return Set.of();
    }


    public final Position arrivalFor(Occupant occupant, Position entered, Direction direction, Board board) {
        return entryEffect().apply(occupant, entered, direction, board);
    }


    protected EntryEffect entryEffect() {
        return STAY;
    }

    
    public void onBoxEntered(Box box) {
    }

    
    public boolean isConsumed() {
        return false;
    }

    
    public Box getStoredBox() {
        return null;
    }

    
    public StorageResult getStorageResult() {
        return null;
    }
}