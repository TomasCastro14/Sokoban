package model.game;

import model.board.Board;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.board.BfsReachability;
import model.board.ReachabilityListener;
import model.board.Visibility;
import model.board.VisibilityMap;
import model.game.event.EventType;
import model.game.event.GameEvent;
import model.game.event.GameListener;
import model.game.loss.LossCondition;
import model.game.loss.LossReason;

import java.util.ArrayList;
import java.util.List;

/** Also listens to the board: when the reachable area grows, it reveals it once the move ends. */
public class Level implements ReachabilityListener {
    private final String name;
    private final int timeLimitSeconds;
    private final int targetScore;
    private final Board board;
    private final LevelStats stats = new LevelStats();
    private final List<LossCondition> lossConditions;
    private final List<GameListener> listeners = new ArrayList<>();
    private final VisibilityMap visibility = new VisibilityMap(new BfsReachability());
    private boolean reachabilityChanged;
    /** Boxes to store, counted once: stored boxes stay fixed, so the count never has to go down. */
    private final int totalBoxes;
    private LevelStatus status = LevelStatus.PLAYING;
    private LossReason lossReason;

    public Level(String name, int timeLimitSeconds, int targetScore, Board board,
                 List<LossCondition> lossConditions) {
        this.name = name;
        this.timeLimitSeconds = timeLimitSeconds;
        this.targetScore = targetScore;
        this.board = board;
        this.lossConditions = List.copyOf(lossConditions);
        this.totalBoxes = board.countBoxes();
        visibility.recalculate(board, board.getPlayerPosition());
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

    /** A blocked move does not count as a move nor as a push. A finished level accepts no more moves. */
    public MoveResult move(Direction direction) {
        if (status != LevelStatus.PLAYING) {
            return MoveResult.BLOCKED;
        }
        Position boxTarget = board.boxDestination(direction); // where the box really lands, even after sliding on ice
        boolean wasFilled = hasStoredBox(boxTarget);
        MoveResult result = board.movePlayer(direction);
        if (result == MoveResult.BLOCKED) {
            return result;
        }
        // The only place that counts. One user action = 1 move, and 1 push if a box was pushed,
        // however many cells the player or the box slid. A blocked move returned above counts nothing.
        stats.addMove();
        revealIfReachabilityChanged();
        if (result != MoveResult.OK) { // PUSHED or BROKEN: the board pushed a box
            stats.addPush();
            if (!wasFilled && hasStoredBox(boxTarget)) {
                stats.addStoredBox();
                notifyListeners(new GameEvent(EventType.BOX_STORED));
            }
        }
        checkEnd();
        return result;
    }

    @Override
    public void reachabilityChanged() {
        reachabilityChanged = true;
    }

    /** Recalculates once per move, however many doors opened in it. */
    private void revealIfReachabilityChanged() {
        if (!reachabilityChanged) {
            return;
        }
        reachabilityChanged = false;
        visibility.recalculate(board, board.getPlayerPosition());
        notifyListeners(new GameEvent(EventType.DOOR_OPENED));
    }

    /** Loss is checked first: a move that loses never wins, even if it stored the last box. */
    private void checkEnd() {
        checkLoss();
        if (status == LevelStatus.PLAYING && stats.getStoredBoxes() == totalBoxes) {
            status = LevelStatus.WON;
            notifyListeners(new GameEvent(EventType.LEVEL_WON));
        }
    }

    private boolean hasStoredBox(Position p) {
        return p != null && board.isInside(p) && board.getTile(p).getStoredBox() != null;
    }

    /** Loss conditions are evaluated once, after the move is finished. */
    private void checkLoss() {
        for (LossCondition condition : lossConditions) {
            if (condition.isMet(this)) {
                status = LevelStatus.LOST;
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

    public LevelStatus getStatus() {
        return status;
    }

    public boolean isWon() {
        return status == LevelStatus.WON;
    }

    /** Returns the reason of the loss, or null while the level is not lost. */
    public LossReason getLossReason() {
        return lossReason;
    }

    /** Computed when the level is built, so it is ready before the first draw. */
    public Visibility getVisibility() {
        return visibility;
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
