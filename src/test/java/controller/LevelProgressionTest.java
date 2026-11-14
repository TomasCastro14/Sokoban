package controller;

import factory.TestLevels;
import model.board.Direction;
import model.game.Game;
import model.game.Level;
import model.game.state.GameCompletedState;
import model.game.state.PlayingState;
import model.game.state.WonState;
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
        new LevelProgression(game, navigator);
    }

    @Test
    @DisplayName("Ganar un nivel no lo cambia solo: se espera a que el jugador pida el siguiente")
    void winningWaitsForNextLevel() {
        game.start();

        game.move(Direction.RIGHT);
        assertSame(WonState.INSTANCE, game.getState());
        assertEquals(1, game.getCurrentLevelNumber());

        game.nextLevel();
        assertEquals(2, game.getCurrentLevelNumber());
        assertSame(PlayingState.INSTANCE, game.getState());
        assertTrue(navigator.shown.isEmpty());
    }

    @Test
    @DisplayName("Al pasar despues del ultimo nivel termina el juego y se vuelve al menu principal")
    void finishingTheLastLevelEndsTheGame() {
        game.start(LEVEL_COUNT);

        game.move(Direction.RIGHT);
        assertTrue(navigator.shown.isEmpty());
        game.nextLevel();

        assertSame(GameCompletedState.INSTANCE, game.getState());
        assertEquals(List.of(Screen.MAIN_MENU), navigator.shown);
    }

    @Test
    @DisplayName("Perder no cambia de nivel ni de pantalla")
    void losingDoesNotNavigate() {
        Game losing = new Game(n -> TestLevels.level("######", "#@3. #", "######"), LEVEL_COUNT);
        new LevelProgression(losing, navigator);
        losing.start();

        losing.move(Direction.RIGHT);
        losing.nextLevel();

        assertEquals(1, losing.getCurrentLevelNumber());
        assertTrue(navigator.shown.isEmpty());
    }
}
