package view.gameplay;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import model.game.Level;
import view.SpriteCatalog;

import java.util.Objects;

public class GameView implements GameScreen {
    private static final String PAUSED_TEXT = "PAUSA";

    private final StackPane root = new StackPane();
    private final BoardView board;
    private final Label pausedBanner = new Label(PAUSED_TEXT);
    private ScreenLifecycleListener lifecycle;
    private final StackPane hudLayer = new StackPane();

    /** The dialog layer goes over the whole screen, HUD included, so a popup covers everything. */
    public GameView(SpriteCatalog sprites, Node dialogLayer) {
        this.board = new BoardView(Objects.requireNonNull(sprites, "sprites"));
        pausedBanner.getStyleClass().add("paused-banner");
        pausedBanner.setVisible(false);
        hudLayer.setPickOnBounds(false);                      // fuera del HUD, el mouse llega a lo de abajo
        root.getChildren().addAll(new StackPane(board, pausedBanner), hudLayer,
                Objects.requireNonNull(dialogLayer, "dialogLayer"));
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
        Objects.requireNonNull(hud, "hud");
        StackPane.setAlignment(hud, Pos.TOP_CENTER);
        hudLayer.getChildren().setAll(hud);
        hud.boundsInParentProperty().addListener((obs, old, bounds) -> board.setTopInset(bounds.getMaxY()));
        board.setTopInset(hud.getBoundsInParent().getMaxY());
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
