package model.game;

import model.board.Board;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.board.Push;
import model.board.BfsReachability;
import model.board.ReachabilityListener;
import model.board.Visibility;
import model.board.VisibilityMap;
import model.game.event.BoardListener;
import model.game.event.LevelOutcomeListener;
import model.game.event.TimeListener;
import model.game.loss.LossReason;
import model.game.loss.LossReporter;
import model.game.score.LevelGoals;
import model.game.score.ScoreCalculator;
import model.game.score.ScoreResult;
import model.history.LevelMemento;
import model.history.Memento;
import model.history.MoveCommand;
import model.history.MoveReceiver;
import model.history.UndoHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Also listens to the board: when the reachable area grows, it reveals it once the move ends.
 * Win and loss are announced once a move is committed (undo history included), so a listener
 * that asks whether the loss can still be undone gets the real answer.
 */
public class Level implements ReachabilityListener, LossReporter {
    private static final Consumer<LevelOutcomeListener> NO_OUTCOME = listener -> { };

    private final String name;
    private final int timeLimitSeconds;
    private final LevelGoals goals;
    private final Board board;
    private final LevelStats stats = new LevelStats();
    private final List<BoardListener> boardListeners = new ArrayList<>();
    private final List<LevelOutcomeListener> outcomeListeners = new ArrayList<>();
    private final List<TimeListener> timeListeners = new ArrayList<>();
    private final VisibilityMap visibility = new VisibilityMap(new BfsReachability());
    private boolean reachabilityChanged;
    private final int totalBoxes;
    private LevelStatus status = LevelStatus.PLAYING;
    private LossReason lossReason;
    private Push lastPush;
    private int remainingSeconds;
    private final UndoHistory history = new UndoHistory();
    private final MoveReceiver receiver = new LevelMoveReceiver();
    private boolean committingMove;
    private Consumer<LevelOutcomeListener> pendingOutcome = NO_OUTCOME;

    public Level(String name, int timeLimitSeconds, LevelGoals goals, Board board) {
        this.name = name;
        this.timeLimitSeconds = timeLimitSeconds;
        this.goals = Objects.requireNonNull(goals, "goals");
        this.board = board;
        this.totalBoxes = board.countBoxes();
        this.remainingSeconds = timeLimitSeconds;
        visibility.recalculate(board, board.getPlayerPosition());
    }

    public String getName() {
        return name;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public LevelGoals getGoals() {
        return goals;
    }

    /** Information Expert: the level holds the goals, the stats and the clock the score is made of. */
    public ScoreResult score(ScoreCalculator calculator) {
        return calculator.calculate(goals, stats, remainingSeconds);
    }

    public Board getBoard() {
        return board;
    }

    public LevelStats getStats() {
        return stats;
    }

    public MoveResult move(Direction direction) {
        MoveCommand command = new MoveCommand(receiver, direction);
        committingMove = true;
        try {
            MoveResult result = command.execute();
            if (result != MoveResult.BLOCKED) {
                history.record(command);
            }
            return result;
        } finally {
            committingMove = false;
            announcePendingOutcome();
        }
    }

    public LevelMemento save() {
        return new LevelMemento(List.of(board.save(), stats.save(), visibility.save(), saveOutcome()));
    }

    private Memento saveOutcome() {
        LevelStatus savedStatus = status;
        LossReason savedReason = lossReason;
        return () -> {
            status = savedStatus;
            lossReason = savedReason;
        };
    }

    private MoveResult play(Direction direction) {
        if (status != LevelStatus.PLAYING) {
            return MoveResult.BLOCKED;
        }
        Position boxTarget = board.boxDestination(direction);
        boolean wasFilled = hasStoredBox(boxTarget);
        MoveResult result = board.movePlayer(direction);
        if (result == MoveResult.BLOCKED) {
            return result;
        }
        lastPush = result == MoveResult.OK ? null : new Push(boxTarget, result);
        stats.addMove();
        notifyBoard(listener -> listener.playerMoved(direction));
        revealIfReachabilityChanged();
        if (result != MoveResult.OK) {
            stats.addPush();
            notifyBoard(listener -> listener.boxPushed(direction));
            if (!wasFilled && hasStoredBox(boxTarget)) {
                stats.addStoredBox();
                notifyBoard(listener -> listener.boxStored(stats.getStoredBoxes(), totalBoxes));
            }
        }
        checkEnd();
        return result;
    }

    @Override
    public void reachabilityChanged() {
        reachabilityChanged = true;
    }

    private void revealIfReachabilityChanged() {
        if (!reachabilityChanged) {
            return;
        }
        reachabilityChanged = false;
        visibility.recalculate(board, board.getPlayerPosition());
        notifyBoard(listener -> listener.doorOpened(visibility));
    }

    private void checkEnd() {
        if (status == LevelStatus.PLAYING && stats.getStoredBoxes() == totalBoxes) {
            status = LevelStatus.WON;
            notifyOutcome(listener -> listener.levelWon(stats));
        }
    }

    private boolean hasStoredBox(Position p) {
        return p != null && board.isInside(p) && board.getTile(p).getStoredBox() != null;
    }

    @Override
    public void declareLoss(LossReason reason) {
        if (status != LevelStatus.PLAYING) {
            return;
        }
        status = LevelStatus.LOST;
        lossReason = reason;
        notifyOutcome(listener -> listener.levelLost(reason));
    }

    private void notifyBoard(Consumer<BoardListener> notification) {
        List.copyOf(boardListeners).forEach(notification);
    }

    /** During a move the outcome waits until the move is in the history; otherwise it goes out now. */
    private void notifyOutcome(Consumer<LevelOutcomeListener> notification) {
        pendingOutcome = notification;
        if (!committingMove) {
            announcePendingOutcome();
        }
    }

    private void announcePendingOutcome() {
        Consumer<LevelOutcomeListener> notification = pendingOutcome;
        pendingOutcome = NO_OUTCOME;
        List.copyOf(outcomeListeners).forEach(notification);
    }

    public boolean undo() {
        if (!history.undo()) {
            return false;
        }
        stats.addUndo();
        return true;
    }

    public int getRemainingUndos() {
        return history.remainingUndos();
    }

    /** A loss only stops the level for good when its reason allows no undo or no undo is left. */
    public boolean canRecoverFrom(LossReason reason) {
        return reason.isRecoverable() && history.canUndo();
    }

    public LevelStatus getStatus() {
        return status;
    }

    public boolean isWon() {
        return status == LevelStatus.WON;
    }

    public Push lastPush() {
        return lastPush;
    }

    public LossReason getLossReason() {
        return lossReason;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void addBoardListener(BoardListener listener) {
        boardListeners.add(listener);
    }

    public void removeBoardListener(BoardListener listener) {
        boardListeners.remove(listener);
    }

    public void addOutcomeListener(LevelOutcomeListener listener) {
        outcomeListeners.add(listener);
    }

    public void removeOutcomeListener(LevelOutcomeListener listener) {
        outcomeListeners.remove(listener);
    }

    public void addTimeListener(TimeListener listener) {
        timeListeners.add(listener);
    }

    public void removeTimeListener(TimeListener listener) {
        timeListeners.remove(listener);
    }

    public void elapseSeconds(int seconds) {
        if (status != LevelStatus.PLAYING) {
            return;
        }
        remainingSeconds = Math.max(0, remainingSeconds - seconds);
        List.copyOf(timeListeners).forEach(listener -> listener.secondElapsed(remainingSeconds));
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    private final class LevelMoveReceiver implements MoveReceiver {

        @Override
        public LevelMemento save() {
            return Level.this.save();
        }

        @Override
        public MoveResult play(Direction direction) {
            return Level.this.play(direction);
        }
    }
}