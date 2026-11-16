package model.game.event;

import model.game.LevelStats;
import model.game.loss.LossReason;


public interface LevelOutcomeListener {


    void levelWon(LevelStats stats);
   
    void levelLost(LossReason reason);
}