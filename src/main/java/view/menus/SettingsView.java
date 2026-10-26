package view.menus;

import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class SettingsView {

    private final StackPane root = new StackPane();

    public SettingsView(Runnable onClose) {

        StackPane content = new StackPane();

        // Construir aquí los controles de ajustes.
        // Por ejemplo: volumen, música y efectos.

        PopupLayout.install(root, content, onClose);
    }

    public Parent getRoot() {
        return root;
    }
}
