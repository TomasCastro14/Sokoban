package model.board;

import java.util.Objects;

public record Push(Position boxPosition, MoveResult result) {

    public Push {
        Objects.requireNonNull(boxPosition, "boxPosition");
        Objects.requireNonNull(result, "result");
    }
}
