package model.tile.mechanism;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.game.Level;
import model.game.loss.LossReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** A key box that lands on key tile 'a' opens every door A in that same push, and they stay open. */
class KeyBoxOpensDoorsTest {

    private static boolean isOpen(Level level, int row, int col) {
        return ((Door) level.getBoard().getTile(new Position(row, col))).isOpen();
    }

    private static void push(Level level, int times) {
        for (int i = 0; i < times; i++) {
            level.move(Direction.RIGHT);
        }
    }

    @Test
    @DisplayName("Una KeyBox empujada sobre a abre la unica puerta A, en el mismo empuje")
    void keyBoxOpensTheOnlyDoor() {
        Level level = TestLevels.level(
                "#########",
                "#@K a   #",
                "####A####",
                "#       #",
                "#########");

        push(level, 1);
        assertFalse(isOpen(level, 2, 4));

        push(level, 1);
        assertTrue(isOpen(level, 2, 4));
    }

    @Test
    @DisplayName("Una KeyBox sobre a abre las tres puertas A del nivel")
    void keyBoxOpensAllThreeDoors() {
        Level level = TestLevels.level(
                "###########",
                "#@K a     #",
                "##A#A#A####",
                "#         #",
                "###########");

        push(level, 2);

        assertTrue(isOpen(level, 2, 2));
        assertTrue(isOpen(level, 2, 4));
        assertTrue(isOpen(level, 2, 6));
    }

    @Test
    @DisplayName("Si la KeyBox sale de la baldosa, las puertas A siguen abiertas")
    void doorsStayOpenWhenTheBoxLeaves() {
        Level level = TestLevels.level(
                "#########",
                "#@K a   #",
                "####A####",
                "#       #",
                "#########");

        push(level, 3);

        assertNull(level.getBoard().getBoxAt(new Position(1, 4)));
        assertTrue(isOpen(level, 2, 4));
    }

    @Test
    @DisplayName("Guardar la KeyBox antes de abrir su puerta es derrota")
    void storingBeforeOpeningLoses() {
        Level level = TestLevels.level(
                "#########",
                "#@K ;  a#",
                "#######A#",
                "#       #",
                "#########");

        push(level, 2);

        assertEquals(LossReason.EARLY_KEY_STORAGE, level.getLossReason());
    }

    @Test
    @DisplayName("Guardar la KeyBox despues de abrir su puerta es valido")
    void storingAfterOpeningIsValid() {
        Level level = TestLevels.level(
                "########",
                "#@Ka;  #",
                "####A###",
                "#      #",
                "########");

        push(level, 2);

        assertTrue(isOpen(level, 2, 4));
        assertNull(level.getLossReason());
    }

    @Test
    @DisplayName("Sin la caja sobre la baldosa, las puertas A siguen cerradas")
    void doorsStayClosedWithoutTheBox() {
        Level level = TestLevels.level(
                "#########",
                "#@K a   #",
                "####A####",
                "#       #",
                "#########");

        push(level, 1);

        assertFalse(isOpen(level, 2, 4));
    }
}
