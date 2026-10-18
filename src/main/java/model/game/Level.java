package model.game;

import model.board.Board;
import model.board.Direction;
import model.board.MoveResult;
import model.board.VisibilityMap;
import model.game.event.EventType;
import model.game.event.GameEvent;
import model.game.event.GameListener;
import model.game.loss.LossCondition;
import model.game.loss.LossReason;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final String name;
    private final int timeLimitSeconds;
    private final int targetScore;
    private final Board board;
    private final LevelStats stats = new LevelStats();
    private final List<LossCondition> lossConditions;
    private final List<GameListener> listeners = new ArrayList<>();
    private LossReason lossReason;

    public Level(String name, int timeLimitSeconds, int targetScore, Board board,
                 List<LossCondition> lossConditions) {
        this.name = name;
        this.timeLimitSeconds = timeLimitSeconds;
        this.targetScore = targetScore;
        this.board = board;
        this.lossConditions = List.copyOf(lossConditions);
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

    /** A blocked move does not count as a move nor as a push. A lost level accepts no more moves. */
    public MoveResult move(Direction direction) {
        if (lossReason != null) {
            return MoveResult.BLOCKED;
        }
        boolean pushing = board.getBoxAt(board.getPlayerPosition().move(direction)) != null;
        MoveResult result = board.movePlayer(direction);
        if (result == MoveResult.BLOCKED) {
            return result;
        }
        stats.addMove();
        if (pushing) {
            stats.addPush();
        }
        checkLoss();
        return result;
    }

    /** Loss conditions are evaluated once, after the move is finished. */
    private void checkLoss() {
        for (LossCondition condition : lossConditions) {
            if (condition.isMet(this)) {
                lossReason = condition.getReason();
                notifyListeners(new GameEvent(EventType.LEVEL_LOST));
                return;
            }
        }
    }

    private void notifyListeners(GameEvent event) {
        for (GameListener listener : listeners) {
            listener.onEvent(event);
        }
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
        return lossReason;
    }

    public VisibilityMap getVisibilityMap() {
        throw new UnsupportedOperationException("TODO");
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    public void elapseSeconds(int seconds) {
        throw new UnsupportedOperationException("TODO");
    }

    public int getRemainingSeconds() {
        throw new UnsupportedOperationException("TODO");
    }
}
