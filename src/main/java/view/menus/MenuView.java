package view.menus;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.components.buttons.ButtonFactory;
import view.components.buttons.ButtonStyle;

import java.util.Objects;

public class MenuView {
    private static final double BUTTON_SPACING = 16;
    private static final double TITLE_SPACING = 40;
    private static final String TITLE = "NEW SOKOBAN";

    private final StackPane root = new StackPane();

    public MenuView(MenuListener menuListener, ButtonFactory buttons) {
        Objects.requireNonNull(menuListener, "listener");
        Objects.requireNonNull(buttons, "buttons");

        Label title = new Label(TITLE);
        root.getStyleClass().add("menu-view");
        title.getStyleClass().add("title");

        VBox panel = new VBox(BUTTON_SPACING,
                buttons.create("CONTINUAR", ButtonStyle.PRIMARY, menuListener::onContinue),
                buttons.create("NIVELES", ButtonStyle.SECONDARY, menuListener::onLevels),
                buttons.create("NUEVA PARTIDA", ButtonStyle.SECONDARY, menuListener::onNewGame),
                buttons.create("AJUSTES", ButtonStyle.SECONDARY, menuListener::onSettings),
                buttons.create("SALIR", ButtonStyle.SECONDARY, menuListener::onExit));

        panel.getStyleClass().add("panel");
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        VBox column = new VBox(TITLE_SPACING, title, panel);
        column.setAlignment(Pos.CENTER);
        root.getChildren().add(column);
    }

    public Parent getRoot() { return root; }
}