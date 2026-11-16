package model.game.loss;

import factory.TestLevels;
import model.game.Level;
import model.game.LevelStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeLossListenerTest {

    private static Level level() {
        return TestLevels.level(
                "######",
                "#@$ .#",
                "######");
    }

    @Test
    @DisplayName("Un segundo antes de agotar el tiempo se sigue jugando")
    void notLostOneSecondBefore() {
        Level level = level();

        level.elapseSeconds(TestLevels.TIME - 1);

        assertEquals(1, level.getRemainingSeconds());
        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("Se pierde por tiempo en el segundo en que el reloj llega a 0")
    void lostWhenTimeRunsOut() {
        Level level = level();
        level.elapseSeconds(TestLevels.TIME - 1);

        level.elapseSeconds(1);

        assertEquals(0, level.getRemainingSeconds());
        assertEquals(LevelStatus.LOST, level.getStatus());
        assertEquals(LossReason.TIME_OUT, level.getLossReason());
    }

    @Test
    @DisplayName("Despues de perder, el reloj no sigue corriendo")
    void clockStopsAfterLosing() {
        Level level = level();
        level.elapseSeconds(TestLevels.TIME);

        level.elapseSeconds(5);

        assertEquals(0, level.getRemainingSeconds());
        assertEquals(LossReason.TIME_OUT, level.getLossReason());
    }
}