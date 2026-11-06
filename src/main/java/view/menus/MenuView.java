package view.menus;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.components.buttons.ButtonProvider;
import view.components.buttons.ButtonStyle;

import java.util.Objects;

public class MenuView {
    private static final double BUTTON_SPACING = 16;
    private static final double TITLE_SPACING = 40;
    private static final String TITLE = "NEW SOKOBAN";
    private static final String CONTINUE_TEXT = "CONTINUAR";
    private static final String LEVELS_TEXT = "NIVELES";
    private static final String NEW_GAME_TEXT = "NUEVA PARTIDA";
    private static final String SETTINGS_TEXT = "AJUSTES";
    private static final String EXIT_TEXT = "SALIR";
    private static final String ROOT_STYLE = "menu-view";
    private static final String TITLE_STYLE = "title";
    private static final String PANEL_STYLE = "panel";

    private final StackPane root = new StackPane();

    public MenuView(MenuListener menuListener, ButtonProvider buttons) {
        Objects.requireNonNull(menuListener, "listener");
        Objects.requireNonNull(buttons, "buttons");

        Label title = new Label(TITLE);
        root.getStyleClass().add(ROOT_STYLE);
        title.getStyleClass().add(TITLE_STYLE);

        VBox panel = new VBox(BUTTON_SPACING,
                buttons.create(CONTINUE_TEXT, ButtonStyle.PRIMARY, menuListener::onContinue),
                buttons.create(LEVELS_TEXT, ButtonStyle.SECONDARY, menuListener::onLevels),
                buttons.create(NEW_GAME_TEXT, ButtonStyle.SECONDARY, menuListener::onNewGame),
                buttons.create(SETTINGS_TEXT, ButtonStyle.SECONDARY, menuListener::onSettings),
                buttons.create(EXIT_TEXT, ButtonStyle.SECONDARY, menuListener::onExit));

        panel.getStyleClass().add(PANEL_STYLE);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        VBox column = new VBox(TITLE_SPACING, title, panel);
        column.setAlignment(Pos.CENTER);
        root.getChildren().add(column);
    }

    public Parent getRoot() { return root; }
}