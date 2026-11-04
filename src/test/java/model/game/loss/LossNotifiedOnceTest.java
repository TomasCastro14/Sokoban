package model.game.loss;

import factory.LevelLoader;
import factory.TestLevels;
import model.board.Direction;
import model.game.Level;
import model.game.LevelStats;
import model.game.event.LevelOutcomeListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LossNotifiedOnceTest {

    private static LossCondition alwaysMet(LossReason reason) {
        return new LossCondition() {
            @Override
            public boolean isMet(Level level) {
                return true;
            }

            @Override
            public LossReason getReason() {
                return reason;
            }
        };
    }

    @Test
    @DisplayName("Con dos condiciones cumplidas a la vez se avisa una sola perdida, la de la primera")
    void twoMetConditionsNotifyOnce() throws Exception {
        LossRules rules = new LossRules(
                List.of(alwaysMet(LossReason.BROKEN_BOX), alwaysMet(LossReason.DEADLOCK)), List.of());
        Level level = new LevelLoader(rules).parse(TestLevels.text("#######", "#@$  .#", "#######"));
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

        level.move(Direction.RIGHT);

        assertEquals(List.of(LossReason.BROKEN_BOX), losses);
    }
}
