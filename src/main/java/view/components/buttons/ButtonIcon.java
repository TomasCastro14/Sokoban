package view.components.buttons;

import view.components.images.UiImage;

/**
 * The drawing on top of a round button. Several icons share one image and only turn it
 * (BACK is PLAY facing left), so no extra assets are needed.
 */
public enum ButtonIcon {
    PLAY(UiImage.ICON_PLAY, 0, 0.40),
    BACK(UiImage.ICON_PLAY, 180, 0.40),
    REPLAY(UiImage.ICON_REPLAY, 0, 0.50),
    CLOSE(UiImage.ICON_CLOSE, 0, 0.42),
    ARROW_LEFT(UiImage.ICON_ARROW, 180, 0.45),
    ARROW_RIGHT(UiImage.ICON_ARROW, 0, 0.45);

    private final UiImage image;
    private final double rotation;
    private final double widthRatio;

    ButtonIcon(UiImage image, double rotation, double widthRatio) {
        this.image = image;
        this.rotation = rotation;
        this.widthRatio = widthRatio;
    }

    UiImage image() {
        return image;
    }

    double rotation() {
        return rotation;
    }

    double widthFor(ButtonSize size) {
        return size.diameter() * widthRatio;
    }
}
