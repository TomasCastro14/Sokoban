package model.game.loss;

import model.board.Board;
import model.board.MoveResult;
import model.board.Push;
import model.tile.StorageResult;

public abstract class StorageLossCondition extends AbstractLossCondition {

    @Override
    protected final boolean appliesTo(Push push) {
        return push.result() == MoveResult.PUSHED;
    }

    @Override
    protected final boolean holds(Board board, Push push) {
        StorageResult result = board.getTile(push.boxPosition()).getStorageResult();
        return result != null && matches(result);
    }

    protected abstract boolean matches(StorageResult result);
}
