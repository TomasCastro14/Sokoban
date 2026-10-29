package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.Position;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/** The player entering ice keeps going in the same direction and ends on the first cell that is not ice. */
class PlayerIceSlideTest {

    @Test
    @DisplayName("Camino de hielo de varias celdas: el jugador termina en la primera celda de piso y es 1 sola accion")
    void longPathIsOneAction() {
        Level level = TestLevels.level(
                "########",
                "#@~~~  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 5), level.getBoard().getPlayerPosition());
        assertEquals(1, level.getStats().getMoves());
    }

    @Test
    @DisplayName("Camino de hielo con curva: el jugador no dobla, sigue derecho hasta salir del hielo")
    void playerDoesNotFollowTheCurve() {
        Level level = TestLevels.level(
                "#######",
                "#@~~~ #",
                "#   ~ #",
                "#   ~ #",
                "#######");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 5), level.getBoard().getPlayerPosition());
    }

    /** Player in the middle; each arm has 2 ice cells, then floor, then the border wall. */
    @ParameterizedTest(name = "Entrando al hielo hacia {0}: termina en ({1},{2})")
    @CsvSource({"UP, 1, 4", "DOWN, 7, 4", "LEFT, 4, 1", "RIGHT, 4, 7"})
    void slidesInEveryDirection(Direction direction, int row, int col) {
        Level level = TestLevels.level(
                "#########",
                "#       #",
                "#   ~   #",
                "#   ~   #",
                "# ~~@~~ #",
                "#   ~   #",
                "#   ~   #",
                "#       #",
                "#########");

        level.move(direction);

        assertEquals(new Position(row, col), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("Un movimiento normal fuera del hielo avanza 1 sola celda")
    void normalMoveAdvancesOneCell() {
        Level level = TestLevels.level(
                "#######",
                "#@    #",
                "#######");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertEquals(1, level.getStats().getMoves());
    }
}
