package model.history;

import java.util.ArrayDeque;
import java.util.Deque;

public final class UndoHistory {
    public static final int MAX_MOVES = 15;
    public static final int MOVES_PER_UNDO = 5;
    public static final int MAX_CONSECUTIVE_UNDOS = 3;

    private final Deque<Command> commands = new ArrayDeque<>();
    private int consecutiveUndos;

    public void record(Command command) {
        commands.push(command);
        if (commands.size() > MAX_MOVES) {
            commands.removeLast();
        }
        consecutiveUndos = 0;
    }

    public boolean undo() {
        if (!canUndo()) {
            return false;
        }
        oldestOfLastBlock().undo();
        consecutiveUndos++;
        return true;
    }

    public int remainingUndos() {
        return MAX_CONSECUTIVE_UNDOS - consecutiveUndos;
    }

    public boolean canUndo() {
        return !commands.isEmpty() && remainingUndos() > 0;
    }

    private Command oldestOfLastBlock() {
        Command oldest = commands.pop();
        for (int undone = 1; undone < MOVES_PER_UNDO && !commands.isEmpty(); undone++) {
            oldest = commands.pop();
        }
        return oldest;
    }
}