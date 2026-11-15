package model.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.game.score.LevelGoals;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LevelHeaderTest {

    private static final LevelGoals GOALS = new LevelGoals(60, 20, 1);

    @Test
    @DisplayName("1. LevelHeader guarda los tres datos tal cual")
    void keepsItsData() {
        LevelHeader header = new LevelHeader("Nivel 1", Duration.ofSeconds(90), GOALS);

        assertEquals("Nivel 1", header.name());
        assertEquals(Duration.ofSeconds(90), header.timeLimit());
        assertEquals(GOALS, header.goals());
    }

    @Test
    @DisplayName("2. LevelHeader valida sus invariantes: nombre no vacio, tiempo > 0, metas presentes y >= 0")
    void validatesItsInvariants() {
        assertThrows(IllegalArgumentException.class, () -> new LevelHeader(" ", Duration.ofSeconds(90), GOALS));
        assertThrows(IllegalArgumentException.class, () -> new LevelHeader("N", Duration.ZERO, GOALS));
        assertThrows(NullPointerException.class, () -> new LevelHeader("N", Duration.ofSeconds(90), null));
        assertThrows(IllegalArgumentException.class, () -> new LevelGoals(-1, 20, 1));
        assertThrows(NullPointerException.class, () -> new LevelHeader(null, Duration.ofSeconds(90), GOALS));
    }
}
