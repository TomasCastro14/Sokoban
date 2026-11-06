package model.tile;

public class Wall extends SolidTile {

    @Override
    public TileLook look() {
        return TileLook.of(TileKind.WALL);
    }
}