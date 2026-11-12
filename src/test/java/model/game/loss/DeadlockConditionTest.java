package model.game.loss;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.game.Level;
import model.game.LevelStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DeadlockConditionTest {

    private final DeadlockCondition condition = new DeadlockCondition();

    static Stream<Arguments> fourCorners() {
        return Stream.of(
                Arguments.of("arriba a la izquierda", Direction.UP,
                        new String[]{"#####", "#   #", "#$  #", "#@ .#", "#####"}),
                Arguments.of("arriba a la derecha", Direction.UP,
                        new String[]{"#####", "#   #", "#  $#", "#. @#", "#####"}),
                Arguments.of("abajo a la izquierda", Direction.DOWN,
                        new String[]{"#####", "#@ .#", "#$  #", "#   #", "#####"}),
                Arguments.of("abajo a la derecha", Direction.DOWN,
                        new String[]{"#####", "#. @#", "#  $#", "#   #", "#####"}));
    }

    @ParameterizedTest(name = "esquina {0}")
    @MethodSource("fourCorners")
    @DisplayName("Una caja en cualquiera de las 4 esquinas, sin objetivo, es deadlock")
    void boxInAnyCornerIsDeadlock(String corner, Direction push, String[] rows) {
        Level level = TestLevels.level(rows);

        assertEquals(MoveResult.PUSHED, level.move(push));

        assertTrue(condition.isMet(level));
        assertEquals(LevelStatus.LOST, level.getStatus());
        assertEquals(LossReason.DEADLOCK, level.getLossReason());
    }

    @Test
    @DisplayName("Una caja en una esquina que es su objetivo no es deadlock")
    void cornerWithTargetIsNotDeadlock() {
        Level level = TestLevels.level("#####", "#.  #", "#$  #", "#@  #", "#####");

        level.move(Direction.UP);

        assertFalse(condition.isMet(level));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("Una caja contra una pared, pero no en una esquina, no es deadlock")
    void againstWallIsNotDeadlock() {
        Level level = TestLevels.level("#####", "#   #", "# $ #", "#.@ #", "#####");

        level.move(Direction.UP);

        assertFalse(condition.isMet(level));
        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("Un bloque de 2x2 de cajas y paredes es deadlock")
    void twoByTwoBlockIsDeadlock() {
        Level level = TestLevels.level("#######", "#  $  #", "#   $ #", "#   @ #", "#..   #", "#######");

        level.move(Direction.UP);

        assertTrue(condition.isMet(level));
    }

    @Test
    @DisplayName("Pasar una caja por encima de cajas ya guardadas no es deadlock")
    void storedBoxesAreNotObstacles() {
        Level level = TestLevels.level("########", "#   . .#", "# $ $  #", "#   @  #", "########");
        level.move(Direction.UP);      // guarda la primera caja en (1,4)
        level.move(Direction.DOWN);
        level.move(Direction.LEFT);
        level.move(Direction.LEFT);
        level.move(Direction.UP);      // sube la segunda caja a (1,2)
        level.move(Direction.LEFT);
        level.move(Direction.UP);

        // queda junto a la caja guardada, contra la pared: no es un bloque
        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertFalse(condition.isMet(level));
        assertEquals(LevelStatus.PLAYING, level.getStatus());

        // pasa por encima del punto ocupado y llega al otro objetivo
        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertEquals(LevelStatus.PLAYING, level.getStatus());
        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));
        assertEquals(LevelStatus.WON, level.getStatus());
    }

    @Test
    @DisplayName("Una caja en esquina que no se empujo no se evalua")
    void onlyThePushedBoxIsChecked() {
        Level level = TestLevels.level("#######", "#$   .#", "#     #", "#@$  .#", "#     #", "#######");

        level.move(Direction.RIGHT);

        assertFalse(condition.isMet(level));
    }

    @Test
    @DisplayName("Un undo desde el deadlock vuelve a jugar")
    void undoRecoversFromDeadlock() {
        Level level = TestLevels.level("#####", "#   #", "#$  #", "#@ .#", "#####");
        level.move(Direction.UP);

        assertTrue(level.undo());

        assertEquals(LevelStatus.PLAYING, level.getStatus());
        assertTrue(LossReason.DEADLOCK.isRecoverable());
    }
}
