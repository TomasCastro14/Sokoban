package model.box;

import model.board.Direction;
import model.tile.Tile;

public class Player implements Occupant {
    private final Direction facing;

    public Player(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return facing;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canEnter(Tile tile) {
        return tile.allowsPlayer();
    }

    
    @Override
    public PushResult pushOnto(Tile tile) {
        return PushResult.BLOCKED;
    }

    
    @Override
    public Box boxIn(Tile tile) {
        return tile.getStoredBox();
    }
}