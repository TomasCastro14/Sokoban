package model.board;

import factory.TestLevels;
import model.box.NormalBox;
import model.tile.mechanism.Door;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReachabilityTest {

    private final ReachabilityCalculator reachability = new BfsReachability();

    private static Board board(String... rows) {
        return TestLevels.level(rows).getBoard();
    }

    private Set<Position> reachFromPlayer(Board board) {
        return reachability.from(board, board.getPlayerPosition());
    }

    private static Position at(int row, int col) {
        return new Position(row, col);
    }

    @Test
    @DisplayName("Habitacion cerrada sin obstaculos: alcanza todo el piso, incluida la celda del Sokoban")
    void reachesWholeRoom() {
        Board board = board(
                "#####",
                "#@  #",
                "#   #",
                "#####");

        assertEquals(Set.of(at(1, 1), at(1, 2), at(1, 3), at(2, 1), at(2, 2), at(2, 3)), reachFromPlayer(board));
    }

    @Test
    @DisplayName("Sokoban encerrado entre 4 paredes: solo su propia posicion")
    void enclosedPlayerReachesOnlyItself() {
        Board board = board(
                "#####",
                "##@##",
                "#####");

        assertEquals(Set.of(at(1, 2)), reachFromPlayer(board));
    }

    @Test
    @DisplayName("Paredes nunca son alcanzables")
    void wallsAreNeverReached() {
        Board board = board(
                "#####",
                "#@# #",
                "#####");

        assertFalse(reachFromPlayer(board).contains(at(1, 2)));
    }

    @Test
    @DisplayName("Zonas inaccesibles (%) nunca son alcanzables")
    void inaccessibleIsNeverReached() {
        Board board = board(
                "#####",
                "#@% #",
                "#####");

        assertFalse(reachFromPlayer(board).contains(at(1, 2)));
        assertFalse(reachFromPlayer(board).contains(at(1, 3)));
    }

    @Test
    @DisplayName("Puerta cerrada: solo la habitacion del Sokoban es alcanzable")
    void closedDoorBlocks() {
        Board board = board(
                "#####",
                "#@A #",
                "#####");

        assertEquals(Set.of(at(1, 1)), reachFromPlayer(board));
    }

    @Test
    @DisplayName("Puerta abierta: las dos habitaciones son alcanzables")
    void openDoorLetsThrough() {
        Board board = board(
                "#####",
                "#@A #",
                "#####");
        ((Door) board.getTile(at(1, 2))).open();

        assertTrue(reachFromPlayer(board).contains(at(1, 3)));
    }

    @Test
    @DisplayName("Una puerta abierta y otra cerrada: se alcanza solo lo que corresponde")
    void oneOpenOneClosedDoor() {
        Board board = board(
                "#######",
                "# B@A #",
                "#######");
        ((Door) board.getTile(at(1, 4))).open();

        Set<Position> reached = reachFromPlayer(board);

        assertTrue(reached.contains(at(1, 5)));
        assertFalse(reached.contains(at(1, 1)));
    }

    @Test
    @DisplayName("Dos celdas que solo se tocan en diagonal no se conectan")
    void noDiagonals() {
        Board board = board(
                "####",
                "#@##",
                "## #",
                "####");

        assertFalse(reachFromPlayer(board).contains(at(2, 2)));
    }

    @Test
    @DisplayName("Una caja no bloquea el alcance")
    void boxesDoNotBlock() {
        Board board = board(
                "#####",
                "#@$ #",
                "#####");

        assertTrue(reachFromPlayer(board).contains(at(1, 3)));
    }

    @Test
    @DisplayName("Un punto de guardado vacio (agujero) no bloquea el alcance")
    void emptyStoragePointDoesNotBlock() {
        Board board = board(
                "#####",
                "#@. #",
                "#####");

        assertTrue(reachFromPlayer(board).contains(at(1, 3)));
    }

    @Test
    @DisplayName("Pasillo en anillo: termina y cada posicion aparece una sola vez")
    void ringTerminates() {
        Board board = board(
                "#####",
                "#@  #",
                "# # #",
                "#   #",
                "#####");

        Set<Position> reached = assertTimeoutPreemptively(Duration.ofSeconds(1), () -> reachFromPlayer(board));

        assertEquals(8, reached.size());
    }

    @Test
    @DisplayName("El calculo no modifica el Board")
    void doesNotModifyTheBoard() {
        Board board = board(
                "######",
                "#@$. #",
                "##A###",
                "#  $ #",
                "######");
        Position player = board.getPlayerPosition();
        List<Object> before = snapshot(board);

        reachFromPlayer(board);

        assertEquals(player, board.getPlayerPosition());
        assertEquals(before, snapshot(board));
    }

    /** Identity of each tile and occupant, cell by cell. */
    private static List<Object> snapshot(Board board) {
        List<Object> contents = new ArrayList<>();
        for (Cell cell : board.cells()) {
            contents.add(cell.getTile());
            contents.add(cell.getOccupant());
        }
        return contents;
    }

    @Test
    @DisplayName("Llamarlo dos veces da el mismo resultado")
    void sameResultTwice() {
        Board board = board(
                "#####",
                "#@A #",
                "#####");

        assertEquals(reachFromPlayer(board), reachFromPlayer(board));
    }

    @Test
    @DisplayName("El resultado no se puede modificar desde afuera")
    void resultIsUnmodifiable() {
        Board board = board(
                "####",
                "#@ #",
                "####");

        Set<Position> reached = reachFromPlayer(board);

        assertThrows(UnsupportedOperationException.class, () -> reached.add(at(0, 0)));
    }

    @Test
    @DisplayName("Una caja empujada a un punto (puente) tampoco bloquea")
    void storedBoxDoesNotBlock() {
        Board board = board(
                "######",
                "#@$. #",
                "######");
        board.placeBox(at(1, 3), new NormalBox());

        assertTrue(reachFromPlayer(board).contains(at(1, 4)));
    }
}
