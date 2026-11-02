package view;

import javafx.scene.image.Image;
import view.components.images.UiImages;

import java.util.EnumMap;
import java.util.Map;

public final class SpriteCatalog {
    private final Map<SpriteKey, Image> images = new EnumMap<>(SpriteKey.class);

    public SpriteCatalog(UiImages uiImages) {
        for (SpriteKey key : SpriteKey.values()) {
            images.put(key, uiImages.get(key));
        }
    }

    public Image get(SpriteKey key) {
        return images.get(key);
    }
}