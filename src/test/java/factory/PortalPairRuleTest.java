package factory;

import exception.InvalidLevelException;
import exception.LevelFormatRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Each portal letter must appear exactly twice; the error points at the first portal of that letter. */
class PortalPairRuleTest {

    private static final int FIRST_MAP_LINE = TestLevels.HEADER_LINES + 1;

    private static InvalidLevelException loadInvalid(String... rows) {
        return assertThrows(InvalidLevelException.class, () -> new LevelLoader().parse(TestLevels.text(rows)));
    }

    @Test
    @DisplayName("Una letra de portal que aparece 1 sola vez es un error de carga")
    void singlePortalIsRejected() {
        InvalidLevelException e = loadInvalid("#######", "#@ P  #", "#######");

        assertEquals(LevelFormatRule.PORTAL_NOT_PAIRED, e.getRule());
    }

    @Test
    @DisplayName("Una letra de portal que aparece 3 veces es un error de carga")
    void threePortalsAreRejected() {
        InvalidLevelException e = loadInvalid("#########", "#@ P P P#", "#########");

        assertEquals(LevelFormatRule.PORTAL_NOT_PAIRED, e.getRule());
    }

    @Test
    @DisplayName("El error indica la linea del primer portal de esa letra")
    void errorPointsAtFirstPortalLine() {
        InvalidLevelException e = loadInvalid("#######", "#@    #", "#  Q  #", "#######");

        assertEquals(FIRST_MAP_LINE + 2, e.getLine());
    }

    @Test
    @DisplayName("El error dice que letra quedo sin pareja")
    void errorNamesTheLetter() {
        InvalidLevelException e = loadInvalid("#########", "#@ P  PR#", "#########");

        assertEquals("R", e.getDetail());
    }

    @Test
    @DisplayName("Varios pares bien formados cargan sin error")
    void wellFormedPairsLoad() {
        assertDoesNotThrow(() -> new LevelLoader().parse(TestLevels.text("##########", "#@PQ  QP #", "##########")));
    }
}
