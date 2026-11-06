package view.gameplay.render;

import model.tile.TileLook;
import view.SpriteKey;

import java.util.List;

public final class InaccessibleRenderer implements TileRenderer {

    @Override
    public List<SpriteKey> layers(TileLook look) {
        return List.of();
    }

    @Override
    public boolean isInsideWarehouse() {
        return false;
    }
}