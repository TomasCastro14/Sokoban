package model.tile.effect;

import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.Occupant;

/**
 * Strategy: where an occupant ends up after entering a tile.
 * It only answers; the board validates the answer and moves the occupant.
 */
public interface EntryEffect {

    Position apply(Occupant occupant, Position at, Direction direction, Board board);
}
