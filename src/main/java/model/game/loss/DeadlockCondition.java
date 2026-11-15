package model.game.loss;

import model.board.Board;
import model.board.MoveResult;
import model.board.Position;
import model.board.Push;

import java.util.Arrays;

public class DeadlockCondition extends AbstractLossCondition {

    @Override
    protected boolean appliesTo(Push push) {
        return push.result() == MoveResult.PUSHED;
    }

    @Override
    protected boolean holds(Board board, Push push) {
        Position box = push.boxPosition();
        return board.hasLooseBoxAt(box) && Arrays.stream(Corner.values())
                .anyMatch(corner -> isCorner(board, box, corner) || isBlock(board, box, corner));
    }

    private static boolean isCorner(Board board, Position box, Corner corner) {
        return board.isBoundaryAt(corner.verticalOf(box)) && board.isBoundaryAt(corner.horizontalOf(box));
    }

    private static boolean isBlock(Board board, Position box, Corner corner) {
        return isObstacle(board, corner.verticalOf(box))
                && isObstacle(board, corner.horizontalOf(box))
                && isObstacle(board, corner.diagonalOf(box));
    }

    private static boolean isObstacle(Board board, Position p) {
        // Una caja ya guardada queda absorbida por su punto (se puede pasar por encima),
        // asi que no bloquea: solo cuentan las cajas sueltas.
        return board.isBoundaryAt(p) || board.hasLooseBoxAt(p);
    }

    @Override
    public LossReason getReason() {
        return LossReason.DEADLOCK;
    }
}
