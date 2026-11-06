package view.gameplay.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.components.buttons.ButtonIcon;
import view.components.buttons.ButtonProvider;
import view.components.buttons.ButtonSize;
import view.components.buttons.RoundStyle;
import view.components.images.ImageProvider;
import view.components.images.UiImage;

import java.util.Objects;

/**
 * The pieces every level popup is built from, so the three of them look like one family:
 * the grey panel, the colored boxes, the goal rows and the round buttons over the bottom edge.
 */
final class DialogParts {
    static final String TITLE_TEXT = "dialog-title";
    static final String LIGHT_TITLE_TEXT = "dialog-title-light";

    private static final ButtonSize ACTION_SIZE = ButtonSize.LARGE;
    /** The action buttons sit half outside the panel. */
    private static final double ACTION_OVERHANG = ACTION_SIZE.diameter() / 2;
    private static final double ACTION_SPACING = 96;
    private static final double PANEL_WIDTH = 380;
    private static final double PANEL_SPACING = 14;
    private static final double PANEL_SIDE_PADDING = 18;
    private static final double PANEL_BOTTOM_PADDING = ACTION_OVERHANG + 12;
    private static final double ROW_ICON_SIZE = 30;
    private static final double ROW_SPACING = 12;
    private static final double LIST_SPACING = 10;

    private static final String PANEL_STYLE = "panel";
    private static final String HEADER_BOX = "dialog-header";
    private static final String LIST_BOX = "dialog-list";
    private static final String ROW_NAME = "goal-name";
    private static final String ROW_VALUE = "goal-value";

    private final ImageProvider images;
    private final ButtonProvider buttons;

    DialogParts(ImageProvider images, ButtonProvider buttons) {
        this.images = Objects.requireNonNull(images, "images");
        this.buttons = Objects.requireNonNull(buttons, "buttons");
    }

    /** The grey panel with its content stacked; the returned pane lets buttons and stars stick out of it. */
    StackPane panel(double topPadding, Node... content) {
        VBox column = new VBox(PANEL_SPACING, content);
        column.getStyleClass().add(PANEL_STYLE);
        column.setAlignment(Pos.TOP_CENTER);
        column.setPadding(new Insets(topPadding, PANEL_SIDE_PADDING, PANEL_BOTTOM_PADDING, PANEL_SIDE_PADDING));
        column.setPrefWidth(PANEL_WIDTH);
        column.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        StackPane dialog = new StackPane(column);
        dialog.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return dialog;
    }

    /** A colored nine-slice box with one centered text, used for titles, scores and messages. */
    StackPane textBox(String text, String boxStyle, String textStyle) {
        Label label = new Label(text);
        label.getStyleClass().add(textStyle);
        label.setWrapText(true);
        StackPane box = new StackPane(label);
        box.getStyleClass().add(boxStyle);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    /** The grey header with the list of rows under it ("Objetivos", "Resumen"). */
    VBox list(String title, Node... rows) {
        VBox body = new VBox(LIST_SPACING, rows);
        body.getStyleClass().add(LIST_BOX);
        return new VBox(textBox(title, HEADER_BOX, TITLE_TEXT), body);
    }

    /** One goal line: icon, name on the left, value on the right. */
    HBox row(UiImage icon, String name, String value, String valueStyle) {
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add(ROW_NAME);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().addAll(ROW_VALUE, valueStyle);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        HBox row = new HBox(ROW_SPACING, image(icon, ROW_ICON_SIZE), nameLabel, gap, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    ImageView image(UiImage asset, double height) {
        ImageView view = new ImageView(images.get(asset));
        view.setFitHeight(height);
        view.setPreserveRatio(true);
        return view;
    }

    Button action(RoundStyle style, ButtonIcon icon, Runnable onClick) {
        return buttons.createRound(style, icon, ACTION_SIZE, onClick);
    }

    /** Places the action buttons centered over the bottom edge of the panel. */
    void addActions(StackPane dialog, Button... actions) {
        HBox row = new HBox(ACTION_SPACING, actions);
        row.setAlignment(Pos.CENTER);
        row.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        row.setPickOnBounds(false);
        StackPane.setAlignment(row, Pos.BOTTOM_CENTER);
        StackPane.setMargin(row, new Insets(0, 0, -ACTION_OVERHANG, 0));
        dialog.getChildren().add(row);
    }
}
