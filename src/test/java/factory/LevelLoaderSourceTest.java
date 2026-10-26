package factory;

import exception.InvalidLevelException;
import exception.LevelNotFoundException;
import model.board.Position;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import port.LevelSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** The loader depends on the LevelSource abstraction, so it can be fed from memory. */
class LevelLoaderSourceTest {

    /** In-memory source with a single level 1. */
    private static final LevelSource ONE_LEVEL_IN_MEMORY = new LevelSource() {
        @Override
        public List<String> lines(int levelNumber) throws LevelNotFoundException {
            if (!exists(levelNumber)) {
                throw new LevelNotFoundException("Level " + levelNumber + " not found in memory");
            }
            return List.of("nombre: En memoria", "tiempo: 60", "objetivo: 100", "---",
                    "#####", "#@$.#", "#####");
        }

        @Override
        public boolean exists(int levelNumber) {
            return levelNumber == 1;
        }
    };

    @Test
    @DisplayName("1. load(n) arma el nivel con las lineas que da la fuente, sin tocar archivos")
    void loadsFromAnySource() throws Exception {
        Level level = new LevelLoader(ONE_LEVEL_IN_MEMORY).load(1);

        assertEquals("En memoria", level.getName());
        assertEquals(new Position(1, 1), level.getBoard().getPlayerPosition());
    }

    @Test
    @DisplayName("2. Si la fuente no tiene el nivel, load(n) propaga LevelNotFoundException")
    void missingLevelPropagates() {
        assertThrows(LevelNotFoundException.class, () -> new LevelLoader(ONE_LEVEL_IN_MEMORY).load(2));
    }

    @Test
    @DisplayName("3. exists(n) le pregunta a la fuente")
    void existsAsksTheSource() {
        LevelLoader loader = new LevelLoader(ONE_LEVEL_IN_MEMORY);

        assertTrue(loader.exists(1));
        assertFalse(loader.exists(2));
    }

    @Test
    @DisplayName("4. Un nivel que existe pero esta mal formado sigue dando InvalidLevelException, no LevelNotFound")
    void formatErrorsStayFormatErrors() {
        LevelSource broken = new LevelSource() {
            @Override
            public List<String> lines(int levelNumber) {
                return List.of("nombre: Roto", "tiempo: 60", "objetivo: 100", "---", "#X#");
            }

            @Override
            public boolean exists(int levelNumber) {
                return true;
            }
        };

        assertThrows(InvalidLevelException.class, () -> new LevelLoader(broken).load(1));
    }
}
