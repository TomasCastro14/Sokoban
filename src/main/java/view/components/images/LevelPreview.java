package view.components.images;

public record LevelPreview(int levelNumber) implements ImageAsset {
    private static final String FOLDER = UiImage.ROOT + "level_previews/level";
    private static final String EXTENSION = ".png";

    @Override
    public String path() {
        return FOLDER + levelNumber + EXTENSION;
    }
}