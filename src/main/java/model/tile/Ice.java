package model.tile;

import model.tile.effect.EntryEffect;
import model.tile.effect.SlideEffect;

public class Ice extends Tile {
    private static final EntryEffect SLIDE = new SlideEffect();

    @Override
    protected EntryEffect entryEffect() {
        return SLIDE;
    }

    @Override
    public TileLook look() {
        return TileLook.of(TileKind.ICE);
    }
}