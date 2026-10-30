package view.gameplay;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import model.game.Level;
import view.SpriteCatalog;

import java.util.Objects;

/**
 * Pantalla de juego: el tablero al centro y un lugar arriba para el HUD. Es una vista pasiva:
 * dibuja lo que el controller le pide (GameScreen) y avisa cuándo entra y sale de la ventana
 * (ScreenLifecycleListener). No conoce Game, ni eventos del modelo, ni teclado, ni reloj.
 */
public class GameView implements GameScreen {
    private final BorderPane root = new BorderPane();
    private final BoardView board;
    private ScreenLifecycleListener lifecycle;

    public GameView(SpriteCatalog sprites) {
        this.board = new BoardView(Objects.requireNonNull(sprites, "sprites"));
        root.setCenter(board);
        root.getStyleClass().add("game-view");
        root.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/view/gameplay.css"),
                        "Falta /view/gameplay.css").toExternalForm());

        // GameWindow intercambia pantallas sacando y poniendo su raíz: esa es nuestra señal de mostrar/ocultar.
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

    /** Punto de enganche del próximo commit: el HUD ocupa su franja y el tablero se achica solo. */
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
}
