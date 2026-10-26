package view.menus;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.components.buttons.ButtonFactory;

public class MenuView {
    private final StackPane root = new StackPane();

    public MenuView(Runnable onWork, Runnable onLevels, Runnable onNewGame, Runnable onExit) {
        Label title = new Label("NEW SOKOBAN");
        title.getStyleClass().add("title");

        Button continuar;
        Button niveles;
        Button nuevaPartida;
        Button ajustes;
        Button salir;
        VBox panel = new VBox(16,
                    continuar = ButtonFactory.create("CONTINUAR", "button-primary", onWork),
                    niveles = ButtonFactory.create("NIVELES", "button-secondary", onLevels),
                    nuevaPartida = ButtonFactory.create("NUEVA PARTIDA", "button-secondary", onNewGame),
                    ajustes = ButtonFactory.create("AJUSTES", "button-secondary", onNewGame),
                    salir = ButtonFactory.create("SALIR", "button-secondary", onExit)
                );

        panel.getStyleClass().add("panel");
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        VBox column = new VBox(40, title, panel);
        column.setAlignment(Pos.CENTER);
        root.getChildren().add(column);
    }

    public Parent getRoot() { return root; }
}