package model.history;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.Box;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveCommandTest {

    private static final class RecordingReceiver implements MoveReceiver {
        private final Level level;
        int saves;

        RecordingReceiver(Level level) {
            this.level = level;
        }

        @Override
        public LevelMemento save() {
            saves++;
            return level.save();
        }

        @Override
        public MoveResult play(Direction direction) {
            return level.getBoard().movePlayer(direction);
        }
    }

    private static Level pushLevel() {
        return TestLevels.level(
                "#######",
                "#@$  .#",
                "#######");
    }

    @Test
    @DisplayName("execute guarda el estado antes de mover y ejecuta el movimiento")
    void executeSavesThenMoves() {
        Level level = pushLevel();
        RecordingReceiver receiver = new RecordingReceiver(level);

        MoveResult result = new MoveCommand(receiver, Direction.RIGHT).execute();

        assertEquals(MoveResult.PUSHED, result);
        assertEquals(1, receiver.saves);
        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("execute y despues undo dejan el nivel igual que antes")
    void executeThenUndoLeavesTheLevelUnchanged() {
        Level level = pushLevel();
        Box box = level.getBoard().getBoxAt(new Position(1, 2));
        MoveCommand command = new MoveCommand(new RecordingReceiver(level), Direction.RIGHT);

        command.execute();
        command.undo();

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertSame(box, level.getBoard().getBoxAt(new Position(1, 2)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
    }

    @Test
    @DisplayName("undo sin haber ejecutado no cambia nada")
    void undoBeforeExecuteDoesNothing() {
        Level level = pushLevel();

        new MoveCommand(new RecordingReceiver(level), Direction.RIGHT).undo();

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }
}