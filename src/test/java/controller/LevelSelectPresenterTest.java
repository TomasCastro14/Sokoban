package controller;

import model.game.progress.Progress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.menus.LevelSelectScreen;
import view.menus.LevelSelectView.LevelInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelSelectPresenterTest {

    private static final class FakeScreen implements LevelSelectScreen {
        List<LevelInfo> shown = List.of();
        Runnable onReset;
        int updates;

        @Override
        public void showLevels(List<LevelInfo> levels) {
            shown = levels;
            updates++;
        }

        @Override
        public void setOnResetProgress(Runnable onReset) {
            this.onReset = onReset;
        }
    }

    private final Progress progress = new Progress(3);
    private final FakeScreen screen = new FakeScreen();

    LevelSelectPresenterTest() {
        new LevelSelectPresenter(progress, screen);
    }

    @Test
    @DisplayName("1. Al crearse muestra el estado inicial: nivel 1 abierto, el resto con candado, sin estrellas")
    void showsTheInitialState() {
        assertEquals(List.of(
                new LevelInfo("Nivel 1", 0, true),
                new LevelInfo("Nivel 2", 0, false),
                new LevelInfo("Nivel 3", 0, false)), screen.shown);
    }

    @Test
    @DisplayName("2. Cuando cambia el progreso la vista recibe las nuevas estrellas y candados")
    void refreshesWhenProgressChanges() {
        progress.registerResult(1, 2, 100);

        assertEquals(List.of(
                new LevelInfo("Nivel 1", 2, true),
                new LevelInfo("Nivel 2", 0, true),
                new LevelInfo("Nivel 3", 0, false)), screen.shown);
    }

    @Test
    @DisplayName("3. Reiniciar desde la vista (ya confirmado) vuelve al estado inicial y la refresca")
    void resetFromTheView() {
        progress.registerResult(1, 3, 100);
        int before = screen.updates;

        screen.onReset.run();

        assertEquals(1, progress.getLastUnlockedLevel());
        assertEquals(before + 1, screen.updates);
        assertFalse(screen.shown.get(1).unlocked());
        assertEquals(0, screen.shown.get(0).stars());
    }
}
