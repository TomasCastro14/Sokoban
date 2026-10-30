package view.menus;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.components.buttons.ButtonFactory;

import java.util.Objects;

public class MenuView {
    private final StackPane root = new StackPane();

    public MenuView(MenuListener menuListener) {
        Objects.requireNonNull(menuListener, "listener");

        Label title = new Label("NEW SOKOBAN");
        root.getStyleClass().add("menu-view");
        title.getStyleClass().add("title");

        VBox panel = new VBox(16,
                ButtonFactory.create("CONTINUAR", "button-primary", menuListener::onContinue),
                ButtonFactory.create("NIVELES", "button-secondary", menuListener::onLevels),
                ButtonFactory.create("NUEVA PARTIDA", "button-secondary", menuListener::onNewGame),
                ButtonFactory.create("AJUSTES", "button-secondary", menuListener::onSettings),
                ButtonFactory.create("SALIR", "button-secondary", menuListener::onExit));

        panel.getStyleClass().add("panel");
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        VBox column = new VBox(40, title, panel);
        column.setAlignment(Pos.CENTER);
        root.getChildren().add(column);
    }

    public Parent getRoot() { return root; }
}