package view.components.buttons;

import javafx.scene.control.Button;

public final class ButtonFactory {

    private ButtonFactory() {}

    public static Button create(String text, String styleClass, Runnable action) {

        Button button = new Button(text);

        button.getStyleClass().addAll("menu-button", styleClass);

        button.setOnAction(e -> action.run());

        return button;
    }
}