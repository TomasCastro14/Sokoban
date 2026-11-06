package view.components.images;

public enum UiImage implements ImageAsset {
    STAR_ON("Items/star.png"),
    STAR_OFF("Items/star_outline.png"),
    ARROW("buttons/rounded_gold/arrow_decorative_e.png"),
    CLOSE_ROUND("buttons/rounded_close/button_round_border.png"),
    CLOSE_SQUARE("buttons/rounded_close/button_square_border.png"),
    CLOSE_CROSS("buttons/rounded_close/icon_cross.png");

    static final String ROOT = "/assets/images/UI/";

    private final String path;

    UiImage(String file) {
        this.path = ROOT + file;
    }

    @Override
    public String path() {
        return path;
    }
}