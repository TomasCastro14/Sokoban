package view.gameplay.render;

import model.tile.TileLook;
import view.SpriteKey;

import java.util.List;

public final class PortalRenderer implements TileRenderer {

    @Override
    public List<SpriteKey> layers(TileLook look) {
        return Layers.onFloor(SpriteKey.PORTAL);
    }
}