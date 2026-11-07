package view.gameplay.dialog;

import javafx.geometry.Bounds;
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
 * the grey panel, the colored boxes, the goal rows, the round buttons over the bottom edge
 * and the decorations (stars, sokoban) that stick out of the panel.
 */
final class DialogParts {
    /** Which of two stacked boxes is drawn over the other where they meet. */
    enum Overlap {
        UPPER_IN_FRONT,
        LOWER_IN_FRONT
    }

    static final String TITLE_TEXT = "dialog-title";
    static final String LIGHT_TITLE_TEXT = "dialog-title-light";
    static final double PANEL_WIDTH = 470;

    private static final ButtonSize ACTION_SIZE = ButtonSize.LARGE;
    /** The action buttons sit half outside the panel. */
    private static final double ACTION_OVERHANG = ACTION_SIZE.diameter() / 2;
    private static final double ACTION_SPACING = 100;
    private static final double PANEL_SPACING = 16;
    private static final double PANEL_SIDE_PADDING = 22;
    private static final double PANEL_BOTTOM_PADDING = ACTION_OVERHANG + 10;
    private static final double ROW_ICON_SIZE = 36;
    private static final double ROW_SPACING = 14;
    private static final double LIST_SPACING = 22;
    /** How much a box tucks under (or over) the one above it: an art choice of the mockups. */
    private static final double BOX_OVERLAP = 12;
    private static final double IN_FRONT = -1;
    private static final double MASCOT_SIZE = 160;
    private static final double BUBBLE_HEIGHT = 72;

    /** Not the menus' "panel" class: its CSS padding would override the padding set here. */
    private static final String PANEL_STYLE = "dialog-panel";
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

    /**
     * The grey panel with its content stacked. The returned pane is exactly the panel's size,
     * so decorations can be placed in panel coordinates and buttons can stick out of it.
     */
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
        StackPane box = new StackPane(label);
        box.getStyleClass().add(boxStyle);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    /** Two boxes one under the other, overlapping a little; {@code overlap} says which one covers the other. */
    VBox overlapped(Overlap overlap, Node upper, Node lower) {
        Node front = overlap == Overlap.UPPER_IN_FRONT ? upper : lower;
        front.setViewOrder(IN_FRONT);
        return new VBox(-BOX_OVERLAP, upper, lower);
    }

    /** The grey header with the list of rows tucked over its bottom edge ("Objetivos", "Resumen"). */
    VBox list(String title, Node... rows) {
        VBox body = new VBox(LIST_SPACING, rows);
        body.getStyleClass().add(LIST_BOX);
        return overlapped(Overlap.LOWER_IN_FRONT, textBox(title, HEADER_BOX, TITLE_TEXT), body);
    }

    /** One goal line: icon, name on the left, value on the right. Texts never shrink into "...". */
    HBox row(UiImage icon, String name, String value, String valueStyle) {
        Label nameLabel = fullLabel(name, ROW_NAME);
        Label valueLabel = fullLabel(value, ROW_VALUE);
        valueLabel.getStyleClass().add(valueStyle);
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

    /** The sokoban peeking from behind the panel, with a thought bubble showing how he feels. */
    void addMascot(StackPane dialog, MascotPose pose, UiImage emote) {
        ImageView figure = image(UiImage.MASCOT, MASCOT_SIZE);
        figure.setRotate(pose.rotation());
        ImageView bubble = image(emote, BUBBLE_HEIGHT);
        dialog.getChildren().add(0, decoration(figure, pose.figureCenterX(PANEL_WIDTH), pose.figureCenterY()));
        dialog.getChildren().add(decoration(bubble, pose.bubbleCenterX(PANEL_WIDTH), pose.bubbleCenterY()));
    }

    /**
     * A node placed by hand at a point of the panel (0,0 is its top-left corner). It is left out of
     * the layout, so it neither moves the panel nor changes its size when it sticks out.
     */
    Node decoration(Node node, double centerX, double centerY) {
        node.setManaged(false);
        node.setMouseTransparent(true);
        Bounds bounds = node.getLayoutBounds();
        node.relocate(centerX - bounds.getWidth() / 2, centerY - bounds.getHeight() / 2);
        return node;
    }

    private static Label fullLabel(String text, String style) {
        Label label = new Label(text);
        label.getStyleClass().add(style);
        label.setMinWidth(Region.USE_PREF_SIZE);
        return label;
    }
}
