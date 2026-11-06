package model.history;

import model.board.Direction;
import model.board.MoveResult;

public interface MoveReceiver {

    LevelMemento save();

    MoveResult play(Direction direction);
}