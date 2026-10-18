package model.board;

import factory.TestLevels;
import model.box.NormalBox;
import model.box.PushResult;
import model.box.WeakBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PushTest {

    @Test
    @DisplayName("1. Empujar una caja hacia piso libre mueve la caja y al jugador, y suma 1 empuje")
    void pushMovesBoxAndPlayer() {
        Level level = TestLevels.level(
                "#######",
                "#@$  .#",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("2. No se puede empujar una caja contra una pared")
    void cannotPushBoxIntoWall() {
        Level level = TestLevels.level(
                "######",
                "#@$#.#",
                "######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("3. No se pueden empujar dos cajas en fila: BLOCKED y nada se mueve")
    void cannotPushTwoBoxes() {
        Level level = TestLevels.level(
                "########",
                "#@$$ ..#",
                "########");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 2)));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 4)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("4. NormalBox no tiene efecto extra al ser empujada")
    void normalBoxHasNoExtraEffect() {
        NormalBox box = new NormalBox();

        for (int i = 0; i < 20; i++) {
            assertEquals(PushResult.PUSHED, box.push());
        }
    }

    @Test
    @DisplayName("5. WeakBox con 3 empujes: despues de 2 empujes le queda 1")
    void weakBoxCountsDown() {
        WeakBox box = new WeakBox(3);

        box.push();
        box.push();

        assertEquals(1, box.getRemainingPushes());
    }

    @Test
    @DisplayName("6. A WeakBox le baja el contador solo cuando la empujan, no cuando el jugador camina")
    void weakBoxOnlyCountsPushes() {
        Level level = TestLevels.level(
                "#######",
                "#  3 :#",
                "#@    #",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 3));

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);

        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("6b. Caminar al lado de una WeakBox(3) no le baja el contador")
    void walkingNextToWeakBoxKeepsCounter() {
        Level level = TestLevels.level(
                "#######",
                "#  3 :#",
                "#@    #",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 3));

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(3, box.getRemainingPushes());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6c. Caminar varias veces sin tocar la WeakBox no cambia el contador de ninguna WeakBox")
    void walkingAroundKeepsEveryWeakBoxCounter() {
        Level level = TestLevels.level(
                "########",
                "#3    :#",
                "#      #",
                "#@   2:#",
                "########");
        WeakBox first = (WeakBox) level.getBoard().getBoxAt(new Position(1, 1));
        WeakBox second = (WeakBox) level.getBoard().getBoxAt(new Position(3, 5));

        level.move(Direction.UP);
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.LEFT);
        level.move(Direction.LEFT);

        assertEquals(6, level.getStats().getMoves());
        assertEquals(3, first.getRemainingPushes());
        assertEquals(2, second.getRemainingPushes());
    }

    @Test
    @DisplayName("6d. Empujar una WeakBox contra una pared da BLOCKED y no le baja el contador")
    void blockedPushAgainstWallKeepsCounter() {
        Level level = TestLevels.level(
                "######",
                "#@3#:#",
                "######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("6e. Empujar una WeakBox contra terreno no accesible (%) da BLOCKED y no le baja el contador")
    void blockedPushAgainstVoidKeepsCounter() {
        Level level = TestLevels.level(
                "#######",
                "#@3% :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(3, box.getRemainingPushes());
    }

    @Test
    @DisplayName("6f. Empujar dos cajas en fila da BLOCKED y ninguna WeakBox descuenta")
    void blockedPushOfTwoBoxesKeepsCounters() {
        Level level = TestLevels.level(
                "########",
                "#@33 ::#",
                "########");
        WeakBox front = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));
        WeakBox behind = (WeakBox) level.getBoard().getBoxAt(new Position(1, 3));

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));

        assertEquals(3, front.getRemainingPushes());
        assertEquals(3, behind.getRemainingPushes());
    }

    @Test
    @DisplayName("6g. Un empuje valido a una WeakBox(3) la deja en 2")
    void validPushCountsDownOnce() {
        Level level = TestLevels.level(
                "#######",
                "#@3  :#",
                "#######");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertSame(box, level.getBoard().getBoxAt(new Position(1, 3)));
        assertEquals(2, box.getRemainingPushes());
    }

    @Test
    @DisplayName("7. Cuando a la WeakBox se le terminan los empujes, el resultado es BROKEN")
    void weakBoxBreaksWhenOutOfPushes() {
        // "Aguanta N empujes": the first N pushes are fine, the next one breaks it.
        WeakBox box = new WeakBox(2);

        assertEquals(PushResult.PUSHED, box.push());
        assertEquals(PushResult.PUSHED, box.push());
        assertEquals(PushResult.BROKEN, box.push());
    }
}
