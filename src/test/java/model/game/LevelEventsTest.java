package model.game;

import model.game.score.LevelGoals;
import factory.TestLevels;
import model.board.Board;
import model.board.Direction;
import model.board.Visibility;
import model.game.event.BoardListener;
import model.game.event.LevelOutcomeListener;
import model.game.event.TimeListener;
import model.game.loss.LossReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelEventsTest {

    private static final int TIME_LIMIT = 3;

    private static Level levelWithoutLossListeners(String... rows) {
        Board board = TestLevels.level(rows).getBoard();
        return new Level("sin perdidas", TIME_LIMIT, new LevelGoals(100, 100, 100), board);
    }

    private static final class PushRecorder implements BoardListener {
        final List<Direction> pushes = new ArrayList<>();

        @Override
        public void boxPushed(Direction direction) {
            pushes.add(direction);
        }

        @Override
        public void playerMoved(Direction direction) {
        }

        @Override
        public void boxStored(int storedBoxes, int totalBoxes) {
        }

        @Override
        public void doorOpened(Visibility visibility) {
        }
    }

    @Test
    @DisplayName("Level publica secondElapsed con el tiempo que queda")
    void publishesSecondElapsed() {
        Level level = levelWithoutLossListeners("######", "#@$ .#", "######");
        List<Integer> remaining = new ArrayList<>();
        level.addTimeListener(remaining::add);

        level.elapseSeconds(1);
        level.elapseSeconds(1);

        assertEquals(List.of(2, 1), remaining);
    }

    @Test
    @DisplayName("Level publica boxPushed en cada empuje")
    void publishesBoxPushed() {
        Level level = levelWithoutLossListeners("#######", "#@3 . #", "#######");
        PushRecorder recorder = new PushRecorder();
        level.addBoardListener(recorder);

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(List.of(Direction.RIGHT, Direction.RIGHT), recorder.pushes);
    }

    @Test
    @DisplayName("Sin listeners de perdida, Level no pierde por tiempo")
    void doesNotEvaluateTimeLoss() {
        Level level = levelWithoutLossListeners("######", "#@$ .#", "######");

        level.elapseSeconds(TIME_LIMIT);

        assertEquals(0, level.getRemainingSeconds());
        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("Sin listeners de perdida, Level no pierde por movimiento")
    void doesNotEvaluateMoveLoss() {
        Level level = levelWithoutLossListeners("#######", "#@3 . #", "#######");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(LevelStatus.PLAYING, level.getStatus());
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("Se puede suscribir un listener de perdida sin tocar Level")
    void subscribeLossListener() {
        Level level = levelWithoutLossListeners("######", "#@$ .#", "######");
        level.addTimeListener(remaining -> level.declareLoss(LossReason.TIME_OUT));

        level.elapseSeconds(1);

        assertEquals(LossReason.TIME_OUT, level.getLossReason());
    }

    @Test
    @DisplayName("Al quitar el listener de perdida deja de actuar")
    void removeLossListener() {
        Level level = levelWithoutLossListeners("######", "#@$ .#", "######");
        TimeListener alwaysLoses = remaining -> level.declareLoss(LossReason.TIME_OUT);
        level.addTimeListener(alwaysLoses);

        level.removeTimeListener(alwaysLoses);
        level.elapseSeconds(1);

        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("Una perdida avisa levelLost una sola vez aunque se declare dos veces")
    void lossIsDeclaredOnce() {
        Level level = levelWithoutLossListeners("######", "#@$ .#", "######");
        List<LossReason> losses = new ArrayList<>();
        level.addOutcomeListener(new LevelOutcomeListener() {
            @Override
            public void levelWon(LevelStats stats) {
            }

            @Override
            public void levelLost(LossReason reason) {
                losses.add(reason);
            }
        });

        level.declareLoss(LossReason.WRONG_STORAGE);
        level.declareLoss(LossReason.TIME_OUT);

        assertEquals(List.of(LossReason.WRONG_STORAGE), losses);
    }
}