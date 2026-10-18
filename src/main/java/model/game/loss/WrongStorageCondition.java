package model.game.loss;

import model.board.Board;
import model.board.Position;
import model.game.Level;

public class WrongStorageCondition implements LossCondition {

    /** Lost as soon as any tile holds a box of another color. */
    @Override
    public boolean isMet(Level level) {
        Board board = level.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getTile(new Position(row, col)).holdsWrongBox()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public LossReason getReason() {
        return LossReason.WRONG_STORAGE;
    }
}
