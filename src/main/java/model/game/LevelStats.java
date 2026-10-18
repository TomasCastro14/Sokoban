package model.game;

public class LevelStats {
    private int moves;
    private int pushes;
    private int undos;
    private int storedBoxes;

    public void addMove() {
        moves++;
    }

    public void addPush() {
        pushes++;
    }

    public void addUndo() {
        undos++;
    }

    public void addStoredBox() {
        storedBoxes++;
    }

    public int getMoves() {
        return moves;
    }

    public int getPushes() {
        return pushes;
    }

    public int getUndos() {
        return undos;
    }

    public int getStoredBoxes() {
        return storedBoxes;
    }
}
