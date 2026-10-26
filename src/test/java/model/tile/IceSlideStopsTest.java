package model.tile;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** A slide stops on the current cell when the next one is blocked, even if it is still ice. */
class IceSlideStopsTest {

    @Test
    @DisplayName("El jugador se desliza y frena justo antes de una pared")
    void playerStopsBeforeWall() {
        Level level = TestLevels.level(
                "#######",
                "#@~~~~#",
                "#######");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 5), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("El jugador se desliza y frena antes de una caja, que no se mueve")
    void playerStopsBeforeBoxWithoutPushingIt() {
        Level level = TestLevels.level(
                "#######",
                "#@~~$.#",
                "#######");

        MoveResult result = level.move(Direction.RIGHT);

        assertEquals(MoveResult.OK, result);
        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 4)));
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("Una caja se desliza y frena antes de una pared")
    void boxStopsBeforeWall() {
        Level level = TestLevels.level(
                "######",
                "#@$~~#",
                "# .  #",
                "######");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 4)));
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
    }

    @Test
    @DisplayName("Una caja se desliza y frena antes de otra caja, sin empujarla")
    void boxStopsBeforeAnotherBox() {
        Level level = TestLevels.level(
                "#######",
                "#@$~~$#",
                "# ..  #",
                "#######");

        level.move(Direction.RIGHT);

        assertNotNull(level.getBoard().getBoxAt(new Position(1, 4)));
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 5)), "the other box does not move");
        assertNull(level.getBoard().getBoxAt(new Position(1, 3)));
    }

    @Test
    @DisplayName("Empujar una caja que esta sobre hielo contra una pared da BLOCKED y no cambia los contadores")
    void pushingBoxOnIceAgainstWallIsBlocked() {
        Level level = TestLevels.level(
                "######",
                "#@$~~#",
                "# .  #",
                "######");
        level.move(Direction.RIGHT); // the box slides and stops on the last ice cell, against the wall
        level.move(Direction.RIGHT); // the player walks up to the box
        int moves = level.getStats().getMoves();
        int pushes = level.getStats().getPushes();

        MoveResult result = level.move(Direction.RIGHT);

        assertEquals(MoveResult.BLOCKED, result);
        assertEquals(moves, level.getStats().getMoves());
        assertEquals(pushes, level.getStats().getPushes());
        assertNotNull(level.getBoard().getBoxAt(new Position(1, 4)));
    }

    @Test
    @DisplayName("El deslizamiento frena antes de una celda inaccesible (%)")
    void slideStopsBeforeInaccessibleCell() {
        Level level = TestLevels.level(
                "########",
                "#@~~%  #",
                "########");

        level.move(Direction.RIGHT);

        assertEquals(new Position(1, 3), level.getBoard().getPlayerPosition());
    }
}
