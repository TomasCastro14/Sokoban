package factory;

import exception.InvalidLevelException;
import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.box.KeyBox;
import model.box.NormalBox;
import model.box.WeakBox;
import model.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/** Every map is closed, has one player and as many boxes as points of each type. */
class WeakBoxLoadingTest {

    private static Board board(String... rows) {
        return TestLevels.level(rows).getBoard();
    }

    @ParameterizedTest(name = "el digito {0} crea una WeakBox con resistencia {0}")
    @ValueSource(chars = {'1', '2', '3', '4', '5', '6', '7', '8', '9'})
    @DisplayName("1. Cada digito 1-9 crea una WeakBox con exactamente esa resistencia")
    void digitIsResistance(char digit) {
        Board board = board(
                "######",
                "#@" + digit + ": #",
                "######");

        WeakBox box = assertInstanceOf(WeakBox.class, board.getBoxAt(new Position(1, 2)));
        assertEquals(Character.digit(digit, 10), box.getRemainingPushes());
    }

    @Test
    @DisplayName("2. La WeakBox queda en la fila y columna del digito en el mapa")
    void weakBoxIsAtItsPosition() {
        Board board = board(
                "#######",
                "#@    #",
                "#   4 #",
                "#  :  #",
                "#######");

        assertInstanceOf(WeakBox.class, board.getBoxAt(new Position(2, 4)));
        assertNull(board.getBoxAt(new Position(1, 2)));
    }

    @Test
    @DisplayName("3. Varias WeakBox con numeros distintos son independientes: empujar una no afecta a la otra")
    void weakBoxesAreIndependent() {
        Level level = TestLevels.level(
                "########",
                "#@2  : #",
                "# 7  : #",
                "########");
        WeakBox two = (WeakBox) level.getBoard().getBoxAt(new Position(1, 2));
        WeakBox seven = (WeakBox) level.getBoard().getBoxAt(new Position(2, 2));

        level.move(Direction.RIGHT);

        assertNotSame(two, seven);
        assertEquals(1, two.getRemainingPushes());
        assertEquals(7, seven.getRemainingPushes());
    }

    @Test
    @DisplayName("4. '$' sigue creando una NormalBox")
    void dollarIsNormalBox() {
        assertInstanceOf(NormalBox.class, board("#####", "#@$.#", "#####").getBoxAt(new Position(1, 2)));
    }

    @Test
    @DisplayName("5. 'K' sigue creando una KeyBox")
    void kIsKeyBox() {
        assertInstanceOf(KeyBox.class, board("#####", "#@K;#", "#####").getBoxAt(new Position(1, 2)));
    }

    @Test
    @DisplayName("6. '0' no crea una WeakBox: llega a la validacion de caracter desconocido")
    void zeroIsUnknownCharacter() {
        InvalidLevelException e = assertThrows(InvalidLevelException.class,
                () -> new LevelLoader().parse(TestLevels.text("#####", "#@0 #", "#####")));

        assertTrue(e.getMessage().startsWith("Unknown character '0'"), e.getMessage());
    }

    @Test
    @DisplayName("7. El simbolo de cada WeakBox sale del rango de WeakBox (MIN_PUSHES..MAX_PUSHES)")
    void creatorSymbolsFollowWeakBoxRange() {
        String symbols = WeakBoxCreator.symbols();

        assertEquals(WeakBox.MAX_PUSHES - WeakBox.MIN_PUSHES + 1, symbols.length());
        assertEquals(Character.forDigit(WeakBox.MIN_PUSHES, 10), symbols.charAt(0));
        assertEquals(Character.forDigit(WeakBox.MAX_PUSHES, 10), symbols.charAt(symbols.length() - 1));
    }
}
