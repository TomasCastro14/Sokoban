package factory;

import model.board.Direction;
import model.game.Level;
import model.game.LevelStatus;
import model.game.loss.LossCondition;
import model.game.loss.LossReason;
import model.game.loss.LossRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelBuilderLossRulesTest {

    private static final String[] CORNER = {"#####", "#   #", "#$  #", "#@ .#", "#####"};

    private static final class StubCondition implements LossCondition {
        int evaluations;
        private final boolean met;

        StubCondition(boolean met) {
            this.met = met;
        }

        @Override
        public boolean isMet(Level level) {
            evaluations++;
            return met;
        }

        @Override
        public LossReason getReason() {
            return LossReason.WRONG_STORAGE;
        }
    }

    private static Level build(LossRules rules, String... rows) throws Exception {
        return new LevelLoader(rules).parse(TestLevels.text(rows));
    }

    @Test
    @DisplayName("El nivel usa la lista inyectada: un stub se evalua en cada empuje")
    void injectedConditionIsEvaluated() throws Exception {
        StubCondition stub = new StubCondition(false);
        Level level = build(new LossRules(List.of(stub), List.of()), CORNER);

        level.move(Direction.UP);

        assertEquals(1, stub.evaluations);
        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("Una condicion falsa cumplida hace perder con su motivo")
    void injectedConditionDecidesTheLoss() throws Exception {
        Level level = build(new LossRules(List.of(new StubCondition(true)), List.of()), "#######", "#@$  .#", "#######");

        level.move(Direction.RIGHT);

        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("Con una lista vacia nunca se pierde, ni en una esquina ni por tiempo")
    void emptyListNeverLoses() throws Exception {
        Level level = build(LossRules.none(), CORNER);

        level.move(Direction.UP);
        level.elapseSeconds(TestLevels.TIME);

        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("Las condiciones de tiempo se evaluan con el reloj, no con los empujes")
    void timeConditionsRunOnTicks() throws Exception {
        StubCondition onTime = new StubCondition(false);
        Level level = build(new LossRules(List.of(), List.of(onTime)), CORNER);

        level.move(Direction.UP);
        level.elapseSeconds(1);

        assertEquals(1, onTime.evaluations);
    }
}
