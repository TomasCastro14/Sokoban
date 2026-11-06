package view.components.container;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import view.components.buttons.ButtonIcon;
import view.components.buttons.ButtonProvider;
import view.components.buttons.ButtonSize;
import view.components.buttons.RoundStyle;

import java.util.Objects;

public final class PopupLayout implements PopupInstaller {
    /** How far the close button sticks out of the popup's top-right corner. */
    private static final double CLOSE_BUTTON_OVERHANG = 20;

    private final ButtonProvider buttons;

    public PopupLayout(ButtonProvider buttons) {
        this.buttons = Objects.requireNonNull(buttons, "buttons");
    }

    @Override
    public void install(StackPane root, StackPane content, Runnable onClose) {
        content.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        content.getChildren().add(closeButton(onClose));
        root.getChildren().addAll(Overlay.create(), content);
    }

    private Button closeButton(Runnable onClose) {
        Button close = buttons.createRound(RoundStyle.RED, ButtonIcon.CLOSE, ButtonSize.SMALL, onClose);
        StackPane.setAlignment(close, Pos.TOP_RIGHT);
        StackPane.setMargin(close, new Insets(-CLOSE_BUTTON_OVERHANG, -CLOSE_BUTTON_OVERHANG, 0, 0));
        return close;
    }
}
