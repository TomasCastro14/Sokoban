package model.game.loss;

import factory.TestLevels;
import model.board.Board;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Push;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractLossConditionTest {

    private static final class RecordingCondition extends AbstractLossCondition {
        final List<String> steps = new ArrayList<>();
        private final boolean applies;
        private final boolean holds;

        RecordingCondition(boolean applies, boolean holds) {
            this.applies = applies;
            this.holds = holds;
        }

        @Override
        protected boolean appliesTo(Push push) {
            steps.add("appliesTo " + push.result());
            return applies;
        }

        @Override
        protected boolean holds(Board board, Push push) {
            steps.add("holds");
            return holds;
        }

        @Override
        public LossReason getReason() {
            return LossReason.DEADLOCK;
        }
    }

    private static Level pushedOnce() {
        Level level = TestLevels.level("#######", "#@$  .#", "#######");
        level.move(Direction.RIGHT);
        return level;
    }

    @Test
    @DisplayName("Sin empuje no se llama a ningun hook")
    void withoutPushNoHookRuns() {
        Level level = TestLevels.level("#######", "#@   .#", "#  $  #", "#######");
        RecordingCondition condition = new RecordingCondition(true, true);
        level.move(Direction.RIGHT);

        assertFalse(condition.isMet(level));
        assertTrue(condition.steps.isEmpty());
    }

    @Test
    @DisplayName("Si no aplica al empuje, no se evalua si se cumple")
    void notApplicableSkipsHolds() {
        RecordingCondition condition = new RecordingCondition(false, true);

        assertFalse(condition.isMet(pushedOnce()));
        assertEquals(List.of("appliesTo " + MoveResult.PUSHED), condition.steps);
    }

    @Test
    @DisplayName("Si aplica, el resultado es el de holds, en ese orden")
    void applicableAsksHolds() {
        RecordingCondition met = new RecordingCondition(true, true);
        RecordingCondition notMet = new RecordingCondition(true, false);
        Level level = pushedOnce();

        assertTrue(met.isMet(level));
        assertFalse(notMet.isMet(level));
        assertEquals(List.of("appliesTo " + MoveResult.PUSHED, "holds"), met.steps);
        assertEquals(met.steps, notMet.steps);
    }

    @Test
    @DisplayName("Todas las condiciones de empuje heredan el mismo flujo")
    void everyPushConditionUsesTheTemplate() {
        assertInstanceOf(AbstractLossCondition.class, new BrokenBoxCondition());
        assertInstanceOf(AbstractLossCondition.class, new DeadlockCondition());
        assertInstanceOf(AbstractLossCondition.class, new WrongStorageCondition());
        assertInstanceOf(AbstractLossCondition.class, new EarlyKeyStorageCondition());
    }
}
