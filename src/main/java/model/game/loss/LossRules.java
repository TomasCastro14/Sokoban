package model.game.loss;

import java.util.List;

public record LossRules(List<LossCondition> onPush, List<LossCondition> onTime) {

    public LossRules {
        onPush = List.copyOf(onPush);
        onTime = List.copyOf(onTime);
    }

    public static LossRules none() {
        return new LossRules(List.of(), List.of());
    }
}
