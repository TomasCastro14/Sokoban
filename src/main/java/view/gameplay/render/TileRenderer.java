package view.gameplay.render;

import model.tile.TileLook;
import view.SpriteKey;

import java.util.List;

public interface TileRenderer {

    List<SpriteKey> layers(TileLook look);

    default boolean isInsideWarehouse() {
        return true;
    }
}