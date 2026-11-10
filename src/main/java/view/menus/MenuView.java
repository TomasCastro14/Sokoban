package view.menus;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.components.buttons.ButtonProvider;
import view.components.buttons.ButtonStyle;
import view.components.images.ImageProvider;
import view.components.images.UiImage;

import java.util.Objects;

public class MenuView {
    private static final double BUTTON_SPACING = 16;
    private static final double TITLE_SPACING = 40;
    private static final double TITLE_LINE_SPACING = 10;
    private static final double TITLE_TOP_WIDTH = 200;
    private static final double TITLE_BOTTOM_WIDTH = 575;
    private static final String CONTINUE_TEXT = "CONTINUAR";
    private static final String LEVELS_TEXT = "NIVELES";
    private static final String SETTINGS_TEXT = "AJUSTES";
    private static final String EXIT_TEXT = "SALIR";
    private static final String ROOT_STYLE = "menu-view";
    private static final String PANEL_STYLE = "panel";

    private final StackPane root = new StackPane();

    public MenuView(MenuListener menuListener, ButtonProvider buttons, ImageProvider images) {
        Objects.requireNonNull(menuListener, "listener");
        Objects.requireNonNull(buttons, "buttons");
        Objects.requireNonNull(images, "images");

        root.getStyleClass().add(ROOT_STYLE);

        VBox panel = new VBox(BUTTON_SPACING,
                buttons.createRectangular(CONTINUE_TEXT, ButtonStyle.PRIMARY, menuListener::onContinue),
                buttons.createRectangular(LEVELS_TEXT, ButtonStyle.SECONDARY, menuListener::onLevels),
                buttons.createRectangular(SETTINGS_TEXT, ButtonStyle.SECONDARY, menuListener::onSettings),
                buttons.createRectangular(EXIT_TEXT, ButtonStyle.SECONDARY, menuListener::onExit));

        panel.getStyleClass().add(PANEL_STYLE);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        VBox column = new VBox(TITLE_SPACING, title(images), panel);
        column.setAlignment(Pos.CENTER);
        root.getChildren().add(column);
    }

    /** "The New" arriba y "SOKOBAN!" abajo, alineados a la izquierda entre sí. */
    private static VBox title(ImageProvider images) {
        VBox title = new VBox(TITLE_LINE_SPACING,
                titleLine(images, UiImage.TITLE_TOP, TITLE_TOP_WIDTH),
                titleLine(images, UiImage.TITLE_BOTTOM, TITLE_BOTTOM_WIDTH));
        title.setAlignment(Pos.CENTER_LEFT);
        title.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return title;
    }

    private static ImageView titleLine(ImageProvider images, UiImage asset, double width) {
        ImageView line = new ImageView(images.get(asset));
        line.setPreserveRatio(true);
        line.setSmooth(true);
        line.setFitWidth(width);
        return line;
    }

    public Parent getRoot() { return root; }
}