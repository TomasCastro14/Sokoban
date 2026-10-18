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
