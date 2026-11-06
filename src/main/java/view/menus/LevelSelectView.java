package view.menus;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import view.components.buttons.ArrowDirection;
import view.components.buttons.ButtonFactory;
import view.components.buttons.ButtonStyle;
import view.components.container.PopupLayout;
import view.components.images.LevelPreview;
import view.components.images.UiImage;
import view.components.images.UiImages;

import java.util.List;
import java.util.function.IntConsumer;

public class LevelSelectView {

    public record LevelInfo(String name, int stars, boolean unlocked) { }

    public static final int FIRST_INDEX = 0;
    private static final int MAX_STARS = 3;
    private static final int FIRST_LEVEL_NUMBER = 1;
    private static final int PREVIOUS = -1;
    private static final int NEXT = 1;
    private static final double PREVIEW_W = 704;
    private static final double PREVIEW_H = 354;
    private static final double STAR_HEIGHT = 40;
    private static final double STAR_SPACING = 6;
    private static final double TITLE_BAR_HEIGHT = 96;
    private static final double FRAME_TOP_OVERLAP = 84;
    private static final double PANEL_WIDTH = 800;
    private static final double PANEL_HEIGHT = 546;
    private static final double ARROW_OUTSET = -36;
    private static final double PLAY_BUTTON_OUTSET = -32;
    private static final double HIDDEN = 0;
    private static final double SHOWN = 1;
    private static final Duration CARD_FADE = Duration.millis(90);

    private final List<LevelInfo> levels;
    private final IntConsumer onPlay;
    private final UiImages images;

    private final StackPane root = new StackPane();
    private final StackPane card;
    private final Label name = new Label();
    private final HBox stars = new HBox(STAR_SPACING);
    private final ImageView preview = new ImageView();
    private final Button play;
    private final Button prev;
    private final Button next;

    private int index;
    private boolean busy;

    public LevelSelectView(List<LevelInfo> levels, IntConsumer onPlay, int startIndex, Runnable onClose,
                           UiImages images, ButtonFactory buttons, PopupLayout popups) {
        this.levels = levels;
        this.onPlay = onPlay;
        this.index = startIndex;
        this.images = images;

        root.getStyleClass().add("level-select");

        name.getStyleClass().add("level-name");
        for (int i = 0; i < MAX_STARS; i++) {
            ImageView s = new ImageView(images.get(UiImage.STAR_OFF));
            s.setFitHeight(STAR_HEIGHT);
            s.setPreserveRatio(true);
            stars.getChildren().add(s);
        }
        StackPane titleBar = new StackPane(name, stars);
        titleBar.getStyleClass().add("level-title-bar");
        titleBar.setPrefHeight(TITLE_BAR_HEIGHT);
        titleBar.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(name, Pos.CENTER_LEFT);
        StackPane.setAlignment(stars, Pos.CENTER_RIGHT);
        stars.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        preview.setPreserveRatio(true);
        preview.setFitWidth(PREVIEW_W);
        preview.setFitHeight(PREVIEW_H);
        StackPane frame = new StackPane(preview);
        frame.getStyleClass().add("frame-blue");
        StackPane.setMargin(frame, new Insets(FRAME_TOP_OVERLAP, 0, 0, 0));

        card = new StackPane(titleBar, frame);
        card.setAlignment(Pos.TOP_CENTER);

        StackPane panel = new StackPane(card);
        panel.getStyleClass().add("panel");
        panel.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        play = buttons.create("¡A TRABAJAR!", ButtonStyle.GOLD, () -> this.onPlay.accept(levelNumberAt(index)));
        StackPane.setAlignment(play, Pos.BOTTOM_CENTER);
        StackPane.setMargin(play, new Insets(0, 0, PLAY_BUTTON_OUTSET, 0));

        prev = buttons.createArrow(ArrowDirection.LEFT, () -> go(PREVIOUS));
        next = buttons.createArrow(ArrowDirection.RIGHT, () -> go(NEXT));
        StackPane.setAlignment(prev, Pos.CENTER_LEFT);
        StackPane.setAlignment(next, Pos.CENTER_RIGHT);
        StackPane.setMargin(prev, new Insets(0, 0, 0, ARROW_OUTSET));
        StackPane.setMargin(next, new Insets(0, ARROW_OUTSET, 0, 0));

        StackPane window = new StackPane(panel, play, prev, next);
        window.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        root.getStyleClass().add("background.level-select");

        popups.install(root, window, onClose);

        refresh();
    }

    private void go(int delta) {
        if (busy) return;
        busy = true;
        FadeTransition out = new FadeTransition(CARD_FADE, card);
        out.setToValue(HIDDEN);
        out.setOnFinished(e -> {
            index += delta;
            refresh();
            FadeTransition in = new FadeTransition(CARD_FADE, card);
            in.setToValue(SHOWN);
            in.setOnFinished(ev -> busy = false);
            in.play();
        });
        out.play();
    }

    private void refresh() {
        LevelInfo l = levels.get(index);
        name.setText(l.name());
        for (int i = 0; i < MAX_STARS; i++) {
            ((ImageView) stars.getChildren().get(i)).setImage(images.get(i < l.stars() ? UiImage.STAR_ON : UiImage.STAR_OFF));
        }
        preview.setImage(images.find(new LevelPreview(levelNumberAt(index))).orElse(null));
        play.setDisable(!l.unlocked());
        prev.setVisible(index > FIRST_INDEX);
        next.setVisible(index < levels.size() - 1);
    }

    private static int levelNumberAt(int index) {
        return index + FIRST_LEVEL_NUMBER;
    }

    public Parent getRoot() { return root; }
}