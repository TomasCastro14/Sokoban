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

    private static final double PREVIEW_W = 704, PREVIEW_H = 354;

    private final List<LevelInfo> levels;
    private final IntConsumer onPlay;
    private final UiImages images;

    private final StackPane root = new StackPane();
    private final StackPane card;
    private final Label name = new Label();
    private final HBox stars = new HBox(6);
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
        for (int i = 0; i < 3; i++) {
            ImageView s = new ImageView(images.get(UiImage.STAR_OFF));
            s.setFitHeight(40);
            s.setPreserveRatio(true);
            stars.getChildren().add(s);
        }
        StackPane titleBar = new StackPane(name, stars);
        titleBar.getStyleClass().add("level-title-bar");
        titleBar.setPrefHeight(96);
        titleBar.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(name, Pos.CENTER_LEFT);
        StackPane.setAlignment(stars, Pos.CENTER_RIGHT);
        stars.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        stars.setSpacing(6);

        preview.setPreserveRatio(true);
        preview.setFitWidth(PREVIEW_W);
        preview.setFitHeight(PREVIEW_H);
        StackPane frame = new StackPane(preview);
        frame.getStyleClass().add("frame-blue");
        StackPane.setMargin(frame, new Insets(84, 0, 0, 0));

        card = new StackPane(titleBar, frame);
        card.setAlignment(Pos.TOP_CENTER);

        StackPane panel = new StackPane(card);
        panel.getStyleClass().add("panel");
        panel.setPrefSize(800, 546);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        play = buttons.create("¡A TRABAJAR!", ButtonStyle.GOLD, () -> this.onPlay.accept(index + 1));
        StackPane.setAlignment(play, Pos.BOTTOM_CENTER);
        StackPane.setMargin(play, new Insets(0, 0, -32, 0));

        prev = buttons.createArrow(ArrowDirection.LEFT, () -> go(-1));
        next = buttons.createArrow(ArrowDirection.RIGHT, () -> go(+1));
        StackPane.setAlignment(prev, Pos.CENTER_LEFT);
        StackPane.setAlignment(next, Pos.CENTER_RIGHT);
        StackPane.setMargin(prev, new Insets(0, 0, 0, -36));
        StackPane.setMargin(next, new Insets(0, -36, 0, 0));

        StackPane window = new StackPane(panel, play, prev, next);
        window.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        root.getStyleClass().add("background.level-select");

        popups.install(root, window, onClose);

        refresh();
    }

    private void go(int delta) {
        if (busy) return;
        busy = true;
        FadeTransition out = new FadeTransition(Duration.millis(90), card);
        out.setToValue(0);
        out.setOnFinished(e -> {
            index += delta;
            refresh();
            FadeTransition in = new FadeTransition(Duration.millis(90), card);
            in.setToValue(1);
            in.setOnFinished(ev -> busy = false);
            in.play();
        });
        out.play();
    }

    private void refresh() {
        LevelInfo l = levels.get(index);
        name.setText(l.name());
        for (int i = 0; i < 3; i++) {
            ((ImageView) stars.getChildren().get(i)).setImage(images.get(i < l.stars() ? UiImage.STAR_ON : UiImage.STAR_OFF));
        }
        preview.setImage(images.find(new LevelPreview(index + 1)).orElse(null));
        play.setDisable(!l.unlocked());
        prev.setVisible(index > 0);
        next.setVisible(index < levels.size() - 1);
    }

    public Parent getRoot() { return root; }
}