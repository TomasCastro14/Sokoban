package model.tile;

public class Wall extends SolidTile {

    @Override
    public <R> R accept(TileVisitor<R> visitor) {
        return visitor.visitWall(this);
    }
}