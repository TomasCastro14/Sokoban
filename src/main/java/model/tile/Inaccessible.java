package model.tile;

public class Inaccessible extends SolidTile {

    @Override
    public <R> R accept(TileVisitor<R> visitor) {
        return visitor.visitInaccessible(this);
    }
}