package model.history;

import model.board.Direction;
import model.board.MoveResult;

import java.util.Objects;

public final class MoveCommand implements Command {
    private final MoveReceiver receiver;
    private final Direction direction;
    private LevelMemento before = LevelMemento.EMPTY;

    public MoveCommand(MoveReceiver receiver, Direction direction) {
        this.receiver = Objects.requireNonNull(receiver, "receiver");
        this.direction = Objects.requireNonNull(direction, "direction");
    }

    @Override
    public MoveResult execute() {
        before = receiver.save();
        return receiver.play(direction);
    }

    @Override
    public void undo() {
        before.restore();
    }
}