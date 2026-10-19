package model.box;

import model.board.Direction;

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
}
