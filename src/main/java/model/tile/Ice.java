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
    public <R> R accept(TileVisitor<R> visitor) {
        return visitor.visitIce(this);
    }
}