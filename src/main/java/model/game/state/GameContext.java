package model.game.state;

import model.board.Direction;
import model.game.loss.LossReason;

public interface GameContext {

    GameState playMove(Direction direction);

    GameState elapseSecond();

    boolean undoLastMove();

    boolean canRecoverFrom(LossReason reason);

    void reloadLevel();

    void loadNextLevel();

    boolean isLastLevel();

    void completeGame();

    void announcePause();

    void announceResume();
}