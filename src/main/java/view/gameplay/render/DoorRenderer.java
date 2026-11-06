package view.gameplay.render;

import model.tile.TileLook;
import view.SpriteKey;

import java.util.List;

public final class DoorRenderer implements TileRenderer {
    private static final List<SpriteKey> CLOSED = Layers.onFloor(SpriteKey.DOOR);

    @Override
    public List<SpriteKey> layers(TileLook look) {
        return look.active() ? Layers.FLOOR : CLOSED;
    }
}