package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.BoxType;
import model.box.KeyBox;
import model.box.NormalBox;
import model.box.WeakBox;
import model.game.Level;
import model.game.loss.LossReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoragePointTest {

    @Test
    @DisplayName("1. Una caja que entra en un punto de su color queda guardada")
    void boxOnMatchingPointIsStored() {
        assertEquals(StorageResult.STORED, new StoragePoint(BoxType.NORMAL).receive(new NormalBox()));
        assertEquals(StorageResult.STORED, new StoragePoint(BoxType.WEAK).receive(new WeakBox(3)));
    }

    @Test
    @DisplayName("1b. Empujar una caja sobre un punto de su color la deja guardada en el punto")
    void pushedBoxOnMatchingPointIsStored() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#######");
        NormalBox box = (NormalBox) level.getBoard().getBoxAt(new Position(1, 2));

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        StoragePoint point = (StoragePoint) level.getBoard().getTile(new Position(1, 3));
        assertTrue(point.isFilled());
        assertSame(box, level.getBoard().getBoxAt(new Position(1, 3)));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("1c. Guardar una caja cuenta como 1 movimiento y 1 empuje, y el jugador avanza")
    void storingCountsAsMoveAndPush() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#######");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertEquals(1, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("1d. La cantidad de cajas guardadas sube en 1 por cada caja guardada")
    void storedBoxesCounterGoesUp() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "# 3:  #",
                "#######");
        assertEquals(0, level.getStats().getStoredBoxes());

        level.move(Direction.RIGHT);
        assertEquals(1, level.getStats().getStoredBoxes());

        level.move(Direction.LEFT);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT);
        assertEquals(2, level.getStats().getStoredBoxes());
    }

    @Test
    @DisplayName("1e. Despues de guardarla, el jugador pisa el punto (puente): no la empuja ni suma empuje")
    void storedBoxIsABridge() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#######");
        level.move(Direction.RIGHT);

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertEquals(new Position(1, 4), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 3)), "the stored box stays fixed");
        assertNull(level.getBoard().getBoxAt(new Position(1, 5)));
        assertEquals(1, level.getStats().getPushes());
        assertEquals(1, level.getStats().getStoredBoxes());
    }

    @Test
    @DisplayName("1f. Empujar una caja a piso comun no la guarda")
    void boxOnFloorIsNotStored() {
        Level level = TestLevels.level(
                "#######",
                "#@$ . #",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));

        assertFalse(((StoragePoint) level.getBoard().getTile(new Position(1, 4))).isFilled());
        assertEquals(0, level.getStats().getStoredBoxes());
    }

    @Test
    @DisplayName("1g. Una WeakBox que se rompe en el empuje hacia su punto no queda guardada (gana BROKEN)")
    void brokenWeakBoxIsNotStored() {
        Level level = TestLevels.level(
                "#######",
                "#@1 : #",
                "#######");
        level.move(Direction.RIGHT); // uses its only push, now next to the ':'

        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));

        assertFalse(((StoragePoint) level.getBoard().getTile(new Position(1, 4))).isFilled());
        assertEquals(0, level.getStats().getStoredBoxes());
    }

    @Test
    @DisplayName("1h. Una caja de otro color no queda guardada y sigue dando derrota WRONG_STORAGE")
    void wrongBoxIsNotStored() {
        Level level = TestLevels.level(
                "#######",
                "#@$:  #",
                "#######");

        level.move(Direction.RIGHT);

        assertFalse(((StoragePoint) level.getBoard().getTile(new Position(1, 3))).isFilled());
        assertEquals(0, level.getStats().getStoredBoxes());
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("2. Una caja sobre un punto de otro color da WRONG_BOX")
    void boxOnOtherColorIsWrong() {
        assertEquals(StorageResult.WRONG_BOX, new StoragePoint(BoxType.NORMAL).receive(new WeakBox(3)));
        assertEquals(StorageResult.WRONG_BOX, new StoragePoint(BoxType.WEAK).receive(new NormalBox()));
    }

    @Test
    @DisplayName("2. Una WeakBox que pasa sobre un punto de otro color da derrota inmediata")
    void weakBoxOnNormalPointLoses() {
        Level level = TestLevels.level(
                "########",
                "#@3.  :#",
                "#    $ #",
                "########");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("2b. Una NormalBox empujada sobre un punto de WeakBox da derrota WRONG_STORAGE en ese movimiento")
    void normalBoxOnWeakPointLoses() {
        Level level = TestLevels.level(
                "#######",
                "#@$:  #",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("2c. Despues de la derrota por color, un nuevo movimiento da BLOCKED y no cambia nada")
    void noMovesAfterWrongStorage() {
        Level level = TestLevels.level(
                "#######",
                "#@$:  #",
                "#     #",
                "#######");
        level.move(Direction.RIGHT);
        Position player = level.getBoard().getPlayerPosition();
        int moves = level.getStats().getMoves();

        assertEquals(MoveResult.BLOCKED, level.move(Direction.DOWN));

        assertEquals(player, level.getBoard().getPlayerPosition());
        assertEquals(moves, level.getStats().getMoves());
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("2d. Una caja empujada sobre un punto de su color no es derrota")
    void boxOnMatchingPointIsNotLoss() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("2e. Empujar una caja a piso comun no es derrota")
    void boxOnFloorIsNotLoss() {
        Level level = TestLevels.level(
                "#######",
                "#@$  :#",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("2f. Un empuje BLOCKED al lado de un punto de otro color no es derrota")
    void blockedPushNextToWrongPointIsNotLoss() {
        Level level = TestLevels.level(
                "#######",
                "#:@$# #",
                "#######");

        assertEquals(MoveResult.BLOCKED, level.move(Direction.RIGHT));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("2g. Una KeyBox sobre un punto de otro color da WRONG_STORAGE (el color tiene prioridad sobre la llave)")
    void keyBoxOnOtherColorIsWrongStorage() {
        assertEquals(StorageResult.WRONG_BOX, new StoragePoint(BoxType.NORMAL).receive(new KeyBox()));

        Level level = TestLevels.level(
                "#######",
                "#@K.  #",
                "#######");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("2h. Si la WeakBox se rompe en el empuje que la llevaria a un punto de otro color, no es WRONG_STORAGE")
    void brokenWeakBoxIsNotWrongStorage() {
        Level level = TestLevels.level(
                "#######",
                "#@1 . #",
                "#######");
        level.move(Direction.RIGHT); // uses its only push, now next to the '.'

        assertEquals(MoveResult.BROKEN, level.move(Direction.RIGHT));
        assertNotEquals(LossReason.WRONG_STORAGE, level.getLossReason());
        assertNull(level.getBoard().getBoxAt(new Position(1, 4)), "a broken box never lands on the point");
    }

    @Test
    @DisplayName("3. Guardar la KeyBox antes de abrir su puerta da derrota (EarlyKeyStorage)")
    void keyBoxStoredBeforeDoorOpenedLoses() {
        assertEquals(StorageResult.KEY_NOT_USED, new StoragePoint(BoxType.KEY).receive(new KeyBox()));

        Level level = TestLevels.level(
                "########",
                "#@K; a #",
                "###A####",
                "#      #",
                "########");

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(LossReason.EARLY_KEY_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("4. Guardar la KeyBox despues de haber abierto la puerta es valido")
    void keyBoxStoredAfterDoorOpenedIsValid() {
        KeyBox keyBox = new KeyBox();
        keyBox.markDoorOpened();
        assertEquals(StorageResult.STORED, new StoragePoint(BoxType.KEY).receive(keyBox));

        Level level = TestLevels.level(
                "########",
                "#@Ka;  #",
                "####A###",
                "#      #",
                "########");

        level.move(Direction.RIGHT); // opens door A
        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertNull(level.getLossReason());
    }
}
