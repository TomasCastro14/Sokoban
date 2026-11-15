package view.menus;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
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

/**
 * Passive view: it shows the levels it receives (padlock when locked, 0 to 3 stars when unlocked)
 * and asks for confirmation before reporting a progress reset. It never reads the progress itself.
 */
public class LevelSelectView implements LevelSelectScreen {

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
    private static final double RESET_MARGIN = 32;
    private static final double CONFIRM_SPACING = 24;
    private static final double CONFIRM_WIDTH = 520;
    private static final double LOCK_SPACING = 12;
    private static final double LOCK_BODY_W = 72;
    private static final double LOCK_BODY_H = 56;
    private static final double LOCK_BODY_ARC = 14;
    private static final double LOCK_SHACKLE_RADIUS = 22;
    private static final double LOCK_SHACKLE_WIDTH = 10;
    private static final double LOCK_KEYHOLE_RADIUS = 7;
    private static final Color LOCK_COLOR = Color.WHITE;
    private static final Color KEYHOLE_COLOR = Color.web("#1b1b22");
    private static final ColorAdjust LOCKED_PREVIEW = new ColorAdjust(0, -1, -0.55, 0);

    private static final String PLAY_TEXT = "¡A TRABAJAR!";
    private static final String ROOT_STYLE = "level-select";
    private static final String BACKGROUND_STYLE = "background";
    private static final String LEVEL_NAME_STYLE = "level-name";
    private static final String TITLE_BAR_STYLE = "level-title-bar";
    private static final String FRAME_STYLE = "frame-blue";
    private static final String PANEL_STYLE = "panel";
    private static final String OVERLAY_STYLE = "popup-overlay";
    private static final String LOCKED_TEXT_STYLE = "level-locked-text";
    private static final String CONFIRM_TEXT_STYLE = "confirm-text";
    private static final String WIDE_BUTTON_STYLE = "menu-button-wide";
    private static final String LOCKED_TEXT = "Ganá el nivel anterior para desbloquearlo";
    private static final String RESET_TEXT = "REINICIAR PROGRESO";
    private static final String CONFIRM_TEXT =
            "¿Reiniciar el progreso? Se pierden las estrellas y vuelve a quedar solo el nivel 1.";
    private static final String CONFIRM_YES = "REINICIAR";
    private static final String CONFIRM_NO = "CANCELAR";
    private static final Runnable NO_RESET = () -> { };

    private List<LevelInfo> levels = List.of();
    private Runnable onReset = NO_RESET;
    private final IntConsumer onPlay;
    private final ImageProvider images;

    private final StackPane root = new StackPane();
    private final Label name = new Label();
    private final List<ImageView> stars = new ArrayList<>(MAX_STARS);
    private final HBox starRow = new HBox(STAR_SPACING);
    private final ImageView preview = new ImageView();
    private final VBox lockBadge = lockBadge();
    private final StackPane confirm;
    private final StackPane card;
    private final Button play;
    private final Button prev;
    private final Button next;

    private int index;
    private boolean transitioning;

    public LevelSelectView(IntConsumer onPlay, int startIndex, Runnable onClose,
                           ImageProvider images, ButtonProvider buttons, PopupInstaller popups) {
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

        Button reset = buttons.createRectangular(RESET_TEXT, ButtonStyle.SECONDARY, this::askToReset);
        reset.getStyleClass().add(WIDE_BUTTON_STYLE);
        StackPane.setAlignment(reset, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(reset, new Insets(RESET_MARGIN));
        confirm = confirmPopup(buttons);
        root.getChildren().addAll(reset, confirm);
    }

    @Override
    public void showLevels(List<LevelInfo> levels) {
        this.levels = List.copyOf(Objects.requireNonNull(levels, "levels"));
        index = Math.min(index, Math.max(FIRST_INDEX, this.levels.size() - 1));
        refresh();
    }

    @Override
    public void setOnResetProgress(Runnable onReset) {
        this.onReset = Objects.requireNonNull(onReset, "onReset");
    }

    private void askToReset() {
        confirm.setVisible(true);
    }

    private StackPane confirmPopup(ButtonProvider buttons) {
        Label question = new Label(CONFIRM_TEXT);
        question.getStyleClass().add(CONFIRM_TEXT_STYLE);
        question.setWrapText(true);

        HBox answers = new HBox(CONFIRM_SPACING,
                buttons.createRectangular(CONFIRM_NO, ButtonStyle.SECONDARY, () -> confirm.setVisible(false)),
                buttons.createRectangular(CONFIRM_YES, ButtonStyle.PRIMARY, () -> {
                    confirm.setVisible(false);
                    onReset.run();
                }));
        answers.setAlignment(Pos.CENTER);

        VBox box = new VBox(CONFIRM_SPACING, question, answers);
        box.getStyleClass().add(PANEL_STYLE);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(CONFIRM_WIDTH);
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Region veil = new Region();
        veil.getStyleClass().add(OVERLAY_STYLE);
        veil.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        StackPane popup = new StackPane(veil, box);
        popup.setVisible(false);
        return popup;
    }

    /** A padlock drawn with shapes (there is no padlock image among the assets) and a hint below. */
    private static VBox lockBadge() {
        Arc shackle = new Arc(0, 0, LOCK_SHACKLE_RADIUS, LOCK_SHACKLE_RADIUS, 0, 180);
        shackle.setType(ArcType.OPEN);
        shackle.setFill(null);
        shackle.setStroke(LOCK_COLOR);
        shackle.setStrokeWidth(LOCK_SHACKLE_WIDTH);
        shackle.setStrokeLineCap(StrokeLineCap.BUTT);

        Rectangle body = new Rectangle(LOCK_BODY_W, LOCK_BODY_H, LOCK_COLOR);
        body.setArcWidth(LOCK_BODY_ARC);
        body.setArcHeight(LOCK_BODY_ARC);
        StackPane bodyWithHole = new StackPane(body, new Circle(LOCK_KEYHOLE_RADIUS, KEYHOLE_COLOR));

        VBox padlock = new VBox(-LOCK_SHACKLE_WIDTH / 2, shackle, bodyWithHole);
        padlock.setAlignment(Pos.CENTER);

        Label hint = new Label(LOCKED_TEXT);
        hint.getStyleClass().add(LOCKED_TEXT_STYLE);

        VBox badge = new VBox(LOCK_SPACING, padlock, hint);
        badge.setAlignment(Pos.CENTER);
        badge.setMouseTransparent(true);
        badge.setVisible(false);
        return badge;
    }

    private StackPane card() {
        preview.setPreserveRatio(true);
        preview.setFitWidth(PREVIEW_W);
        preview.setFitHeight(PREVIEW_H);
        return TitledPanel.card(titleBar(), new StackPane(preview, lockBadge));
    }

    private StackPane titleBar() {
        name.getStyleClass().add(LEVEL_NAME_STYLE);

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
        if (transitioning || levels.isEmpty()) return;
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
        if (levels.isEmpty()) {
            play.setDisable(true);
            prev.setVisible(false);
            next.setVisible(false);
            return;
        }
        LevelInfo level = levels.get(index);
        name.setText(level.name());
        starRow.setVisible(level.unlocked());
        lockBadge.setVisible(!level.unlocked());
        preview.setEffect(level.unlocked() ? null : LOCKED_PREVIEW);
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
