package view.components.container;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * The menus' window (level select, settings): a grey panel holding a blue title bar laid over
 * the top edge of a blue frame. Built in parts so a screen can animate the card on its own.
 */
public final class TitledPanel {
    private static final double TITLE_BAR_HEIGHT = 96;
    /** The frame starts this far down, so the bar covers its top edge. */
    private static final double FRAME_TOP_OFFSET = 84;
    private static final String PANEL_STYLE = "panel";
    private static final String TITLE_BAR_STYLE = "title-bar";
    private static final String FRAME_STYLE = "frame-blue";

    private TitledPanel() {
    }

    /** The blue bar; its children are placed with StackPane.setAlignment (centered by default). */
    public static StackPane titleBar(Node... content) {
        StackPane bar = new StackPane(content);
        bar.getStyleClass().add(TITLE_BAR_STYLE);
        bar.setPrefHeight(TITLE_BAR_HEIGHT);
        bar.setMaxHeight(Region.USE_PREF_SIZE);
        return bar;
    }

    /** The title bar over the frame that holds the screen's content. */
    public static StackPane card(StackPane titleBar, Node frameContent) {
        StackPane frame = new StackPane(frameContent);
        frame.getStyleClass().add(FRAME_STYLE);
        StackPane.setMargin(frame, new Insets(FRAME_TOP_OFFSET, 0, 0, 0));

        StackPane card = new StackPane(titleBar, frame);
        card.setAlignment(Pos.TOP_CENTER);
        return card;
    }

    /** The grey panel around the card, never bigger than its preferred size. */
    public static StackPane panel(Node card) {
        StackPane panel = new StackPane(card);
        panel.getStyleClass().add(PANEL_STYLE);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return panel;
    }
}