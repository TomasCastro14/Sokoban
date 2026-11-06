package model.game;

import factory.TestLevels;
import model.board.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelStatsTest {

    @Test
    @DisplayName("1. Los contadores arrancan en 0 y suman de a uno")
    void countersStartAtZeroAndIncrement() {
        LevelStats stats = new LevelStats();
        assertEquals(0, stats.getMoves());
        assertEquals(0, stats.getPushes());
        assertEquals(0, stats.getUndos());

        stats.addMove();
        stats.addMove();
        stats.addPush();
        stats.addUndo();

        assertEquals(2, stats.getMoves());
        assertEquals(1, stats.getPushes());
        assertEquals(1, stats.getUndos());
    }

    @Test
    @DisplayName("1. Cuenta bien movimientos, empujes y undos durante una partida")
    void countsDuringPlay() {
        Level level = TestLevels.level(
                "########",
                "#@     #",
                "#  $  .#",
                "#      #",
                "########");

        level.move(Direction.RIGHT);
        level.move(Direction.RIGHT);
        level.move(Direction.DOWN);
        level.move(Direction.UP);
        level.move(Direction.LEFT);
        level.move(Direction.RIGHT);
        level.move(Direction.LEFT);
        level.move(Direction.RIGHT);
        level.undo();

        assertEquals(3, level.getStats().getMoves());
        assertEquals(1, level.getStats().getPushes());
        assertEquals(1, level.getStats().getUndos());
    }
}