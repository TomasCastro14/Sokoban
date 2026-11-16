package model.game.loss;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.game.Level;
import model.game.LevelStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrokenBoxConditionTest {

    private final BrokenBoxCondition condition = new BrokenBoxCondition();

    @Test
    @DisplayName("Se pierde en el empuje que rompe la caja debil")
    void losesWhenTheBoxBreaks() {
        Level level = TestLevels.level("#######", "#@1  :#", "#######");
        level.move(Direction.RIGHT);

        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));

        assertTrue(condition.isMet(level));
        assertEquals(LevelStatus.LOST, level.getStatus());
        assertEquals(LossReason.BROKEN_BOX, level.getLossReason());
    }

    @Test
    @DisplayName("Un empuje normal de una caja debil no es una perdida")
    void normalPushIsNotLost() {
        Level level = TestLevels.level("#######", "#@2  :#", "#######");

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));

        assertFalse(condition.isMet(level));
        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("Caminar sin empujar no es una perdida")
    void walkingIsNotLost() {
        Level level = TestLevels.level("#######", "#@    #", "#  1 :#", "#######");

        level.move(Direction.RIGHT);

        assertFalse(condition.isMet(level));
    }
}
