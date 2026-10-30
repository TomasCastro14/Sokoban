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

    /** Datos que la vista necesita; no depende del modelo. */
    public record LevelInfo(String name, int stars, boolean unlocked) { }

    private static final String UI = "/assets/images/ui/";
    private static final double PREVIEW_W = 704, PREVIEW_H = 354;   // ajustar al preview real

    private final List<LevelInfo> levels;
    private final IntConsumer onPlay;                 // recibe el número de nivel (1-based)
    private final Map<Integer, Image> previews = new HashMap<>();
    private final Image starOn = ImageLoader.load(UI + "items/star.png");
    private final Image starOff = ImageLoader.load(UI + "items/star_outline.png");

    private final StackPane root = new StackPane();
    private final StackPane card;
    private final Label name = new Label();
    private final HBox stars = new HBox(6);
    private final ImageView preview = new ImageView();
    private final Button play = new Button("¡A TRABAJAR!");
    private final Button prev;
    private final Button next;
    private final Button exit = new Button("SALIR");

    private int index;
    private boolean busy;

    public LevelSelectView(List<LevelInfo> levels, IntConsumer onPlay, int startIndex, Runnable onClose) {
        this.levels = levels;
        this.onPlay = onPlay;
        this.index = startIndex;

        root.getStyleClass().add("level-select");

        // Título: va DEBAJO del contenedor de la imagen (se dibuja primero)
        name.getStyleClass().add("level-name");
        for (int i = 0; i < 3; i++) {
            ImageView s = new ImageView(starOff);
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

        // Contenedor de la imagen: se superpone al borde inferior del título
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

        play.getStyleClass().addAll("menu-button", "button-gold");
        play.setOnAction(e -> this.onPlay.accept(index + 1));
        StackPane.setAlignment(play, Pos.BOTTOM_CENTER);

        prev = arrow(false);
        next = arrow(true);
        StackPane.setAlignment(prev, Pos.CENTER);
        StackPane.setAlignment(next, Pos.CENTER_RIGHT);
        StackPane.setAlignment(prev, Pos.CENTER_LEFT);
        StackPane.setAlignment(next, Pos.CENTER_RIGHT);
        StackPane.setMargin(prev, new Insets(0, 0, 0, -36));
        StackPane.setMargin(next, new Insets(0, -36, 0, 0));
        prev.setOnAction(e -> go(-1));
        next.setOnAction(e -> go(+1));
        StackPane.setAlignment(play, Pos.BOTTOM_CENTER);
        StackPane.setMargin(play, new Insets(0, 0, -32, 0));

        StackPane window = new StackPane(panel, play, prev, next);

        window.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        root.getStyleClass().add("background.level-select");

        PopupLayout.install(root, window, onClose);

        refresh();
    }

    private Button arrow(boolean right) {
        ImageView a = new ImageView(
                ImageLoader.load(UI + "buttons/rounded_gold/arrow_decorative_e.png")
        );
        a.setFitWidth(32);
        a.setPreserveRatio(true);
        if (!right) a.setRotate(180);
        Button b = new Button();
        b.setGraphic(a);
        b.getStyleClass().add("button-round");
        return b;
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
            ((ImageView) stars.getChildren().get(i)).setImage(i < l.stars() ? starOn : starOff);
        }
        preview.setImage(previews.computeIfAbsent(index + 1, n -> ImageLoader.load(UI + "level_previews/level" + n + ".png")));
        play.setDisable(!l.unlocked());
        prev.setVisible(index > 0);
        next.setVisible(index < levels.size() - 1);
    }

    public Parent getRoot() { return root; }
}