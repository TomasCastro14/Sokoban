package controller;

import factory.TestLevels;
import model.board.Direction;
import model.game.Game;
import model.game.loss.LossReason;
import model.game.progress.Progress;
import model.game.score.Goal;
import model.game.score.ScoreResult;
import model.game.score.StandardScoreCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.Navigator;
import view.Screen;
import view.gameplay.dialog.LevelDialogs;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelDialogPresenterTest {

    private static final int LEVEL_COUNT = 3;
    private static final String[] ONE_PUSH_WIN = {"######", "#@$. #", "######"};
    private static final String[] ONE_PUSH_LOSS = {"######", "#@3. #", "######"};

    private static final class FakeDialogs implements LevelDialogs {
        final List<ScoreResult> victories = new ArrayList<>();
        final List<Boolean> records = new ArrayList<>();
        int defeats;

        @Override
        public void showGoals(String levelName, List<Goal> goals, Runnable onPlay) {
        }

        @Override
        public void showDefeat(LossReason reason, Runnable onLeave, Runnable onRetry) {
            defeats++;
        }

        @Override
        public void showVictory(ScoreResult result, boolean newRecord, Runnable onRetry, Runnable onNext) {
            victories.add(result);
            records.add(newRecord);
        }

        @Override
        public void showPause(Runnable onLeave, Runnable onRestart, Runnable onResume) {
        }

        @Override
        public void close() {
        }
    }

    private static final Navigator NO_NAVIGATION = new Navigator() {
        @Override
        public void show(Screen screen) {
        }

        @Override
        public void back() {
        }
    };

    private final FakeDialogs dialogs = new FakeDialogs();
    private final Progress progress = new Progress(LEVEL_COUNT);

    private Game gameWith(String... rows) {
        Game game = new Game(n -> TestLevels.level(rows), LEVEL_COUNT);
        PlaySession session = new PlaySession(new InputController(game), new GameClock(game));
        new LevelDialogPresenter(game, dialogs, session, new StandardScoreCalculator(), NO_NAVIGATION, progress);
        return game;
    }

    @Test
    @DisplayName("1. Ganar registra las estrellas del nivel y desbloquea el siguiente")
    void winningRecordsProgress() {
        Game game = gameWith(ONE_PUSH_WIN);
        game.start();

        game.move(Direction.RIGHT);

        ScoreResult shown = dialogs.victories.get(0);
        assertEquals(3, shown.stars());
        assertEquals(3, progress.getStars(1));
        assertEquals(shown.score(), progress.getBestScore(1).orElseThrow());
        assertTrue(progress.isUnlocked(2));
        assertEquals(List.of(true), dialogs.records, "la primera victoria de la sesion es record");
    }

    @Test
    @DisplayName("2. Perder no toca el progreso")
    void losingDoesNotTouchProgress() {
        Game game = gameWith(ONE_PUSH_LOSS);
        game.start();

        game.move(Direction.RIGHT);

        assertEquals(1, dialogs.defeats);
        assertEquals(0, progress.getStars(1));
        assertFalse(progress.isUnlocked(2));
        assertTrue(progress.getBestScore(1).isEmpty());
    }

    @Test
    @DisplayName("3. Salir o reiniciar a mitad de nivel no toca el progreso")
    void leavingHalfwayDoesNotTouchProgress() {
        Game game = gameWith("########", "#@  $. #", "########");
        game.start();

        game.move(Direction.RIGHT);
        game.restart();
        game.start(1);

        assertTrue(dialogs.victories.isEmpty());
        assertEquals(1, progress.getLastUnlockedLevel());
    }

    @Test
    @DisplayName("4. La pantalla de victoria dice si se supero el record de la sesion")
    void victoryTellsWhetherItIsARecord() {
        Game game = gameWith(ONE_PUSH_WIN);
        game.start();
        game.tick();
        game.move(Direction.RIGHT);          // record: primera victoria
        int firstScore = progress.getBestScore(1).orElseThrow();

        game.restart();
        game.tick();
        game.tick();
        game.move(Direction.RIGHT);          // menos tiempo restante: menos puntos

        game.restart();
        game.move(Direction.RIGHT);          // mas tiempo restante: mas puntos

        assertEquals(List.of(true, false, true), dialogs.records);
        assertTrue(progress.getBestScore(1).orElseThrow() > firstScore);
    }
}
