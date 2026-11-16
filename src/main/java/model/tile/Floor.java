package model.tile;

public class Floor extends Tile {

    @Override
    public boolean acceptsEjectedBox() {
        return true;
    }

    @Override
    public <R> R accept(TileVisitor<R> visitor) {
        return visitor.visitFloor(this);
    }
}