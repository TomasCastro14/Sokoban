package model.game.loss;

import factory.TestLevels;
import model.board.Direction;
import model.game.Level;
import model.game.LevelStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveLossListenerTest {

    private static Level wrongColorTwoPushesAway() {
        return TestLevels.level(
                "#######",
                "#@3 . #",
                "#######");
    }

    @Test
    @DisplayName("No se pierde un movimiento antes de dejar la caja en un punto de otro color")
    void notLostOneMoveBefore() {
        Level level = wrongColorTwoPushesAway();

        level.move(Direction.RIGHT);

        assertEquals(LevelStatus.PLAYING, level.getStatus());
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("Se pierde justo en el empuje que deja la caja en un punto de otro color")
    void lostExactlyOnTheLosingPush() {
        Level level = wrongColorTwoPushesAway();

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(LevelStatus.LOST, level.getStatus());
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("Guardar la KeyBox antes de abrir su puerta da derrota en ese empuje")
    void earlyKeyStorageLoses() {
        Level level = TestLevels.level(
                "########",
                "#@K ;  #",
                "#  a   #",
                "####A###",
                "########");

        level.move(Direction.RIGHT);
        assertEquals(LevelStatus.PLAYING, level.getStatus());
        level.move(Direction.RIGHT);

        assertEquals(LossReason.EARLY_KEY_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("Caminar sin empujar nunca evalua las perdidas por movimiento")
    void walkingDoesNotEvaluate() {
        Level level = TestLevels.level(
                "#######",
                "#@    #",
                "#  3. #",
                "#######");
        int[] evaluations = {0};
        LossCondition counting = new LossCondition() {
            @Override
            public boolean isMet(Level checked) {
                evaluations[0]++;
                return false;
            }

            @Override
            public LossReason getReason() {
                return LossReason.DEADLOCK;
            }
        };
        level.addBoardListener(new MoveLossListener(new LossEvaluator(level, List.of(counting), level)));

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(0, evaluations[0]);
    }
}