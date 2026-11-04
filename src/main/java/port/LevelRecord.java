package port;

public record LevelRecord(int levelNumber, int fewestMoves, int fewestPushes) {

    public LevelRecord {
        if (levelNumber < Progress.FIRST_LEVEL) {
            throw new IllegalArgumentException("Level number must be at least 1, got " + levelNumber);
        }
        if (fewestMoves < 0 || fewestPushes < 0) {
            throw new IllegalArgumentException(
                    "Moves and pushes cannot be negative, got " + fewestMoves + " and " + fewestPushes);
        }
        if (fewestPushes > fewestMoves) {
            throw new IllegalArgumentException(
                    "Every push is a move: " + fewestPushes + " pushes in " + fewestMoves + " moves");
        }
    }

    public LevelRecord bestOf(LevelRecord other) {
        if (other.levelNumber != levelNumber) {
            throw new IllegalArgumentException(
                    "Cannot compare level " + levelNumber + " with level " + other.levelNumber);
        }
        return isBetterThan(other) ? this : other;
    }

    private boolean isBetterThan(LevelRecord other) {
        if (fewestMoves != other.fewestMoves) {
            return fewestMoves < other.fewestMoves;
        }
        return fewestPushes <= other.fewestPushes;
    }
}