package view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MenuView {
    private final StackPane root = new StackPane();

    public MenuView(Runnable onWork, Runnable onLevels, Runnable onNewGame, Runnable onExit) {
        Label title = new Label("NEW SOKOBAN");
        title.getStyleClass().add("title");



        VBox panel = new VBox(16,
                button("CONTINUAR", "button-primary", onWork),
                button("NIVELES", "button-secondary", onLevels),
                button("NUEVA PARTIDA", "button-secondary", onNewGame),
                button("SALIR", "button-secondary", onExit));
        panel.getStyleClass().add("panel");
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        VBox column = new VBox(40, title, panel);
        column.setAlignment(Pos.CENTER);
        root.getChildren().add(column);
    }

    private Button button(String text, String styleClass, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().addAll("menu-button", styleClass);
        b.setOnAction(e -> action.run());
        return b;
    }

    public Parent getRoot() { return root; }
}