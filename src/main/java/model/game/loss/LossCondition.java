package model.game.loss;

import model.game.Level;

public interface LossCondition {

    boolean isMet(Level level);

    LossReason getReason();
}
