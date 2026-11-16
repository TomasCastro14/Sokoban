package model.tile.effect;

import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.Occupant;

/** Most tiles: the occupant stays on the tile it entered. */
public class NoEffect implements EntryEffect {

    @Override
    public Position apply(Occupant occupant, Position at, Direction direction, Board board) {
        return at;
    }
}
