package factory;

import model.game.loss.BrokenBoxCondition;
import model.game.loss.DeadlockCondition;
import model.game.loss.EarlyKeyStorageCondition;
import model.game.loss.LossRules;
import model.game.loss.TimeOutCondition;
import model.game.loss.WrongStorageCondition;

import java.util.List;

public final class StandardLossRules {

    private StandardLossRules() {
    }

    public static LossRules create() {
        return new LossRules(
                List.of(new BrokenBoxCondition(), new WrongStorageCondition(), new EarlyKeyStorageCondition(),
                        new DeadlockCondition()),
                List.of(new TimeOutCondition()));
    }
}
