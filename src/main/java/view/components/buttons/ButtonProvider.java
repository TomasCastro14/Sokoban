package view.components.buttons;

import javafx.scene.control.Button;

/**
 * Creates the game buttons. Every rectangular button and every round button is made in one
 * single way: color, icon and size are only parameters, never a new button class.
 */
public interface ButtonProvider {
    Button createRectangular(String text, ButtonStyle style, Runnable action);

    Button createRound(RoundStyle style, ButtonIcon icon, ButtonSize size, Runnable action);
}
