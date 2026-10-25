package model.game.loss;

import model.board.Board;
import model.board.Position;
import model.game.Level;
import model.tile.StorageResult;

/**
 * Template Method: walks the board once and each storage rule only says
 * which storage result means a loss.
 */
public abstract class StorageLossCondition implements LossCondition {

    @Override
    public final boolean isMet(Level level) {
        Board board = level.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                StorageResult result = board.getTile(new Position(row, col)).getStorageResult();
                if (result != null && matches(result)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Hook: true when this storage result makes the level lost. */
    protected abstract boolean matches(StorageResult result);
}
