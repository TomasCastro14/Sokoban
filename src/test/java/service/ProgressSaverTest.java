package service;

import factory.TestLevels;
import model.board.Direction;
import model.game.Game;
import model.game.Level;
import model.game.LevelStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import port.LevelRecord;
import port.Progress;
import port.ProgressRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgressSaverTest {

    private static final int LEVEL_COUNT = 5;

  
    private static final class FakeRepository implements ProgressRepository {
        private final Progress stored;
        private final List<Progress> saved = new ArrayList<>();

        FakeRepository(Progress stored) {
            this.stored = stored;
        }

        @Override
        public Progress load() {
            return stored;
        }

        @Override
        public void save(Progress progress) {
            saved.add(progress);
        }
    }

    private static Level oneMoveLevel() {
        return TestLevels.level("######", "#@$. #", "######");
    }


    private static Level brokenBoxLevel() {
        return TestLevels.level("#######", "#@1  :#", "#######");
    }

    private static LevelStats statsOf(int moves, int pushes) {
        LevelStats stats = new LevelStats();
        for (int i = 0; i < moves; i++) {
            stats.addMove();
        }
        for (int i = 0; i < pushes; i++) {
            stats.addPush();
        }
        return stats;
    }

    @Test
    @DisplayName("1. Al ganar un nivel del juego se llama a save con el siguiente nivel desbloqueado")
    void winningLevelSavesProgress() {
        FakeRepository repository = new FakeRepository(Progress.initial());
        Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
        game.addLevelCompletedListener(new ProgressSaver(repository, LEVEL_COUNT));
        game.start();

        game.move(Direction.RIGHT);

        assertEquals(1, repository.saved.size());
        Progress saved = repository.saved.get(0);
        assertEquals(2, saved.unlockedLevel());
        assertEquals(List.of(new LevelRecord(1, 1, 1)), saved.levels());
    }

    @Test
    @DisplayName("2. Se guarda el numero del nivel que se gano, no siempre el primero")
    void savesTheLevelThatWasWon() {
        FakeRepository repository = new FakeRepository(Progress.initial());
        Game game = new Game(n -> oneMoveLevel(), LEVEL_COUNT);
        game.addLevelCompletedListener(new ProgressSaver(repository, LEVEL_COUNT));
        game.start(3);

        game.move(Direction.RIGHT);

        assertEquals(4, repository.saved.get(0).unlockedLevel());
        assertEquals(3, repository.saved.get(0).levels().get(0).levelNumber());
    }

    @Test
    @DisplayName("3. Perder un nivel no guarda nada")
    void losingSavesNothing() {
        FakeRepository repository = new FakeRepository(Progress.initial());
        Game game = new Game(n -> brokenBoxLevel(), LEVEL_COUNT);
        game.addLevelCompletedListener(new ProgressSaver(repository, LEVEL_COUNT));
        game.start();

        game.move(Direction.RIGHT);
        game.move(Direction.RIGHT);

        assertTrue(repository.saved.isEmpty());
    }

    @Test
    @DisplayName("4. Parte del progreso ya guardado y conserva el mejor resultado")
    void keepsPreviouslySavedBest() {
        LevelRecord best = new LevelRecord(1, 10, 2);
        FakeRepository repository = new FakeRepository(new Progress(3, List.of(best)));
        ProgressSaver saver = new ProgressSaver(repository, LEVEL_COUNT);

        saver.levelCompleted(1, statsOf(25, 6));

        Progress saved = repository.saved.get(0);
        assertEquals(3, saved.unlockedLevel());
        assertEquals(List.of(best), saved.levels());
    }
}