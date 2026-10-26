package model.tile;

import model.tile.effect.EntryEffect;
import model.tile.effect.SlideEffect;

/** Admits the player and boxes like floor; whoever enters slides in the same direction (SlideEffect). */
public class Ice extends Tile {
    private static final EntryEffect SLIDE = new SlideEffect();

    @Override
    protected EntryEffect entryEffect() {
        return SLIDE;
    }
}
