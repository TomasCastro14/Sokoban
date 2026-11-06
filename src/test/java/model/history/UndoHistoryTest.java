package model.history;

import model.board.MoveResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UndoHistoryTest {

    private final List<Integer> undone = new ArrayList<>();
    private final UndoHistory history = new UndoHistory();

    private Command command(int number) {
        return new Command() {
            @Override
            public MoveResult execute() {
                return MoveResult.OK;
            }

            @Override
            public void undo() {
                undone.add(number);
            }
        };
    }

    private void recordMoves(int from, int to) {
        for (int number = from; number <= to; number++) {
            history.record(command(number));
        }
    }

    @Test
    @DisplayName("Con el historial vacio no se puede deshacer y quedan todos los usos")
    void emptyHistory() {
        assertFalse(history.undo());
        assertTrue(undone.isEmpty());
        assertEquals(UndoHistory.MAX_CONSECUTIVE_UNDOS, history.remainingUndos());
    }

    @Test
    @DisplayName("Con menos de 5 movimientos, el undo vuelve al mas antiguo y vacia el historial")
    void fewerMovesThanABlock() {
        recordMoves(1, 3);

        assertTrue(history.undo());
        assertFalse(history.undo());

        assertEquals(List.of(1), undone);
    }

    @Test
    @DisplayName("Cada undo retrocede un bloque de 5 movimientos")
    void undoesABlockOfFive() {
        recordMoves(1, 7);

        history.undo();

        assertEquals(List.of(3), undone);
    }

    @Test
    @DisplayName("Tres undos seguidos retroceden 15 movimientos, de a bloques de 5")
    void threeUndosGoBackFifteenMoves() {
        recordMoves(1, 15);

        assertTrue(history.undo());
        assertTrue(history.undo());
        assertTrue(history.undo());
        recordMoves(16, 16);
        history.undo();

        assertEquals(List.of(11, 6, 1, 16), undone);
    }

    @Test
    @DisplayName("Sin usos disponibles no deja deshacer aunque quede historial")
    void noUndosLeft() {
        recordMoves(1, 20);
        history.undo();
        history.undo();
        history.undo();

        assertFalse(history.undo());
        assertEquals(0, history.remainingUndos());
        assertEquals(3, undone.size());
    }

    @Test
    @DisplayName("Un movimiento nuevo reinicia los usos")
    void newMoveResetsTheUses() {
        recordMoves(1, 20);
        history.undo();
        history.undo();
        history.undo();

        recordMoves(21, 21);

        assertEquals(UndoHistory.MAX_CONSECUTIVE_UNDOS, history.remainingUndos());
        assertTrue(history.undo());
    }

    @Test
    @DisplayName("El historial guarda como maximo 15 movimientos: los mas viejos se descartan")
    void keepsAtMostFifteenMoves() {
        recordMoves(1, 17);

        history.undo();
        history.undo();
        history.undo();

        assertEquals(List.of(13, 8, 3), undone);
    }
}