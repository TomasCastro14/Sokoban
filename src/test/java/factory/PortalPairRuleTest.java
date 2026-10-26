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

        assertEquals("'R' aparece 1 vez", e.getDetail());
    }

    @Test
    @DisplayName("Varios pares bien formados cargan sin error")
    void wellFormedPairsLoad() {
        assertDoesNotThrow(() -> new LevelLoader().parse(TestLevels.text("##########", "#@PQ  QP #", "##########")));
    }

    @Test
    @DisplayName("Con tres Q el error dice la letra y cuantas veces aparecio")
    void threeQsReportCount() {
        InvalidLevelException e = loadInvalid("#########", "#@ Q Q Q#", "#########");

        assertEquals("'Q' aparece 3 veces", e.getDetail());
        assertEquals(FIRST_MAP_LINE + 1, e.getLine());
    }

    @Test
    @DisplayName("Con P x2 y R x1 el error menciona la R")
    void validPairPlusSingleRMentionsR() {
        InvalidLevelException e = loadInvalid("#########", "#@ P  PR#", "#########");

        assertEquals(LevelFormatRule.PORTAL_NOT_PAIRED, e.getRule());
        assertTrue(e.getMessage().contains("'R'"), e.getMessage());
    }

    @Test
    @DisplayName("Con un solo P el error apunta a la linea de ese P")
    void singlePReportsItsLine() {
        InvalidLevelException e = loadInvalid("#######", "#@    #", "#  P  #", "#######");

        assertEquals(LevelFormatRule.PORTAL_NOT_PAIRED, e.getRule());
        assertEquals(FIRST_MAP_LINE + 2, e.getLine());
    }

    @Test
    @DisplayName("P x2 y Q x2 cargan sin error")
    void twoPairsLoad() {
        assertDoesNotThrow(() -> new LevelLoader().parse(TestLevels.text("#########", "#@PQ QP #", "#########")));
    }

    @Test
    @DisplayName("Un nivel sin portales carga sin error")
    void noPortalsIsValid() {
        assertDoesNotThrow(() -> new LevelLoader().parse(TestLevels.text("#####", "#@$.#", "#####")));
    }
}
