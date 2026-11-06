package model.tile;

public class Inaccessible extends SolidTile {

    @Override
    public TileLook look() {
        return TileLook.of(TileKind.INACCESSIBLE);
    }
}