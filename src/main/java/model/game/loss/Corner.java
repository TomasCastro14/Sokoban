package model.game.loss;

import model.board.Direction;
import model.board.Position;

enum Corner {
    UP_LEFT(Direction.UP, Direction.LEFT),
    UP_RIGHT(Direction.UP, Direction.RIGHT),
    DOWN_LEFT(Direction.DOWN, Direction.LEFT),
    DOWN_RIGHT(Direction.DOWN, Direction.RIGHT);

    private final Direction vertical;
    private final Direction horizontal;

    Corner(Direction vertical, Direction horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    Position verticalOf(Position p) {
        return p.move(vertical);
    }

    Position horizontalOf(Position p) {
        return p.move(horizontal);
    }

    Position diagonalOf(Position p) {
        return p.move(vertical).move(horizontal);
    }
}
