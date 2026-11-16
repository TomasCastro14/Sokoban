package model.tile.effect;

import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.Occupant;

/** Portal: whoever enters appears on the pair portal. Arriving there does not teleport again. */
public class TeleportEffect implements EntryEffect {
    private final Position exit;

    public TeleportEffect(Position exit) {
        this.exit = exit;
    }

    @Override
    public Position apply(Occupant occupant, Position at, Direction direction, Board board) {
        return exit.move(Direction.DOWN);
    }
}
