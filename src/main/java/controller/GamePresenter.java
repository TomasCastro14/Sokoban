package controller;

import javafx.scene.Scene;
import model.board.Direction;
import model.board.Visibility;
import model.game.Game;
import model.game.Level;
import model.game.event.BoardListener;
import model.game.event.LevelFlowListener;
import view.gameplay.GameScreen;
import view.gameplay.ScreenLifecycleListener;

import java.util.Objects;

/** Joins the board view to the model and the screen lifecycle to the play session. Popups (pause included) live in LevelDialogPresenter. */
public final class GamePresenter implements BoardListener, LevelFlowListener, ScreenLifecycleListener {
    private final Game game;
    private final GameScreen screen;
    private final PlaySession session;
    private boolean visible;

    public GamePresenter(Game game, GameScreen screen, PlaySession session) {
        this.game = Objects.requireNonNull(game, "game");
        this.screen = Objects.requireNonNull(screen, "screen");
        this.session = Objects.requireNonNull(session, "session");
        game.addBoardListener(this);
        game.addLevelFlowListener(this);
        screen.setLifecycleListener(this);
    }

    public void dispose() {
        screenHidden();
        session.stop();
        game.removeBoardListener(this);
        game.removeLevelFlowListener(this);
    }

    /** ESC pauses and resumes only while playing: with the goals, defeat or victory popup open it does nothing. */
    public void onEscape() {
        if (session.isActive()) {
            game.togglePause();
        }
    }

    @Override
    public void screenShown(Scene scene) {
        visible = true;
        screen.showLevel(game.getCurrentLevel());
        session.attach(scene);
    }

    @Override
    public void screenHidden() {
        visible = false;
        session.detach();
    }

    @Override
    public void levelStarted(int levelNumber, Level level) {
        if (visible) {
            screen.showLevel(level);
        }
    }

    @Override
    public void levelRestarted(int levelNumber, Level level) {
        levelStarted(levelNumber, level);
    }

    @Override
    public void boardRestored(Level level) {
        if (visible) {
            screen.showLevel(level);
        }
    }

    @Override
    public void playerMoved(Direction direction) {
        if (visible) {
            screen.playerMoved();
        }
    }

    @Override
    public void boxPushed(Direction direction) {
        if (visible) {
            screen.boardChanged();
        }
    }

    @Override
    public void boxStored(int storedBoxes, int totalBoxes) {
        if (visible) {
            screen.boardChanged();
        }
    }

    @Override
    public void doorOpened(Visibility visibility) {
        if (visible) {
            screen.boardChanged();
        }
    }
}