package model.game.event;

import model.game.LevelStats;
import model.game.loss.LossReason;

/** Observer del resultado de un nivel. */
public interface LevelOutcomeListener {

    /** Se guardaron todas las cajas. */
    void levelWon(LevelStats stats);

    /** Se cumplió una condición de derrota. */
    void levelLost(LossReason reason);
}