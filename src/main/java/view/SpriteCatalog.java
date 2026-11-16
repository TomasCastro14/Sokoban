package view;

import javafx.scene.image.Image;
import view.components.images.ImageProvider;

import java.util.EnumMap;
import java.util.Map;

public final class SpriteCatalog {
    private final Map<SpriteKey, Image> images = new EnumMap<>(SpriteKey.class);

    public SpriteCatalog(ImageProvider imageProvider) {
        for (SpriteKey key : SpriteKey.values()) {
            images.put(key, imageProvider.get(key));
        }
    }

    public Image get(SpriteKey key) {
        return images.get(key);
    }
}