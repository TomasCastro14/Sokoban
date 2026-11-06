package view.gameplay.render;

import model.tile.TileLook;
import view.SpriteKey;

import java.util.List;

public final class KeyTileRenderer implements TileRenderer {
    private static final List<SpriteKey> UNUSED = Layers.onFloor(SpriteKey.KEY_ITEM);

    @Override
    public List<SpriteKey> layers(TileLook look) {
        return look.active() ? Layers.FLOOR : UNUSED;
    }
}