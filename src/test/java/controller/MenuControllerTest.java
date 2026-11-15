package controller;

import factory.TestLevels;
import model.board.Direction;
import model.game.Game;
import model.game.Level;
import model.game.progress.Progress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.Navigator;
import view.Screen;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuControllerTest {

    private static final int LEVEL_COUNT = 3;

    private final List<Integer> built = new ArrayList<>();
    private final List<Screen> shown = new ArrayList<>();
    private final Progress progress = new Progress(LEVEL_COUNT);
    private final Game game = new Game(this::buildFromText, LEVEL_COUNT);
    private final MenuController menu = new MenuController(game, progress, new Navigator() {
        @Override
        public void show(Screen screen) {
            shown.add(screen);
        }

        @Override
        public void back() {
        }
    }, () -> { });

    /** Stands for the LevelLoader: every call builds a brand new level, as reading the .txt does. */
    private Level buildFromText(int number) {
        built.add(number);
        return TestLevels.level("########", "#@  $. #", "########");
    }

    @Test
    @DisplayName("1. Continuar arranca el ultimo nivel desbloqueado")
    void continuePlaysTheLastUnlockedLevel() {
        progress.registerResult(1, 1, 0);

        menu.onContinue();

        assertEquals(2, game.getCurrentLevelNumber());
        assertEquals(List.of(Screen.GAME), shown);
    }

    @Test
    @DisplayName("2. Salir a mitad de nivel y continuar lo arranca de cero, construido de nuevo")
    void continueNeverResumesAHalfPlayedLevel() {
        menu.onContinue();
        Level halfPlayed = game.getCurrentLevel();
        game.move(Direction.RIGHT);
        assertEquals(1, halfPlayed.getStats().getMoves());

        menu.onContinue();

        assertNotSame(halfPlayed, game.getCurrentLevel());
        assertEquals(0, game.getCurrentLevel().getStats().getMoves());
        assertEquals(List.of(1, 1), built);
    }

    @Test
    @DisplayName("3. Reiniciar un nivel tambien lo vuelve a construir")
    void restartRebuildsTheLevel() {
        menu.onLevelChosen(1);
        Level first = game.getCurrentLevel();

        game.restart();

        assertNotSame(first, game.getCurrentLevel());
        assertEquals(List.of(1, 1), built);
    }

    @Test
    @DisplayName("4. Un nivel bloqueado no se puede elegir")
    void lockedLevelIsIgnored() {
        menu.onLevelChosen(3);

        assertFalse(game.hasStarted());
        assertTrue(shown.isEmpty());
    }
}
