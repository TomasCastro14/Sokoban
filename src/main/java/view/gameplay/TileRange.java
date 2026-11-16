package view.gameplay;

import model.board.Position;

import java.util.function.Consumer;

/** Rectángulo de casilleros, bordes incluidos (por ejemplo, los que entran en pantalla). */
record TileRange(int firstRow, int lastRow, int firstCol, int lastCol) {

    TileRange grow(int cells) {
        return new TileRange(firstRow - cells, lastRow + cells, firstCol - cells, lastCol + cells);
    }

    void forEach(Consumer<Position> action) {
        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = firstCol; col <= lastCol; col++) {
                action.accept(new Position(row, col));
            }
        }
    }
}
