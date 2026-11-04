package controller;

import factory.TestLevels;
import model.board.Direction;
import model.game.Game;
import model.game.Level;
import model.game.state.GameCompletedState;
import model.game.state.PlayingState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.Navigator;
import view.Screen;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelProgressionTest {

    private static final int LEVEL_COUNT = 3;

    private static final class FakeNavigator implements Navigator {
        final List<Screen> shown = new ArrayList<>();
        int backs;

        @Override
        public void show(Screen screen) {
            shown.add(screen);
        }

        @Override
        public void back() {
            backs++;
        }
    }

    private static Level oneMoveLevel() {
        return TestLevels.level("######", "#@$. #", "######");
    }

    private final FakeNavigator navigator = new FakeNavigator();
    private final Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);

    LevelProgressionTest() {
        new LevelProgression(game, navigator, Runnable::run);
    }

    @Test
    @DisplayName("Al ganar un nivel se pasa al siguiente por el evento levelWon")
    void winningLoadsTheNextLevel() {
        game.start();

        game.move(Direction.RIGHT);

        assertEquals(2, game.getCurrentLevelNumber());
        assertSame(PlayingState.INSTANCE, game.getState());
        assertTrue(navigator.shown.isEmpty());
    }

    @Test
    @DisplayName("Al ganar el ultimo nivel termina el juego y se vuelve al menu principal")
    void winningTheLastLevelEndsTheGame() {
        game.start(LEVEL_COUNT);

        game.move(Direction.RIGHT);

        assertSame(GameCompletedState.INSTANCE, game.getState());
        assertEquals(List.of(Screen.MAIN_MENU), navigator.shown);
    }

    @Test
    @DisplayName("Perder no cambia de nivel ni de pantalla")
    void losingDoesNotNavigate() {
        Game losing = new Game(n -> TestLevels.level("######", "#@3. #", "######"), LEVEL_COUNT);
        new LevelProgression(losing, navigator, Runnable::run);
        losing.start();

        losing.move(Direction.RIGHT);

        assertEquals(1, losing.getCurrentLevelNumber());
        assertTrue(navigator.shown.isEmpty());
    }

    @Test
    @DisplayName("El paso de nivel espera a que termine el evento actual")
    void advanceWaitsForTheCurrentEvent() {
        List<Runnable> pending = new ArrayList<>();
        Game deferred = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
        new LevelProgression(deferred, navigator, pending::add);
        deferred.start();

        deferred.move(Direction.RIGHT);
        assertEquals(1, deferred.getCurrentLevelNumber());

        pending.forEach(Runnable::run);
        assertEquals(2, deferred.getCurrentLevelNumber());
    }
}