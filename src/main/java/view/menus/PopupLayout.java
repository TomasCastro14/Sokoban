package view.menus;

import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import view.components.buttons.CloseButton;

public final class PopupLayout {

    private PopupLayout() {
        // Evita instanciar esta clase utilitaria.
    }

    public static void install(
            StackPane root,
            Parent content,
            Runnable onClose) {

        Region overlay = new Region();
        overlay.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.65);"
        );
        overlay.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        root.getChildren().addAll(overlay, content);

        CloseButton.install((StackPane) content, onClose);
    }
}