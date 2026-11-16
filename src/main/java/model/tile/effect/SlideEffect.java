package model.tile.effect;

import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.Occupant;

/**
 * Ice: whoever enters keeps moving one more cell in the same direction, as long as that cell can be entered.
 * The next cell's own effect decides what happens there: more ice keeps sliding, floor stops the slide on
 * that floor cell. So a 1-cell ice path moves the occupant exactly one cell past the ice, and a long path
 * is the same step repeated.
 * Always ends: every step moves forward in a fixed direction and the board is finite and closed by walls.
 */
public class SlideEffect implements EntryEffect {

    @Override
    public Position apply(Occupant occupant, Position at, Direction direction, Board board) {
        Position next = at.move(direction);
        if (!board.canEnter(occupant, next)) {
            return at; // blocked or at the edge of the board: stays on this ice cell
        }
        return board.getTile(next).arrivalFor(occupant, next, direction, board);
    }
}
