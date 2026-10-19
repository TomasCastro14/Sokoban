package model.tile;

public class Portal extends Tile {
    private final char letter;

    public Portal(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }
}
