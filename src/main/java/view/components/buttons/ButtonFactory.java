package view.components.buttons;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import view.components.images.ImageProvider;

import java.util.Objects;

public final class ButtonFactory implements ButtonProvider {
    private static final String RECTANGULAR_BUTTON = "menu-button";
    private static final String ROUND_BUTTON = "button-round";
    private static final double MIRROR = -1;

    private final ImageProvider images;

    public ButtonFactory(ImageProvider images) {
        this.images = Objects.requireNonNull(images, "images");
    }

    @Override
    public Button createRectangular(String text, ButtonStyle style, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().addAll(RECTANGULAR_BUTTON, style.cssClass());
        onClick(button, action);
        return button;
    }

    /** Round buttons never take the focus, so the arrow keys keep reaching the game. */
    @Override
    public Button createRound(RoundStyle style, ButtonIcon icon, ButtonSize size, Runnable action) {
        Button button = new Button();
        button.setGraphic(iconView(icon, size));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.getStyleClass().addAll(ROUND_BUTTON, style.cssClass());
        button.setPrefSize(size.diameter(), size.diameter());
        button.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        button.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        button.setFocusTraversable(false);
        onClick(button, action);
        return button;
    }

    private ImageView iconView(ButtonIcon icon, ButtonSize size) {
        ImageView view = new ImageView(images.get(icon.image()));
        view.setFitWidth(icon.widthFor(size));
        view.setPreserveRatio(true);
        view.setRotate(icon.rotation());
        if (icon.mirrored()) {
            view.setScaleX(MIRROR);
        }
        return view;
    }

    private static void onClick(Button button, Runnable action) {
        Objects.requireNonNull(action, "action");
        button.setOnAction(event -> action.run());
    }
}
