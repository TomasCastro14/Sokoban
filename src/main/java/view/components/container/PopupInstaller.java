package view.components.container;

import javafx.scene.layout.StackPane;

/** Monta un contenido como popup sobre una pantalla, con overlay y botón de cierre. */
public interface PopupInstaller {
    void install(StackPane root, StackPane content, Runnable onClose);
}
