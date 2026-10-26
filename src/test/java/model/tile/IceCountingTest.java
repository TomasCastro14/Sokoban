package model.tile;

import factory.TestLevels;
import model.board.Board;
import model.board.Cell;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.NormalBox;
import model.box.Player;
import model.box.WeakBox;
import model.game.Level;
import model.game.LevelStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Rule: one user action = 1 move, and 1 push if a box was pushed, however many cells
 * the player or the box slide. A blocked move counts nothing.
 */
class IceCountingTest {

    private static void assertCounts(Level level, int moves, int pushes) {
        LevelStats stats = level.getStats();
        assertEquals(moves, stats.getMoves(), "moves");
        assertEquals(pushes, stats.getPushes(), "pushes");
    }

    @Test
    @DisplayName("El jugador se desliza N celdas: 1 movimiento y 0 empujes")
    void playerSlidingNCellsIsOneMove() {
        Level level = TestLevels.level(
                "########",
                "#@~~~  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 5), level.getBoard().getPlayerPosition());
        assertCounts(level, 1, 0);
    }

    @Test
    @DisplayName("Camino de hielo de 1 bloque: 1 movimiento y 0 empujes")
    void singleIceTileIsOneMove() {
        Level level = TestLevels.level(
                "#######",
                "#@~   #",
                "#######");

        level.move(Direction.RIGHT);

        assertCounts(level, 1, 0);
    }

    @Test
    @DisplayName("Una caja empujada que se desliza N celdas: 1 movimiento y 1 empuje")
    void boxSlidingNCellsIsOnePush() {
        Level level = TestLevels.level(
                "#########",
                "#@$~~~ .#",
                "#########");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 6)));
        assertCounts(level, 1, 1);
    }

    @Test
    @DisplayName("Una WeakBox que se desliza baja su contador 1 sola vez")
    void weakBoxSlidingLosesOneUse() {
        Level level = TestLevels.level(
                "#########",
                "#@3~~~ :#",
                "#########");
        WeakBox box = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));

        level.move(Direction.RIGHT);

        assertEquals(2, box.getRemainingPushes());
        assertCounts(level, 1, 1);
    }

    /** The player enters ice while pushing: the box moves one cell, the player stops before it. */
    @Test
    @DisplayName("El jugador y la caja sobre hielo a la vez: 1 movimiento y 1 empuje")
    void playerOnIceAndPushedBoxAtTheSameTime() {
        Cell[][] cells = {{
                new Cell(new Position(0, 0), new Floor()),
                new Cell(new Position(0, 1), new Ice()),
                new Cell(new Position(0, 2), new Floor()),
                new Cell(new Position(0, 3), new Floor())}};
        cells[0][0].place(new Player(Direction.RIGHT));
        cells[0][1].place(new NormalBox());
        Level level = new Level("hand-built", 60, 100, new Board(cells, new Position(0, 0)), List.of());

        MoveResult result = level.move(Direction.RIGHT);

        assertEquals(MoveResult.PUSHED, result);
        assertEquals(new Position(0, 1), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(0, 2)));
        assertCounts(level, 1, 1);
    }

    @Test
    @DisplayName("Un movimiento bloqueado no cambia los contadores")
    void blockedMoveCountsNothing() {
        Level level = TestLevels.level(
                "######",
                "#@~# #",
                "######");
        level.move(Direction.RIGHT);
        assertCounts(level, 1, 0);

        MoveResult result = level.move(Direction.RIGHT);

        assertEquals(MoveResult.BLOCKED, result);
        assertCounts(level, 1, 0);
    }

    @Test
    @DisplayName("Fuera del hielo todo cuenta como antes: caminar suma 1 movimiento, empujar suma 1 movimiento y 1 empuje")
    void normalMovesAreUnchanged() {
        Level level = TestLevels.level(
                "########",
                "#@ $.  #",
                "########");

        level.move(Direction.RIGHT);
        assertCounts(level, 1, 0);

        level.move(Direction.RIGHT);
        assertCounts(level, 2, 1);
    }
}
