package model.game.loss;

import model.board.Cell;
import model.game.Level;
import model.tile.StorageResult;

/**
 * Template Method: walks the board once and each storage rule only says
 * which storage result means a loss.
 */
public abstract class StorageLossCondition implements LossCondition {

    @Override
    public final boolean isMet(Level level) {
        for (Cell cell : level.getBoard().cells()) {
            StorageResult result = cell.getTile().getStorageResult();
            if (result != null && matches(result)) {
                return true;
            }
        }
        return false;
    }

    /** Hook: true when this storage result makes the level lost. */
    protected abstract boolean matches(StorageResult result);
}
