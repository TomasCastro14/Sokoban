package view.components.buttons;

import javafx.scene.control.Button;

/** Crea los botones de la interfaz con el estilo del juego. */
public interface ButtonProvider {
    Button create(String text, ButtonStyle style, Runnable action);

    Button createArrow(ArrowDirection direction, Runnable action);

    Button createClose(Runnable action);
}
