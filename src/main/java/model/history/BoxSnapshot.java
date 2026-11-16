package model.history;

import model.board.Board;
import model.board.Position;
import model.box.Box;

import java.util.Objects;

public final class BoxSnapshot implements Memento {
    private final Board board;
    private final Box box;
    private final Position position;
    private final Memento boxState;

    public BoxSnapshot(Board board, Box box, Position position) {
        this.board = Objects.requireNonNull(board, "board");
        this.box = Objects.requireNonNull(box, "box");
        this.position = Objects.requireNonNull(position, "position");
        this.boxState = box.save();
    }

    @Override
    public void restore() {
        boxState.restore();
        board.placeBox(position, box);
    }
}