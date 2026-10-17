package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
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
    @DisplayName("2. Una caja sobre un punto de otro color da WRONG_TYPE")
    void boxOnOtherColorIsWrong() {
        assertEquals(StorageResult.WRONG_TYPE, new StoragePoint(BoxType.NORMAL).receive(new WeakBox(3)));
        assertEquals(StorageResult.WRONG_TYPE, new StoragePoint(BoxType.WEAK).receive(new NormalBox()));
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
    @DisplayName("3. Guardar la KeyBox antes de abrir su puerta da derrota (EarlyKeyStorage)")
    void keyBoxStoredBeforeDoorOpenedLoses() {
        assertEquals(StorageResult.EARLY_KEY, new StoragePoint(BoxType.KEY).receive(new KeyBox()));

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
