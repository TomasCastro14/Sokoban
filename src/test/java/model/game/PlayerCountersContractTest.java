package model.game;

import factory.TestLevels;
import model.board.Direction;
import model.board.MoveResult;
import model.board.Position;
import model.box.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract of the player's data as the class diagram models it: the Player is an occupant,
 * its position lives only in Board, and the move/push counters live only in LevelStats.
 */
class PlayerCountersContractTest {

    /** RIGHT walks, then RIGHT pushes the box; the room below is free floor. */
    private static Level room() {
        return TestLevels.level(
                "########",
                "#@ $  .#",
                "#      #",
                "########");
    }

    @Test
    @DisplayName("1. Un nivel recien cargado tiene al jugador en su posicion y los contadores en 0")
    void freshLevelStartsAtZero() {
        Level level = room();

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertInstanceOf(Player.class, level.getBoard().cellAt(new Position(1, 1)).getOccupant());
        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("2. Unas estadisticas nuevas arrancan en 0 y solo suben con metodos con intencion")
    void statsOnlyGoUp() {
        LevelStats stats = new LevelStats();
        assertEquals(0, stats.getMoves());
        assertEquals(0, stats.getPushes());

        stats.addMove();
        stats.addMove();
        stats.addPush();

        assertEquals(2, stats.getMoves());
        assertEquals(1, stats.getPushes());
    }

    @Test
    @DisplayName("3. Caminar suma 1 movimiento y ningun empuje")
    void walkCountsOneMove() {
        Level level = room();

        level.move(Direction.DOWN);

        assertEquals(1, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("4. Un empuje suma 1 movimiento y 1 empuje (regla de move-counts)")
    void pushCountsMoveAndPush() {
        Level level = room();
        level.move(Direction.RIGHT);

        assertEquals(MoveResult.PUSHED, level.move(Direction.RIGHT));

        assertEquals(2, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("5. Secuencia mezclada (mover, empujar, mover, mover) da los contadores esperados")
    void mixedSequence() {
        Level level = room();

        level.move(Direction.RIGHT); // walk
        level.move(Direction.RIGHT); // push
        level.move(Direction.DOWN);  // walk
        level.move(Direction.LEFT);  // walk

        assertEquals(4, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes());
    }

    @Test
    @DisplayName("6. La posicion del jugador se lee del Board y cambia al moverse")
    void positionComesFromTheBoard() {
        Level level = room();

        level.move(Direction.DOWN);

        assertEquals(new Position(2, 1), level.getBoard().getPlayerPosition());
        assertInstanceOf(Player.class, level.getBoard().cellAt(new Position(2, 1)).getOccupant());
    }

    @Test
    @DisplayName("7. Un movimiento bloqueado no cambia la posicion ni los contadores")
    void blockedMoveChangesNothing() {
        Level level = room();

        assertEquals(MoveResult.BLOCKED, level.move(Direction.UP));

        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
        assertEquals(0, level.getStats().getMoves());
        assertEquals(0, level.getStats().getPushes());
    }

    @Test
    @DisplayName("8. Recargar el nivel da contadores nuevos en 0 (no hay reset que recordar)")
    void reloadingGivesFreshCounters() {
        Level played = room();
        played.move(Direction.DOWN);

        Level reloaded = room();

        assertEquals(1, played.getStats().getMoves());
        assertEquals(0, reloaded.getStats().getMoves());
    }

    @Test
    @DisplayName("9. El jugador es un ocupante que no se puede empujar")
    void playerIsNotPushable() {
        assertFalse(new Player(Direction.DOWN).isPushable());
    }
}
