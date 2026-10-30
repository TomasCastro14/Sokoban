package view;

import javafx.scene.image.Image;
import view.components.images.ImageLoader;

import java.util.EnumMap;
import java.util.Map;

/**
 * Carga todos los sprites una sola vez, al construirse. Si falta alguno, el juego falla al
 * arrancar con la ruta del archivo, y no a mitad de una partida con un casillero invisible.
 */
public final class SpriteCatalog {
    private final Map<SpriteKey, Image> images = new EnumMap<>(SpriteKey.class);

    public SpriteCatalog() {
        for (SpriteKey key : SpriteKey.values()) {
            Image image = ImageLoader.load(key.path());   // ImageLoader devuelve null si no existe
            if (image == null) {
                throw new IllegalStateException("Falta el sprite " + key + ": " + key.path());
            }
            images.put(key, image);
        }
    }

    public Image get(SpriteKey key) {
        return images.get(key);
    }
}
