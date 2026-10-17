package model.game;

import model.board.Board;
import model.board.Direction;
import model.board.MoveResult;
import model.board.VisibilityMap;
import model.game.event.GameListener;
import model.game.loss.LossReason;

public class Level {
    private final String name;
    private final int timeLimitSeconds;
    private final int targetScore;
    private final Board board;
    private final LevelStats stats = new LevelStats();

    public Level(String name, int timeLimitSeconds, int targetScore, Board board) {
        this.name = name;
        this.timeLimitSeconds = timeLimitSeconds;
        this.targetScore = targetScore;
        this.board = board;
    }

    public String getName() {
        return name;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public Board getBoard() {
        return board;
    }

    public LevelStats getStats() {
        return stats;
    }

    /** A blocked move does not count as a move nor as a push. */
    public MoveResult move(Direction direction) {
        boolean pushing = board.getBoxAt(board.getPlayerPosition().move(direction)) != null;
        MoveResult result = board.movePlayer(direction);
        if (result == MoveResult.BLOCKED) {
            return result;
        }
        stats.addMove();
        if (pushing) {
            stats.addPush();
        }
        return result;
    }

    /** Returns false when the undo was not applied (empty history or no undos left). */
    public boolean undo() {
        throw new UnsupportedOperationException("TODO");
    }

    public int getRemainingUndos() {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isWon() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Returns the reason of the loss, or null while the level is not lost. */
    public LossReason getLossReason() {
        throw new UnsupportedOperationException("TODO");
    }

    public VisibilityMap getVisibilityMap() {
        throw new UnsupportedOperationException("TODO");
    }

    public void addListener(GameListener listener) {
        throw new UnsupportedOperationException("TODO");
    }

    public void elapseSeconds(int seconds) {
        throw new UnsupportedOperationException("TODO");
    }

    public int getRemainingSeconds() {
        throw new UnsupportedOperationException("TODO");
    }
}
