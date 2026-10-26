package model.board;

import factory.TestLevels;
import model.box.Player;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Base of the movement: walking one cell onto free floor. */
class WalkOnFloorTest {
    private static final Position CENTER = new Position(2, 2);

    /** 5x5 room with the player in the middle and free floor all around. */
    private static Level emptyRoom() {
        return TestLevels.level(
                "#####",
                "#   #",
                "# @ #",
                "#   #",
                "#####");
    }

    /** Long corridor so a slide of more than one cell would be noticed. */
    private static Level corridor() {
        return TestLevels.level(
                "#########",
                "#@      #",
                "#       #",
                "#########");
    }

    @Test
    @DisplayName("1. Mover hacia arriba a piso libre da OK y deja al jugador una fila arriba")
    void walkUp() {
        Level level = emptyRoom();

        assertEquals(MoveResult.OK, level.move(Direction.UP));
        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. Mover hacia abajo a piso libre da OK y deja al jugador una fila abajo")
    void walkDown() {
        Level level = emptyRoom();

        assertEquals(MoveResult.OK, level.move(Direction.DOWN));
        assertEquals(new Position(3, 2), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("3. Mover hacia la izquierda a piso libre da OK y deja al jugador una columna a la izquierda")
    void walkLeft() {
        Level level = emptyRoom();

        assertEquals(MoveResult.OK, level.move(Direction.LEFT));
        assertEquals(new Position(2, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("4. Mover hacia la derecha a piso libre da OK y deja al jugador una columna a la derecha")
    void walkRight() {
        Level level = emptyRoom();

        assertEquals(MoveResult.OK, level.move(Direction.RIGHT));
        assertEquals(new Position(2, 3), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("5. Despues de moverse la celda de origen queda libre y la de destino ocupada por el jugador")
    void originIsFreedAndTargetOccupied() {
        Level level = emptyRoom();

        level.move(Direction.RIGHT);

        assertTrue(level.getBoard().cellAt(CENTER).isFree());
        assertInstanceOf(Player.class, level.getBoard().cellAt(new Position(2, 3)).getOccupant());
    }

    @Test
    @DisplayName("6. Derecha, derecha y abajo dejan al jugador en la posicion esperada")
    void severalStepsInARow() {
        Level level = corridor();

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.DOWN);

        assertEquals(new Position(2, 3), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("7. Ida y vuelta (derecha + izquierda) devuelve al jugador a su posicion inicial")
    void goAndComeBack() {
        Level level = emptyRoom();

        level.move(Direction.RIGHT);
        level.move(Direction.LEFT);

        assertEquals(CENTER, level.getBoard().getPlayerPosition());
        assertTrue(level.getBoard().cellAt(new Position(2, 3)).isFree());
    }

    @Test
    @DisplayName("8. Cada movimiento avanza exactamente 1 celda aunque haya mas piso libre adelante")
    void movesExactlyOneCell() {
        Level level = corridor();

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
    }
}
