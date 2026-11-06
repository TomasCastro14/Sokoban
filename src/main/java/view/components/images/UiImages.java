package view.components.images;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class UiImages implements ImageProvider {
    private final ImageLoader loader;
    private final Map<String, Image> loaded = new HashMap<>();

    public UiImages(ImageLoader loader) {
        this.loader = Objects.requireNonNull(loader, "loader");
    }

    @Override
    public Image get(ImageAsset asset) {
        return loaded.computeIfAbsent(asset.path(), loader::load);
    }

    @Override
    public Optional<Image> find(ImageAsset asset) {
        return loader.exists(asset.path()) ? Optional.of(get(asset)) : Optional.empty();
    }
}