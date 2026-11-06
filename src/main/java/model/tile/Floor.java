package model.tile;

public class Floor extends Tile {

    @Override
    public boolean acceptsEjectedBox() {
        return true;
    }

    @Override
    public TileLook look() {
        return TileLook.of(TileKind.FLOOR);
    }
}