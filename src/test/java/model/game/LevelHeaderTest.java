package model.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LevelHeaderTest {

    @Test
    @DisplayName("1. LevelHeader guarda los tres datos tal cual")
    void keepsItsData() {
        LevelHeader header = new LevelHeader("Nivel 1", Duration.ofSeconds(90), 500);

        assertEquals("Nivel 1", header.name());
        assertEquals(Duration.ofSeconds(90), header.timeLimit());
        assertEquals(500, header.targetScore());
    }

    @Test
    @DisplayName("2. LevelHeader valida sus invariantes: nombre no vacio, tiempo > 0, objetivo >= 0")
    void validatesItsInvariants() {
        assertThrows(IllegalArgumentException.class, () -> new LevelHeader(" ", Duration.ofSeconds(90), 500));
        assertThrows(IllegalArgumentException.class, () -> new LevelHeader("N", Duration.ZERO, 500));
        assertThrows(IllegalArgumentException.class, () -> new LevelHeader("N", Duration.ofSeconds(90), -1));
        assertThrows(NullPointerException.class, () -> new LevelHeader(null, Duration.ofSeconds(90), 500));
    }
}
