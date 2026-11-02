package model.game.event;

import model.game.Level;

public interface LevelFlowListener {
    void levelStarted(int levelNumber, Level level);

    /** Se reinició el nivel actual desde cero. */
    void levelRestarted(int levelNumber, Level level);

    /** Se deshizo una jugada y el tablero volvió a un estado anterior. */
    void boardRestored(Level level);
}