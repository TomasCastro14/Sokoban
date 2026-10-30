package view;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.*;

public class GameWindow implements Navigator {
    private static final double WIDTH = 1280;
    private static final double HEIGHT = 720;
    private static final Duration FADE = Duration.millis(180);
    private final Deque<Screen> history = new ArrayDeque<>();

    private final Stage stage;
    private final StackPane content = new StackPane();   // lo único que se intercambia
    private final Map<Screen, Parent> screens = new EnumMap<>(Screen.class);
    private Screen current;

    public GameWindow(Stage stage, SpriteCatalog sprites) {
        this.stage = stage;

        StackPane root = new StackPane(content);          // fondo estático
        root.getStyleClass().add("background");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/view/sokoban.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle("New Sokoban!");
        stage.getIcons().add(sprites.get(SpriteKey.WINDOW_ICON));
        stage.setResizable(false);
        stage.setFullScreen(true);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });
    }

    public void register(Screen screen, Parent node) {
        screens.put(screen, node);
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
        out.setToValue(0);
        out.setOnFinished(e -> {
            content.getChildren().setAll(target);
            current = next;
            FadeTransition in = new FadeTransition(FADE, content);
            in.setToValue(1);
            in.setOnFinished(ev -> content.setMouseTransparent(false));
            in.play();
        });
        out.play();
    }

    private void loadFont(String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in != null) Font.loadFont(in, 12);
        } catch (Exception ignored) { }
    }
}