package model.history;

import model.board.MoveResult;

public interface Command {

    MoveResult execute();

    void undo();
}