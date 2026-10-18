package model.game.loss;

import model.board.Board;
import model.board.Position;
import model.game.Level;
import model.tile.StorageResult;

public class EarlyKeyStorageCondition implements LossCondition {

    /** Lost as soon as a key box reaches its point before opening its door (KeyBox decides it). */
    @Override
    public boolean isMet(Level level) {
        Board board = level.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getTile(new Position(row, col)).getStorageResult() == StorageResult.KEY_NOT_USED) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public LossReason getReason() {
        return LossReason.EARLY_KEY_STORAGE;
    }
}
