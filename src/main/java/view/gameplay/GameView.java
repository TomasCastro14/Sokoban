package view.gameplay;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import model.game.Level;
import view.SpriteCatalog;

import java.util.Objects;

public class GameView implements GameScreen {
    private static final String PAUSED_TEXT = "PAUSA";

    private final BorderPane root = new BorderPane();
    private final BoardView board;
    private final Label pausedBanner = new Label(PAUSED_TEXT);
    private ScreenLifecycleListener lifecycle;

    public GameView(SpriteCatalog sprites) {
        this.board = new BoardView(Objects.requireNonNull(sprites, "sprites"));
        pausedBanner.getStyleClass().add("paused-banner");
        pausedBanner.setVisible(false);
        root.setCenter(new StackPane(board, pausedBanner));
        root.getStyleClass().add("game-view");
        root.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/view/gameplay.css"),
                        "Falta /view/gameplay.css").toExternalForm());

        root.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (lifecycle == null) {
                return;
            }
            if (scene != null) {
                lifecycle.screenShown(scene);
            } else {
                lifecycle.screenHidden();
            }
        });
    }

    public Parent getRoot() {
        return root;
    }

    public void setHud(Node hud) {
        root.setTop(hud);
    }

    @Override
    public void setLifecycleListener(ScreenLifecycleListener listener) {
        this.lifecycle = listener;
    }

    @Override
    public void showLevel(Level level) {
        board.setLevel(level);
    }

    @Override
    public void playerMoved() {
        board.onPlayerMoved();
    }

    @Override
    public void boardChanged() {
        board.refresh();
    }

    @Override
    public void showPaused(boolean paused) {
        pausedBanner.setVisible(paused);
    }
}