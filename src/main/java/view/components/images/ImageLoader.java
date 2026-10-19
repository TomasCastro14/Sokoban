package view.components.images;

import javafx.scene.image.Image;

import java.net.URL;

public class ImageLoader {

    public static Image load(String path) {
        URL url = ImageLoader.class.getResource(path);

        return url == null
                ? null
                : new Image(url.toExternalForm());
    }
}
