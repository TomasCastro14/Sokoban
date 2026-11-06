package view;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class GameWindow implements Navigator {
    private static final double WIDTH = 1280;
    private static final double HEIGHT = 720;
    private static final Duration FADE = Duration.millis(180);
    private static final double HIDDEN = 0;
    private static final double SHOWN = 1;
    private static final String TITLE = "New Sokoban!";
    private static final Runnable NOTHING = () -> { };
    private final Deque<Screen> history = new ArrayDeque<>();

    private final Stage stage;
    private final StackPane content = new StackPane();
    private final Map<Screen, Parent> screens = new EnumMap<>(Screen.class);
    private final Map<Screen, Runnable> escapeActions = new EnumMap<>(Screen.class);
    private Screen current;

    public GameWindow(Stage stage, SpriteCatalog sprites) {
        this.stage = stage;

        StackPane root = new StackPane(content);
        root.getStyleClass().add("background");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/view/sokoban.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(sprites.get(SpriteKey.WINDOW_ICON));
        stage.setResizable(false);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                escapeActions.getOrDefault(current, NOTHING).run();
            }
        });
    }

    public void register(Screen screen, Parent node, Runnable onEscape) {
        screens.put(screen, node);
        escapeActions.put(screen, Objects.requireNonNull(onEscape, "onEscape"));
    }

    public void open() {
        stage.show();
    }

    @Override
    public void show(Screen next) {
        if (current != null && current != next) history.push(current);
        swap(next);
    }

    @Override
    public void back() {
        if (!history.isEmpty()) swap(history.pop());
    }

    private void swap(Screen next) {
        Parent target = screens.get(next);
        if (current == null) {
            content.getChildren().setAll(target);
            current = next;
            return;
        }
        content.setMouseTransparent(true);
        FadeTransition out = new FadeTransition(FADE, content);
        out.setToValue(HIDDEN);
        out.setOnFinished(e -> {
            content.getChildren().setAll(target);
            current = next;
            FadeTransition in = new FadeTransition(FADE, content);
            in.setToValue(SHOWN);
            in.setOnFinished(ev -> content.setMouseTransparent(false));
            in.play();
        });
        out.play();
    }
}