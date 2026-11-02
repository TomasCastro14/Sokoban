package view.components.container;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import view.components.buttons.ButtonFactory;

import java.util.Objects;

public final class PopupLayout {
    private static final String OVERLAY_STYLE = "popup-overlay";
    private static final double CLOSE_BUTTON_PEEK = 44;
    private static final double CLOSE_BUTTON_RIGHT_INSET = 32;

    private final ButtonFactory buttons;

    public PopupLayout(ButtonFactory buttons) {
        this.buttons = Objects.requireNonNull(buttons, "buttons");
    }

    public void install(StackPane root, StackPane content, Runnable onClose) {
        root.getChildren().addAll(overlay(), content);
        content.getChildren().add(0, closeButton(onClose));
    }

    private static Region overlay() {
        Region overlay = new Region();
        overlay.getStyleClass().add(OVERLAY_STYLE);
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return overlay;
    }

    private Button closeButton(Runnable onClose) {
        Button close = buttons.createClose(onClose);
        StackPane.setAlignment(close, Pos.TOP_RIGHT);
        StackPane.setMargin(close, new Insets(-CLOSE_BUTTON_PEEK, CLOSE_BUTTON_RIGHT_INSET, 0, 0));
        return close;
    }
}