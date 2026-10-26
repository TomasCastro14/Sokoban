package factory;

import model.board.Position;
import model.game.Level;
import model.tile.Ice;
import model.tile.Portal;
import model.tile.mechanism.Door;
import model.tile.mechanism.KeyTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SpecialTileLoadingTest {

    @Test
    @DisplayName("El loader crea un Ice al leer ~")
    void loaderCreatesIce() {
        Level level = TestLevels.level(
                "#####",
                "#@~ #",
                "#####");

        assertInstanceOf(Ice.class, level.getBoard().getTile(new Position(1, 2)));
    }

    @ParameterizedTest
    @ValueSource(chars = {'a', 'b', 'c', 'd', 'e'})
    @DisplayName("El loader crea una KeyTile y una Door con el grupo de su letra")
    void loaderCreatesKeyTileAndDoorOfTheGroup(char key) {
        char door = Character.toUpperCase(key);
        Level level = TestLevels.level(
                "######",
                "#@" + key + door + " #",
                "######");

        KeyTile keyTile = assertInstanceOf(KeyTile.class, level.getBoard().getTile(new Position(1, 2)));
        Door loadedDoor = assertInstanceOf(Door.class, level.getBoard().getTile(new Position(1, 3)));
        assertEquals(door, keyTile.getGroup());
        assertEquals(door, loadedDoor.getGroup());
    }

    @Test
    @DisplayName("Una Door arranca cerrada")
    void doorStartsClosed() {
        Level level = TestLevels.level(
                "######",
                "#@aA #",
                "######");

        Door door = assertInstanceOf(Door.class, level.getBoard().getTile(new Position(1, 3)));
        assertFalse(door.isOpen());
    }

    @ParameterizedTest
    @ValueSource(chars = {'P', 'Q', 'R', 'S'})
    @DisplayName("El loader crea los Portal con la letra correcta")
    void loaderCreatesPortalsWithTheirLetter(char letter) {
        Level level = TestLevels.level(
                "#######",
                "#@" + letter + " " + letter + "#",
                "#######");

        Portal first = assertInstanceOf(Portal.class, level.getBoard().getTile(new Position(1, 2)));
        Portal second = assertInstanceOf(Portal.class, level.getBoard().getTile(new Position(1, 4)));
        assertEquals(letter, first.getLetter());
        assertEquals(letter, second.getLetter());
    }

    @Test
    @DisplayName("Un Portal recien creado no tiene pareja")
    void newPortalHasNoPair() {
        assertTrue(new Portal('P').linkedPositions().isEmpty());
    }
}
