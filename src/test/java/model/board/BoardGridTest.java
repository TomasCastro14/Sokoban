package model.board;

import model.box.BoxType;
import model.box.NormalBox;
import model.box.Player;
import model.tile.Floor;
import model.tile.StoragePoint;
import model.tile.Tile;
import model.tile.Wall;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Grid structure and access by position, with boards built by hand (no Loader). */
class BoardGridTest {
    private static final int ROWS = 3;
    private static final int COLS = 4;
    private static final Position PLAYER = new Position(1, 1);

    /** 3x4: walls on the border, floor inside, a storage point at (1,2), the player at (1,1). */
    private static Cell[][] grid() {
        Cell[][] cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Position p = new Position(row, col);
                boolean border = row == 0 || col == 0 || row == ROWS - 1 || col == COLS - 1;
                Tile tile = border ? new Wall() : new Floor();
                cells[row][col] = new Cell(p, p.equals(new Position(1, 2)) ? new StoragePoint(BoxType.NORMAL) : tile);
            }
        }
        cells[PLAYER.row()][PLAYER.col()].place(new Player(Direction.DOWN));
        return cells;
    }

    private static Board board() {
        return new Board(grid(), PLAYER);
    }

    @Test
    @DisplayName("1. Un tablero de 3x4 tiene 3 filas y 4 columnas")
    void sizeIsRowsByColumns() {
        Board board = board();

        assertEquals(ROWS, board.getRows());
        assertEquals(COLS, board.getCols());
    }

    @Test
    @DisplayName("2. cellAt devuelve la celda de esa posicion, con su elemento fijo")
    void cellAtReturnsThatCell() {
        Cell cell = board().cellAt(new Position(1, 2));

        assertEquals(new Position(1, 2), cell.getPosition());
        assertInstanceOf(StoragePoint.class, cell.getTile());
    }

    @Test
    @DisplayName("3. Las 4 esquinas y una celda interior se acceden sin confundir filas y columnas")
    void cornersAndInside() {
        Board board = board();

        for (Position corner : List.of(new Position(0, 0), new Position(0, COLS - 1),
                new Position(ROWS - 1, 0), new Position(ROWS - 1, COLS - 1))) {
            assertEquals(corner, board.cellAt(corner).getPosition(), corner.toString());
            assertInstanceOf(Wall.class, board.cellAt(corner).getTile(), corner.toString());
        }
        assertInstanceOf(Floor.class, board.cellAt(new Position(1, 1)).getTile());
    }

    @Test
    @DisplayName("4. Una posicion fuera de la grilla da IllegalArgumentException con la posicion en el mensaje")
    void outsideTheGridIsAClearError() {
        Board board = board();

        for (Position outside : List.of(new Position(-1, 0), new Position(0, -1),
                new Position(ROWS, 0), new Position(0, COLS))) {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> board.cellAt(outside));
            assertTrue(e.getMessage().contains(outside.toString()), e.getMessage());
            assertFalse(board.isInside(outside));
        }
    }

    @Test
    @DisplayName("5. neighbor devuelve la celda vecina en cada una de las 4 direcciones")
    void neighborInEachDirection() {
        Board board = board();

        assertEquals(new Position(0, 1), board.neighbor(PLAYER, Direction.UP).getPosition());
        assertEquals(new Position(2, 1), board.neighbor(PLAYER, Direction.DOWN).getPosition());
        assertEquals(new Position(1, 0), board.neighbor(PLAYER, Direction.LEFT).getPosition());
        assertEquals(new Position(1, 2), board.neighbor(PLAYER, Direction.RIGHT).getPosition());
    }

    @Test
    @DisplayName("6. Una celda con ocupante y una sin ocupante se distinguen")
    void occupiedAndFreeCells() {
        Board board = board();

        assertFalse(board.cellAt(PLAYER).isFree());
        assertInstanceOf(Player.class, board.cellAt(PLAYER).getOccupant());
        assertTrue(board.cellAt(new Position(1, 2)).isFree());
        assertNull(board.cellAt(new Position(1, 2)).getOccupant());
    }

    @Test
    @DisplayName("7. Poner y sacar un ocupante cambia esa celda y no la vecina")
    void placeAndRemoveOnlyTouchThatCell() {
        Board board = board();
        NormalBox box = new NormalBox();
        Cell cell = board.cellAt(new Position(1, 2));

        cell.place(box);
        assertSame(box, cell.getOccupant());
        assertInstanceOf(Player.class, board.neighbor(new Position(1, 2), Direction.LEFT).getOccupant());

        assertSame(box, cell.remove());
        assertTrue(cell.isFree());
        assertInstanceOf(Player.class, board.cellAt(PLAYER).getOccupant());
    }

    @Test
    @DisplayName("8. Una caja sobre un punto de guardado: la celda conserva el punto como terreno y la caja como ocupante")
    void boxOverStoragePointKeepsBothLayers() {
        Board board = board();
        NormalBox box = new NormalBox();

        board.placeBox(new Position(1, 2), box);

        assertInstanceOf(StoragePoint.class, board.getTile(new Position(1, 2)));
        assertSame(box, board.cellAt(new Position(1, 2)).getOccupant());
    }

    @Test
    @DisplayName("9. cells() recorre las N x M celdas, cada una una sola vez y en orden de lectura")
    void cellsVisitsEveryCellOnce() {
        List<Cell> cells = board().cells();

        assertEquals(ROWS * COLS, cells.size());
        assertEquals(ROWS * COLS, new HashSet<>(cells).size());
        assertEquals(new Position(0, 0), cells.get(0).getPosition());
        assertEquals(new Position(0, 1), cells.get(1).getPosition());
        assertEquals(new Position(ROWS - 1, COLS - 1), cells.get(cells.size() - 1).getPosition());
    }

    @Test
    @DisplayName("10. La lista de cells() no se puede modificar")
    void cellsListIsReadOnly() {
        List<Cell> cells = board().cells();

        assertThrows(UnsupportedOperationException.class, () -> cells.remove(0));
    }

    @Test
    @DisplayName("11. Modificar el arreglo que se le paso al constructor no cambia el tablero (copia defensiva)")
    void constructorCopiesTheGrid() {
        Cell[][] grid = grid();
        Board board = new Board(grid, PLAYER);
        Cell original = board.cellAt(new Position(1, 2));

        grid[1][2] = new Cell(new Position(1, 2), new Wall());

        assertSame(original, board.cellAt(new Position(1, 2)));
    }

    @Test
    @DisplayName("12. Datos invalidos al construir fallan enseguida: null, grilla vacia, filas desparejas o jugador fuera")
    void invalidGridsAreRejected() {
        assertThrows(NullPointerException.class, () -> new Board(null, PLAYER));
        assertThrows(NullPointerException.class, () -> new Board(grid(), null));
        assertThrows(IllegalArgumentException.class, () -> new Board(new Cell[0][0], PLAYER));

        Cell[][] ragged = grid();
        ragged[2] = new Cell[COLS - 1];
        assertThrows(IllegalArgumentException.class, () -> new Board(ragged, PLAYER));

        Cell[][] withHole = grid();
        withHole[2][2] = null;
        assertThrows(IllegalArgumentException.class, () -> new Board(withHole, PLAYER));

        assertThrows(IllegalArgumentException.class, () -> new Board(grid(), new Position(ROWS, 0)));
    }
}
