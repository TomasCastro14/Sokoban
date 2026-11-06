package port;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class LevelRecordTest {

    @ParameterizedTest
    @CsvSource({"0, 10, 2", "-1, 10, 2", "1, -1, 0", "1, 10, -1", "1, 3, 4"})
    @DisplayName("1. Rechaza nivel menor a 1, valores negativos y mas empujes que movimientos")
    void rejectsInvalidData(int levelNumber, int moves, int pushes) {
        assertThrows(IllegalArgumentException.class, () -> new LevelRecord(levelNumber, moves, pushes));
    }

    @Test
    @DisplayName("2. Acepta un resultado valido")
    void acceptsValidData() {
        LevelRecord record = new LevelRecord(2, 30, 8);

        assertEquals(2, record.levelNumber());
        assertEquals(30, record.fewestMoves());
        assertEquals(8, record.fewestPushes());
    }

    @Test
    @DisplayName("3. El mejor resultado es el de menos movimientos")
    void bestHasFewerMoves() {
        LevelRecord slow = new LevelRecord(1, 40, 5);
        LevelRecord fast = new LevelRecord(1, 25, 9);

        assertSame(fast, slow.bestOf(fast));
        assertSame(fast, fast.bestOf(slow));
    }

    @Test
    @DisplayName("4. Con los mismos movimientos, desempata el de menos empujes")
    void fewerPushesBreaksTie() {
        LevelRecord morePushes = new LevelRecord(1, 25, 9);
        LevelRecord fewerPushes = new LevelRecord(1, 25, 6);

        assertSame(fewerPushes, morePushes.bestOf(fewerPushes));
    }

    @Test
    @DisplayName("5. No compara resultados de niveles distintos")
    void rejectsDifferentLevels() {
        LevelRecord first = new LevelRecord(1, 25, 6);
        LevelRecord second = new LevelRecord(2, 25, 6);

        assertThrows(IllegalArgumentException.class, () -> first.bestOf(second));
    }
}