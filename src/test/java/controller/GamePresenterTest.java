package controller;

import factory.TestLevels;
import model.board.Direction;
import model.game.Game;
import model.game.Level;
import model.game.state.PausedState;
import model.game.state.WonState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.gameplay.GameScreen;
import view.gameplay.ScreenLifecycleListener;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GamePresenterTest {

    private static final int LEVEL_COUNT = 5;

    private static final class FakeScreen implements GameScreen {
        final List<String> calls = new ArrayList<>();
        boolean paused;

        @Override
        public void showLevel(Level level) {
            calls.add("showLevel");
        }

        @Override
        public void playerMoved() {
            calls.add("playerMoved");
        }

        @Override
        public void boardChanged() {
            calls.add("boardChanged");
        }

        @Override
        public void showPaused(boolean paused) {
            this.paused = paused;
        }

        @Override
        public void setLifecycleListener(ScreenLifecycleListener listener) {
        }
    }

    private final FakeScreen screen = new FakeScreen();

    private GamePresenter presenterFor(Game game) {
        return new GamePresenter(game, screen, new InputController(game), new GameClock(game));
    }

    private static Game startedGame(Level level) {
        Game game = new Game(n -> level, LEVEL_COUNT);
        game.start();
        return game;
    }

    @Test
    @DisplayName("ESC en el juego pausa y muestra la pausa; otro ESC reanuda")
    void escapePausesAndResumes() {
        Game game = startedGame(TestLevels.level("########", "#@     #", "#  $.  #", "########"));
        GamePresenter presenter = presenterFor(game);

        presenter.onEscape();
        assertSame(PausedState.INSTANCE, game.getState());
        assertTrue(screen.paused);

        presenter.onEscape();
        assertFalse(screen.paused);
    }

    @Test
    @DisplayName("El presenter no pasa de nivel por su cuenta al ganar")
    void presenterDoesNotAdvanceLevels() {
        Game game = startedGame(TestLevels.level("######", "#@$. #", "######"));
        presenterFor(game);

        game.move(Direction.RIGHT);

        assertSame(WonState.INSTANCE, game.getState());
        assertEquals(1, game.getCurrentLevelNumber());
    }

    @Test
    @DisplayName("Con la pantalla oculta, el presenter no le pide nada a la vista")
    void hiddenScreenIsNotDrawn() {
        Game game = startedGame(TestLevels.level("######", "#@$. #", "######"));
        presenterFor(game);

        game.move(Direction.RIGHT);

        assertFalse(screen.calls.contains("playerMoved"));
    }
}