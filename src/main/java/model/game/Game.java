package model.game;

import model.board.Direction;
import model.board.Visibility;
import model.game.event.BoardListener;
import model.game.event.GameCompletionListener;
import model.game.event.LevelCompletedListener;
import model.game.event.LevelFlowListener;
import model.game.event.LevelOutcomeListener;
import model.game.event.PauseListener;
import model.game.loss.LossReason;
import model.game.state.GameContext;
import model.game.state.GameState;
import model.game.state.MenuState;
import model.game.state.PlayingState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class Game {
    public static final int FIRST_LEVEL = 1;

    private final IntFunction<Level> levelFactory;
    private final int levelCount;
    private final List<BoardListener> boardListeners = new ArrayList<>();
    private final List<LevelFlowListener> flowListeners = new ArrayList<>();
    private final List<LevelOutcomeListener> outcomeListeners = new ArrayList<>();
    private final List<LevelCompletedListener> completedListeners = new ArrayList<>();
    private final List<PauseListener> pauseListeners = new ArrayList<>();
    private final List<GameCompletionListener> completionListeners = new ArrayList<>();
    private final LevelRelay relay = new LevelRelay();
    private final GameContext context = new StateContext();
    private GameState state = MenuState.INSTANCE;
    private Level level;
    private int levelNumber;

    public Game(IntFunction<Level> levelFactory, int levelCount) {
        if (levelCount < 1) {
            throw new IllegalArgumentException("The game needs at least one level, got " + levelCount);
        }
        this.levelFactory = Objects.requireNonNull(levelFactory, "levelFactory");
        this.levelCount = levelCount;
    }

    public void addBoardListener(BoardListener listener) {
        boardListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeBoardListener(BoardListener listener) {
        boardListeners.remove(listener);
    }

    public void addLevelFlowListener(LevelFlowListener listener) {
        flowListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeLevelFlowListener(LevelFlowListener listener) {
        flowListeners.remove(listener);
    }

    public void addLevelOutcomeListener(LevelOutcomeListener listener) {
        outcomeListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeLevelOutcomeListener(LevelOutcomeListener listener) {
        outcomeListeners.remove(listener);
    }

    public void addLevelCompletedListener(LevelCompletedListener listener) {
        completedListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeLevelCompletedListener(LevelCompletedListener listener) {
        completedListeners.remove(listener);
    }

    public void addPauseListener(PauseListener listener) {
        pauseListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removePauseListener(PauseListener listener) {
        pauseListeners.remove(listener);
    }

    public void addCompletionListener(GameCompletionListener listener) {
        completionListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeCompletionListener(GameCompletionListener listener) {
        completionListeners.remove(listener);
    }

    public GameState getState() {
        return state;
    }

    public Level getCurrentLevel() {
        if (level == null) {
            throw new IllegalStateException("The game has not started: call start() first");
        }
        return level;
    }

    public boolean hasStarted() {
        return level != null;
    }

    public int getCurrentLevelNumber() {
        return levelNumber;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void start() {
        start(FIRST_LEVEL);
    }

    public void start(int number) {
        if (number < FIRST_LEVEL || number > levelCount) {
            throw new IllegalArgumentException("Level " + number + " is out of range 1.." + levelCount);
        }
        load(number);
        state = PlayingState.INSTANCE;
        notifyFlow(listener -> listener.levelStarted(levelNumber, level));
    }

    public void move(Direction direction) {
        state = state.move(context, direction);
    }

    public void undo() {
        state = state.undo(context);
    }

    public void restart() {
        state = state.restart(context);
    }

    public void nextLevel() {
        state = state.nextLevel(context);
    }

    public void togglePause() {
        state = state.togglePause(context);
    }

    public void tick() {
        state = state.tick(context);
    }

    private void load(int number) {
        Level next = levelFactory.apply(number);
        if (next == null) {
            throw new IllegalStateException("No level was built for number " + number);
        }
        if (level != null) {
            level.removeBoardListener(relay);
            level.removeOutcomeListener(relay);
        }
        next.addBoardListener(relay);
        next.addOutcomeListener(relay);
        level = next;
        levelNumber = number;
    }

    private void notifyBoard(Consumer<BoardListener> notification) {
        List.copyOf(boardListeners).forEach(notification);
    }

    private void notifyFlow(Consumer<LevelFlowListener> notification) {
        List.copyOf(flowListeners).forEach(notification);
    }

    private void notifyOutcome(Consumer<LevelOutcomeListener> notification) {
        List.copyOf(outcomeListeners).forEach(notification);
    }

    private void notifyCompleted(LevelStats stats) {
        int number = levelNumber;
        List.copyOf(completedListeners).forEach(listener -> listener.levelCompleted(number, stats));
    }

    private void notifyPause(Consumer<PauseListener> notification) {
        List.copyOf(pauseListeners).forEach(notification);
    }

    private final class LevelRelay implements BoardListener, LevelOutcomeListener {

        @Override
        public void playerMoved(Direction direction) {
            notifyBoard(listener -> listener.playerMoved(direction));
        }

        @Override
        public void boxPushed(Direction direction) {
            notifyBoard(listener -> listener.boxPushed(direction));
        }

        @Override
        public void boxStored(int storedBoxes, int totalBoxes) {
            notifyBoard(listener -> listener.boxStored(storedBoxes, totalBoxes));
        }

        @Override
        public void doorOpened(Visibility visibility) {
            notifyBoard(listener -> listener.doorOpened(visibility));
        }

        @Override
        public void levelWon(LevelStats stats) {
            state = state.onLevelWon(stats);
            notifyCompleted(stats);
            notifyOutcome(listener -> listener.levelWon(stats));
        }

        @Override
        public void levelLost(LossReason reason) {
            state = state.onLevelLost(context, reason);
            notifyOutcome(listener -> listener.levelLost(reason));
        }
    }

    private final class StateContext implements GameContext {

        @Override
        public GameState playMove(Direction direction) {
            level.move(direction);
            return state;
        }

        @Override
        public GameState elapseSecond() {
            level.elapseSeconds(1);
            return state;
        }

        @Override
        public boolean undoLastMove() {
            boolean undone = level.undo();
            if (undone) {
                notifyFlow(listener -> listener.boardRestored(level));
            }
            return undone;
        }

        @Override
        public boolean canRecoverFrom(LossReason reason) {
            return level.canRecoverFrom(reason);
        }

        @Override
        public void reloadLevel() {
            load(levelNumber);
            notifyFlow(listener -> listener.levelRestarted(levelNumber, level));
        }

        @Override
        public void loadNextLevel() {
            load(levelNumber + 1);
            notifyFlow(listener -> listener.levelStarted(levelNumber, level));
        }

        @Override
        public boolean isLastLevel() {
            return levelNumber == levelCount;
        }

        @Override
        public void completeGame() {
            List.copyOf(completionListeners).forEach(GameCompletionListener::gameCompleted);
        }

        @Override
        public void announcePause() {
            notifyPause(PauseListener::gamePaused);
        }

        @Override
        public void announceResume() {
            notifyPause(PauseListener::gameResumed);
        }
    }
}