package view.components.images;

public class ImageLoadException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ImageLoadException(String path) {
        super("No existe la imagen: " + path);
    }

    public ImageLoadException(String path, Throwable cause) {
        super("No se pudo cargar la imagen: " + path, cause);
    }
}