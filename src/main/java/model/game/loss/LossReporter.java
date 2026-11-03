package model.game.loss;

public interface LossReporter {

    void declareLoss(LossReason reason);
}