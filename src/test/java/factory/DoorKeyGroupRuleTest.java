package factory;

import exception.InvalidLevelException;
import exception.LevelFormatRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Every key tile needs a door of its group and every door a key tile; the error points at the one left alone. */
class DoorKeyGroupRuleTest {

    private static final int FIRST_MAP_LINE = TestLevels.HEADER_LINES + 1;

    private static InvalidLevelException loadInvalid(String... rows) {
        return assertThrows(InvalidLevelException.class, () -> new LevelLoader().parse(TestLevels.text(rows)));
    }

    private static void assertValid(String... rows) {
        assertDoesNotThrow(() -> new LevelLoader().parse(TestLevels.text(rows)));
    }

    @Test
    @DisplayName("Una baldosa a sin puerta A es un error de carga")
    void keyTileWithoutDoor() {
        InvalidLevelException e = loadInvalid("#######", "#@    #", "#  a  #", "#######");

        assertEquals(LevelFormatRule.DOOR_KEY_MISMATCH, e.getRule());
        assertEquals(FIRST_MAP_LINE + 2, e.getLine());
    }

    @Test
    @DisplayName("Una puerta B sin baldosa b es un error de carga")
    void doorWithoutKeyTile() {
        InvalidLevelException e = loadInvalid("#######", "#@ B  #", "#######");

        assertEquals(LevelFormatRule.DOOR_KEY_MISMATCH, e.getRule());
        assertEquals(FIRST_MAP_LINE + 1, e.getLine());
        assertTrue(e.getMessage().contains("'B'"), e.getMessage());
    }

    @Test
    @DisplayName("Un grupo a con una A y otro c con dos C cargan sin error")
    void completeGroupsLoad() {
        assertValid("#########", "#@ aA c #", "#  CC   #", "#########");
    }

    @Test
    @DisplayName("Un grupo a/A completo y otro b incompleto mencionan la b")
    void incompleteGroupMentionsItsLetter() {
        InvalidLevelException e = loadInvalid("#########", "#@ aA b #", "#########");

        assertEquals(LevelFormatRule.DOOR_KEY_MISMATCH, e.getRule());
        assertTrue(e.getMessage().contains("'b'"), e.getMessage());
    }

    @Test
    @DisplayName("Un nivel sin puertas ni baldosas carga sin error")
    void noDoorsNoKeyTilesIsValid() {
        assertValid("#####", "#@$.#", "#####");
    }

    @Test
    @DisplayName("Varias baldosas del mismo grupo son validas: cada una abre todo el grupo")
    void severalKeyTilesOfOneGroupAreValid() {
        assertValid("#########", "#@ a a A#", "#########");
    }
}
