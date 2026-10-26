package model.game;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.KeyBox;
import model.game.event.EventType;
import model.game.loss.LossReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VictoryTest {

    private static List<EventType> listen(Level level) {
        List<EventType> events = new ArrayList<>();
        level.addListener(event -> events.add(event.getType()));
        return events;
    }

    /** Two normal boxes: RIGHT stores the first one; DOWN + RIGHT store the second one. */
    private static Level twoBoxes() {
        return TestLevels.level(
                "#######",
                "#@$.  #",
                "#  $. #",
                "#######");
    }

    @Test
    @DisplayName("1. Al arrancar, el nivel esta en curso: ni ganado ni perdido")
    void levelStartsPlaying() {
        Level level = twoBoxes();

        assertEquals(LevelStatus.PLAYING, level.getStatus());
        assertFalse(level.isWon());
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("2. Con una sola caja, guardarla gana el nivel")
    void storingTheOnlyBoxWins() {
        Level level = TestLevels.level(
                "#####",
                "#@$.#",
                "#####");

        level.move(Direction.RIGHT);

        assertTrue(level.isWon());
        assertEquals(LevelStatus.WON, level.getStatus());
    }

    @Test
    @DisplayName("3. Con todas las cajas guardadas menos una, el nivel sigue en curso")
    void missingOneBoxKeepsPlaying() {
        Level level = twoBoxes();

        level.move(Direction.RIGHT);

        assertEquals(LevelStatus.PLAYING, level.getStatus());
    }

    @Test
    @DisplayName("4. Guardar la ultima caja gana el nivel")
    void storingTheLastBoxWins() {
        Level level = twoBoxes();

        level.move(Direction.RIGHT);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT);

        assertTrue(level.isWon());
    }

    @Test
    @DisplayName("5. Cajas de los tres tipos (la KeyBox con su puerta abierta), todas guardadas, ganan el nivel")
    void allBoxTypesStoredWins() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "# 3:  #",
                "# K;  #",
                "#######");
        ((KeyBox) level.getBoard().getBoxAt(new Position(3, 2))).markDoorOpened();

        level.move(Direction.RIGHT); // normal box
        level.move(Direction.LEFT);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT); // weak box
        level.move(Direction.LEFT);
        level.move(Direction.DOWN);
        assertEquals(LevelStatus.PLAYING, level.getStatus());
        level.move(Direction.RIGHT); // key box

        assertTrue(level.isWon());
    }

    @Test
    @DisplayName("6. Si el ultimo empuje deja la caja en un punto de otro color, es derrota y no victoria")
    void lastPushOnWrongColorLoses() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#  $: #",
                "#######");

        level.move(Direction.RIGHT);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT);

        assertEquals(LevelStatus.LOST, level.getStatus());
        assertEquals(LossReason.WRONG_STORAGE, level.getLossReason());
        assertFalse(level.isWon());
    }

    @Test
    @DisplayName("7. Si el ultimo empuje guarda la KeyBox antes de abrir su puerta, es derrota y no victoria")
    void lastPushWithEarlyKeyLoses() {
        Level level = TestLevels.level(
                "#######",
                "#@$.  #",
                "#  K; #",
                "#######");

        level.move(Direction.RIGHT);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT);

        assertEquals(LossReason.EARLY_KEY_STORAGE, level.getLossReason());
        assertFalse(level.isWon());
    }

    @Test
    @DisplayName("8. Una WeakBox rota no deja ganar aunque se guarden las demas cajas")
    void brokenBoxNeverWins() {
        Level level = TestLevels.level(
                "########",
                "#@1  : #",
                "#  $.  #",
                "########");

        level.move(Direction.RIGHT); // uses its only push
        level.move(Direction.RIGHT); // breaks
        level.move(Direction.LEFT);
        level.move(Direction.DOWN);
        level.move(Direction.LEFT);
        level.move(Direction.LEFT);
        level.move(Direction.DOWN);
        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT); // stores the normal box

        assertFalse(level.isWon());
    }

    @Test
    @DisplayName("9. Despues de ganar, un nuevo movimiento da BLOCKED y no cambia nada")
    void noMovesAfterWinning() {
        Level level = TestLevels.level(
                "######",
                "#@$. #",
                "#    #",
                "######");
        level.move(Direction.RIGHT);
        Position player = level.getBoard().getPlayerPosition();

        assertEquals(MoveResult.BLOCKED, level.move(Direction.DOWN));

        assertEquals(player, level.getBoard().getPlayerPosition());
        assertEquals(1, level.getStats().getMoves());
        assertTrue(level.isWon());
    }

    @Test
    @DisplayName("10. Ganar avisa LEVEL_WON una sola vez")
    void winningNotifiesOnce() {
        Level level = TestLevels.level(
                "######",
                "#@$. #",
                "######");
        List<EventType> events = listen(level);

        level.move(Direction.RIGHT);
        level.move(Direction.LEFT);

        assertEquals(1, events.stream().filter(EventType.LEVEL_WON::equals).count());
    }
}
