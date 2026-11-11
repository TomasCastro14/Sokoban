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
import view.components.buttons.ButtonIcon;
import view.components.buttons.ButtonProvider;
import view.components.buttons.ButtonSize;
import view.components.buttons.ButtonStyle;
import view.components.buttons.RoundStyle;
import view.components.container.PopupInstaller;
import view.components.container.TitledPanel;
import view.components.images.LevelPreview;
import view.components.images.UiImage;
import view.components.images.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private static final String PLAY_TEXT = "¡A TRABAJAR!";
    private static final String ROOT_STYLE = "level-select";
    private static final String BACKGROUND_STYLE = "background";
    private static final String LEVEL_NAME_STYLE = "level-name";
    private static final String TITLE_BAR_STYLE = "level-title-bar";
    private static final String FRAME_STYLE = "frame-blue";
    private static final String PANEL_STYLE = "panel";

    private final List<LevelInfo> levels;
    private final IntConsumer onPlay;
    private final ImageProvider images;

    private final StackPane root = new StackPane();
    private final Label name = new Label();
    private final List<ImageView> stars = new ArrayList<>(MAX_STARS);
    private final ImageView preview = new ImageView();
    private final StackPane card;
    private final Button play;
    private final Button prev;
    private final Button next;

    private int index;
    private boolean transitioning;

    public LevelSelectView(List<LevelInfo> levels, IntConsumer onPlay, int startIndex, Runnable onClose,
                           ImageProvider images, ButtonProvider buttons, PopupInstaller popups) {
        this.levels = List.copyOf(Objects.requireNonNull(levels, "levels"));
        this.onPlay = Objects.requireNonNull(onPlay, "onPlay");
        this.images = Objects.requireNonNull(images, "images");
        Objects.requireNonNull(onClose, "onClose");
        Objects.requireNonNull(buttons, "buttons");
        Objects.requireNonNull(popups, "popups");
        this.index = startIndex;

        card = card();
        play = playButton(buttons);
        prev = arrowButton(buttons, ButtonIcon.ARROW_LEFT, PREVIOUS, Pos.CENTER_LEFT, new Insets(0, 0, 0, ARROW_OUTSET));
        next = arrowButton(buttons, ButtonIcon.ARROW_RIGHT, NEXT, Pos.CENTER_RIGHT, new Insets(0, ARROW_OUTSET, 0, 0));

        StackPane panel = TitledPanel.panel(card);
        panel.setPrefSize(PANEL_WIDTH, PANEL_HEIGHT);
        StackPane window = new StackPane(panel, play, prev, next);
        window.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        root.getStyleClass().addAll(BACKGROUND_STYLE, ROOT_STYLE);
        popups.install(root, window, onClose);

        refresh();
    }

    private StackPane card() {
        preview.setPreserveRatio(true);
        preview.setFitWidth(PREVIEW_W);
        preview.setFitHeight(PREVIEW_H);
        return TitledPanel.card(titleBar(), preview);
    }

    private StackPane titleBar() {
        name.getStyleClass().add(LEVEL_NAME_STYLE);

        HBox starRow = new HBox(STAR_SPACING);
        for (int i = 0; i < MAX_STARS; i++) {
            ImageView star = new ImageView(images.get(UiImage.STAR_OFF));
            star.setFitHeight(STAR_HEIGHT);
            star.setPreserveRatio(true);
            stars.add(star);
        }
        starRow.getChildren().setAll(stars);
        starRow.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        StackPane titleBar = TitledPanel.titleBar(name, starRow);
        titleBar.getStyleClass().add(TITLE_BAR_STYLE);
        titleBar.setPrefHeight(TITLE_BAR_HEIGHT);
        titleBar.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(name, Pos.CENTER_LEFT);
        StackPane.setAlignment(starRow, Pos.CENTER_RIGHT);
        return titleBar;
    }

    private Button playButton(ButtonProvider buttons) {
        Button button = buttons.createRectangular(PLAY_TEXT, ButtonStyle.GOLD, () -> onPlay.accept(levelNumberAt(index)));
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setMargin(button, new Insets(0, 0, PLAY_BUTTON_OUTSET, 0));
        return button;
    }

    private Button arrowButton(ButtonProvider buttons, ButtonIcon arrow, int step,
                               Pos alignment, Insets margin) {
        Button button = buttons.createRound(RoundStyle.GOLD, arrow, ButtonSize.REGULAR, () -> moveBy(step));
        StackPane.setAlignment(button, alignment);
        StackPane.setMargin(button, margin);
        return button;
    }

    private void moveBy(int step) {
        if (transitioning) return;
        transitioning = true;
        fade(HIDDEN, () -> {
            index += step;
            refresh();
            fade(SHOWN, () -> transitioning = false);
        });
    }

    private void fade(double opacity, Runnable whenDone) {
        FadeTransition transition = new FadeTransition(CARD_FADE, card);
        transition.setToValue(opacity);
        transition.setOnFinished(event -> whenDone.run());
        transition.play();
    }

    private void refresh() {
        LevelInfo level = levels.get(index);
        name.setText(level.name());
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setImage(images.get(i < level.stars() ? UiImage.STAR_ON : UiImage.STAR_OFF));
        }
        preview.setImage(images.find(new LevelPreview(levelNumberAt(index))).orElse(null));
        play.setDisable(!level.unlocked());
        prev.setVisible(index > FIRST_INDEX);
        next.setVisible(index < levels.size() - 1);
    }

    private static int levelNumberAt(int index) {
        return index + FIRST_LEVEL_NUMBER;
    }

    public Parent getRoot() {
        return root;
    }
}
