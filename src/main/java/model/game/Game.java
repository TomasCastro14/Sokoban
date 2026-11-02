package model.game;

import model.board.Direction;
import model.board.Visibility;
import model.game.event.BoardListener;
import model.game.event.LevelFlowListener;
import model.game.event.LevelOutcomeListener;
import model.game.loss.LossReason;
import model.game.state.BlockedState;
import model.game.state.GameCompletedState;
import model.game.state.GameState;
import model.game.state.LostState;
import model.game.state.MenuState;
import model.game.state.PlayingState;
import model.game.state.WonState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class Game {
    private final IntFunction<Level> levelFactory;
    private final int levelCount;
    private final List<BoardListener> boardListeners = new ArrayList<>();
    private final List<LevelFlowListener> flowListeners = new ArrayList<>();
    private final List<LevelOutcomeListener> outcomeListeners = new ArrayList<>();
    /** Escucha al nivel actual y reenvía sus eventos a los observers del juego. */
    private final LevelRelay relay = new LevelRelay();
    private GameState state = new MenuState();
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

    public GameState getState() {
        return state;
    }

    public Level getCurrentLevel() {
        if (level == null) {
            throw new IllegalStateException("The game has not started: call start() first");
        }
        return level;
    }

    public int getCurrentLevelNumber() {
        return levelNumber;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void start() {
        start(1);
    }

    public void start(int number) {
        if (number < 1 || number > levelCount) {
            throw new IllegalArgumentException("Level " + number + " is out of range 1.." + levelCount);
        }
        load(number);
        notifyFlow(listener -> listener.levelStarted(levelNumber, level));
    }

    public void move(Direction direction) {
        if (state.acceptsInput()) {
            level.move(direction);
        }
    }

    public void undo() {
        if (state.allowsUndo() && level.undo()) {
            state = stateFor(level);
            notifyFlow(listener -> listener.boardRestored(level));
        }
    }

    public void restart() {
        if (state.allowsRestart()) {
            load(levelNumber);
            notifyFlow(listener -> listener.levelRestarted(levelNumber, level));
        }
    }

    public void nextLevel() {
        if (!state.allowsNextLevel()) {
            return;
        }
        if (levelNumber == levelCount) {
            state = new GameCompletedState();
            return;
        }
        load(levelNumber + 1);
        notifyFlow(listener -> listener.levelStarted(levelNumber, level));
    }

    public void tick() {
        if (state.acceptsInput()) {
            level.elapseSeconds(1);
        }
    }

    public boolean isInputBlocked() {
        return !state.acceptsInput();
    }

    private void load(int number) {
        Level next = levelFactory.apply(number);
        if (next == null) {
            throw new IllegalStateException("No level was built for number " + number);
        }
        if (level != null) {
            // El nivel anterior deja de reportar: solo el nivel actual llega a los observers.
            level.removeBoardListener(relay);
            level.removeOutcomeListener(relay);
        }
        next.addBoardListener(relay);
        next.addOutcomeListener(relay);
        level = next;
        levelNumber = number;
        state = stateFor(next);
    }

    private static GameState stateFor(Level level) {
        return switch (level.getStatus()) {
            case PLAYING -> new PlayingState();
            case WON -> new WonState();
            case LOST -> level.getLossReason() == LossReason.DEADLOCK ? new BlockedState() : new LostState();
        };
    }

    // Copia de la lista: un observer puede desuscribirse mientras recibe el aviso.
    private void notifyBoard(Consumer<BoardListener> notification) {
        List.copyOf(boardListeners).forEach(notification);
    }

    private void notifyFlow(Consumer<LevelFlowListener> notification) {
        List.copyOf(flowListeners).forEach(notification);
    }

    private void notifyOutcome(Consumer<LevelOutcomeListener> notification) {
        List.copyOf(outcomeListeners).forEach(notification);
    }

    /**
     * Clase interna para no exponer playerMoved(), levelWon(), etc. como métodos públicos de Game.
     * Los eventos de tablero se reenvían tal cual; los de resultado además actualizan el estado.
     */
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
            state = stateFor(level);
            notifyOutcome(listener -> listener.levelWon(stats));
        }

        @Override
        public void levelLost(LossReason reason) {
            state = stateFor(level);
            notifyOutcome(listener -> listener.levelLost(reason));
        }
    }
}