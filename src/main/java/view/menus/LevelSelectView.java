package view.menus;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import view.components.images.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class LevelSelectView {

    public record LevelInfo(String name, int stars, boolean unlocked) { }

    public static final int FIRST_INDEX = 0;
    private static final String UI = "/assets/images/ui/";
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
    private static final double ARROW_WIDTH = 32;
    private static final double PLAY_BUTTON_OUTSET = -32;
    private static final double HALF_TURN = 180;
    private static final double HIDDEN = 0;
    private static final double SHOWN = 1;
    private static final Duration CARD_FADE = Duration.millis(90);

    private final List<LevelInfo> levels;
    private final IntConsumer onPlay;
    private final Map<Integer, Image> previews = new HashMap<>();
    private final Image starOn = ImageLoader.load(UI + "items/star.png");
    private final Image starOff = ImageLoader.load(UI + "items/star_outline.png");

    private final StackPane root = new StackPane();
    private final StackPane card;
    private final Label name = new Label();
    private final HBox stars = new HBox(STAR_SPACING);
    private final ImageView preview = new ImageView();
    private final Button play = new Button("¡A TRABAJAR!");
    private final Button prev;
    private final Button next;

    private int index;
    private boolean busy;

    public LevelSelectView(List<LevelInfo> levels, IntConsumer onPlay, int startIndex, Runnable onClose) {
        this.levels = levels;
        this.onPlay = onPlay;
        this.index = startIndex;

        root.getStyleClass().add("level-select");

        name.getStyleClass().add("level-name");
        for (int i = 0; i < MAX_STARS; i++) {
            ImageView s = new ImageView(starOff);
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

        play.getStyleClass().addAll("menu-button", "button-gold");
        play.setOnAction(e -> this.onPlay.accept(levelNumberAt(index)));
        StackPane.setAlignment(play, Pos.BOTTOM_CENTER);

        prev = arrow(false);
        next = arrow(true);
        StackPane.setAlignment(prev, Pos.CENTER_LEFT);
        StackPane.setAlignment(next, Pos.CENTER_RIGHT);
        StackPane.setMargin(prev, new Insets(0, 0, 0, ARROW_OUTSET));
        StackPane.setMargin(next, new Insets(0, ARROW_OUTSET, 0, 0));
        prev.setOnAction(e -> go(PREVIOUS));
        next.setOnAction(e -> go(NEXT));
        StackPane.setMargin(play, new Insets(0, 0, PLAY_BUTTON_OUTSET, 0));

        StackPane window = new StackPane(panel, play, prev, next);
        window.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        root.getStyleClass().add("background.level-select");

        PopupLayout.install(root, window, onClose);

        refresh();
    }

    private Button arrow(boolean right) {
        ImageView a = new ImageView(
                ImageLoader.load(UI + "buttons/rounded_gold/arrow_decorative_e.png")
        );
        a.setFitWidth(ARROW_WIDTH);
        a.setPreserveRatio(true);
        if (!right) a.setRotate(HALF_TURN);
        Button b = new Button();
        b.setGraphic(a);
        b.getStyleClass().add("button-round");
        return b;
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
            ((ImageView) stars.getChildren().get(i)).setImage(i < l.stars() ? starOn : starOff);
        }
        preview.setImage(previews.computeIfAbsent(levelNumberAt(index), n -> ImageLoader.load(UI + "level_previews/level" + n + ".png")));
        play.setDisable(!l.unlocked());
        prev.setVisible(index > FIRST_INDEX);
        next.setVisible(index < levels.size() - 1);
    }

    private static int levelNumberAt(int index) {
        return index + FIRST_LEVEL_NUMBER;
    }

    public Parent getRoot() { return root; }
}