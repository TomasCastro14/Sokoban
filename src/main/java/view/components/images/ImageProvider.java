package view.components.images;

import javafx.scene.image.Image;

import java.util.Optional;

/** Entrega imágenes compartidas a partir de su asset (rol FlyweightFactory). */
public interface ImageProvider {
    Image get(ImageAsset asset);

    Optional<Image> find(ImageAsset asset);
}
