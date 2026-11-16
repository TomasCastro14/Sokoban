package view.components.images;

import javafx.scene.image.Image;

import java.net.URL;

public class ImageLoader {

    public Image load(String path) {
        URL url = resource(path);
        if (url == null) {
            throw new ImageLoadException(path);
        }
        Image image = new Image(url.toExternalForm());
        if (image.isError()) {
            throw new ImageLoadException(path, image.getException());
        }
        return image;
    }

    public boolean exists(String path) {
        return resource(path) != null;
    }

    private URL resource(String path) {
        return ImageLoader.class.getResource(path);
    }
}