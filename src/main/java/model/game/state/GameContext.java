package model.game.state;

import model.board.Direction;


public interface GameContext {


    GameState playMove(Direction direction);


    GameState elapseSecond();

   
    boolean undoLastMove();

    void reloadLevel();

    void loadNextLevel();

    boolean isLastLevel();
}