package model.tile;

import factory.TestLevels;
import model.board.Board;
import model.board.Cell;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.Player;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Assumed rule: whoever is standing on ice keeps moving in the same direction
 * while the next cell is free, so the slide ends on the first non-ice cell
 * or just before an obstacle.
 */
class IceTest {

    @Test
    @DisplayName("1. El jugador que entra al hielo se desliza hasta el final del camino")
    void playerSlidesAcrossIce() {
        Level level = TestLevels.level(
                "########",
                "#@~~~  #",
                "#  $.  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 5), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. Una caja empujada sobre el hielo tambien se desliza hasta el final")
    void pushedBoxSlidesAcrossIce() {
        Level level = TestLevels.level(
                "#########",
                "#@$~~~ .#",
                "#########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 6)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
    }

    @Test
    @DisplayName("3. El deslizamiento frena antes de una pared")
    void slideStopsBeforeWall() {
        Level level = TestLevels.level(
                "########",
                "#@~~~# #",
                "#  $.  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 4), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("3. El deslizamiento frena antes de otra caja")
    void slideStopsBeforeBox() {
        Level level = TestLevels.level(
                "########",
                "#@~~~$ #",
                "#   .  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 4), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 5)), "sliding does not push the box");
    }

    // 4. A 1-cell ice path: the slide moves the player one cell past the ice and stops there.

    @Test
    @DisplayName("4. Hielo de 1 bloque seguido de piso: termina en la primera celda de piso y no sigue")
    void singleIceThenFloorStopsOnFirstFloor() {
        Level level = TestLevels.level(
                "#######",
                "#@~   #",
                "# $.  #",
                "#######");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
        assertEquals(1, level.getStats().getMoves(), "the whole slide counts as one move (see TODO in Level.move)");
    }

    @Test
    @DisplayName("4. Hielo de 1 bloque seguido de pared: el jugador queda sobre el hielo")
    void singleIceThenWallStaysOnIce() {
        Level level = TestLevels.level(
                "#######",
                "#@~#  #",
                "# $.  #",
                "#######");

        MoveResult result = level.move(Direction.RIGHT);

        assertEquals(MoveResult.OK, result);
        assertEquals(new Position(1, 2), level.getBoard().getPlayerPosition());
    }

    /**
     * Built by hand because the border rule never lets a loaded level put ice on the edge.
     * The slide has to stop on the ice instead of looking outside the grid.
     */
    @Test
    @DisplayName("4. Hielo de 1 bloque en el borde del tablero: no se sale del tablero")
    void singleIceOnBoardEdgeStaysInside() {
        Cell[][] cells = {{
                new Cell(new Position(0, 0), new Floor()),
                new Cell(new Position(0, 1), new Ice())}};
        cells[0][0].place(new Player(Direction.RIGHT));
        Board board = new Board(cells, new Position(0, 0));

        MoveResult result = board.movePlayer(Direction.RIGHT);

        assertEquals(MoveResult.OK, result);
        assertEquals(new Position(0, 1), board.getPlayerPosition());
    }

    /** Player in the middle, one ice cell on each side and floor right after it. */
    @ParameterizedTest(name = "4. Hielo de 1 bloque hacia {0}: termina en ({1},{2})")
    @CsvSource({"UP, 2, 3", "DOWN, 6, 3", "LEFT, 4, 1", "RIGHT, 4, 5"})
    void singleIceInEveryDirection(Direction direction, int row, int col) {
        Level level = TestLevels.level(
                "#######",
                "#$.   #",
                "#     #",
                "#  ~  #",
                "# ~@~ #",
                "#  ~  #",
                "#     #",
                "#######");

        level.move(direction);

        assertEquals(new Position(row, col), level.getBoard().getPlayerPosition());
    }
}
