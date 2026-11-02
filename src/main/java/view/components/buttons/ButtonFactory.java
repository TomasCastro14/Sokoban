package view.components.buttons;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import view.components.images.UiImage;
import view.components.images.UiImages;

import java.util.Objects;

public final class ButtonFactory {
    private static final String MENU_BUTTON = "menu-button";
    private static final String ROUND_BUTTON = "button-round";
    private static final double ARROW_WIDTH = 32;

    private final UiImages images;

    public ButtonFactory(UiImages images) {
        this.images = Objects.requireNonNull(images, "images");
    }

    public Button create(String text, ButtonStyle style, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().addAll(MENU_BUTTON, style.cssClass());
        onClick(button, action);
        return button;
    }

    public Button createArrow(ArrowDirection direction, Runnable action) {
        ImageView arrow = new ImageView(images.get(UiImage.ARROW));
        arrow.setFitWidth(ARROW_WIDTH);
        arrow.setPreserveRatio(true);
        arrow.setRotate(direction.rotation());
        Button button = new Button();
        button.setGraphic(arrow);
        button.getStyleClass().add(ROUND_BUTTON);
        onClick(button, action);
        return button;
    }

    public Button createClose(Runnable action) {
        Button button = new CloseButton(images);
        onClick(button, action);
        return button;
    }

    private static void onClick(Button button, Runnable action) {
        button.setOnAction(event -> action.run());
    }
}