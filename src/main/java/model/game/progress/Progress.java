package model.game.progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * What the player achieved, and nothing else:
 * which levels are unlocked, the best stars and the best score of each level.
 *
 * It knows nothing about disks or files: it can hand out a {@link ProgressData} snapshot and be
 * restored from one, and whoever wants to persist it (see port.ProgressStore) listens to its changes.
 * It never holds the state of a level being played (board, boxes, moves, undo history):
 * a level is always rebuilt from its .txt file.
 *
 * One instance is created by the composition root and handed to the controllers that need it.
 */
public final class Progress {
    public static final int FIRST_LEVEL = 1;
    public static final int MIN_STARS = 0;
    public static final int MAX_STARS = 3;

    private final int levelCount;
    private final Map<Integer, Integer> starsByLevel = new HashMap<>();
    private final Map<Integer, Integer> bestScoreByLevel = new HashMap<>();
    private final List<ProgressListener> listeners = new ArrayList<>();
    private int lastUnlockedLevel = FIRST_LEVEL;

    public Progress(int levelCount) {
        if (levelCount < FIRST_LEVEL) {
            throw new IllegalArgumentException("The game needs at least one level, got " + levelCount);
        }
        this.levelCount = levelCount;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public int getLastUnlockedLevel() {
        return lastUnlockedLevel;
    }

    public boolean isUnlocked(int level) {
        requireLevel(level);
        return level <= lastUnlockedLevel;
    }

    /** The best stars; 0 if the level was never won. */
    public int getStars(int level) {
        requireLevel(level);
        return starsByLevel.getOrDefault(level, MIN_STARS);
    }

    /** The best score; empty if the level was never won. */
    public OptionalInt getBestScore(int level) {
        requireLevel(level);
        Integer best = bestScoreByLevel.get(level);
        return best == null ? OptionalInt.empty() : OptionalInt.of(best);
    }

    /**
     * Records a won level: keeps the stars and the score only if they beat the best so far,
     * and unlocks the next level. Only a win is recorded; losing or leaving a level never calls this.
     *
     * @return true if it is a new record (first win, more stars or more points)
     */
    public boolean registerResult(int level, int stars, int score) {
        requireLevel(level);
        requireStars(stars);
        if (score < 0) {
            throw new IllegalArgumentException("The score cannot be negative, got " + score);
        }
        if (!isUnlocked(level)) {
            throw new IllegalStateException("Level " + level + " is locked, it cannot have been won");
        }
        boolean firstWin = !bestScoreByLevel.containsKey(level);
        boolean moreStars = stars > getStars(level);
        boolean morePoints = firstWin || score > bestScoreByLevel.get(level);

        starsByLevel.merge(level, stars, Math::max);
        bestScoreByLevel.merge(level, score, Math::max);
        lastUnlockedLevel = Math.max(lastUnlockedLevel, Math.min(level + 1, levelCount));
        notifyChanged();
        return moreStars || morePoints;
    }

    /** A copy of everything this progress remembers, to be saved somewhere. */
    public ProgressData snapshot() {
        return new ProgressData(lastUnlockedLevel, starsByLevel, bestScoreByLevel);
    }

    /**
     * Replaces this progress with a saved one. Saved data comes from outside (a file the user can edit
     * or that was written by a version with more levels), so it is taken leniently: levels out of range,
     * invalid stars and negative scores are ignored, and every won level keeps the next one unlocked.
     */
    public void restore(ProgressData data) {
        Objects.requireNonNull(data, "data");
        starsByLevel.clear();
        bestScoreByLevel.clear();
        lastUnlockedLevel = clampLevel(data.lastUnlockedLevel());
        data.bestScoreByLevel().forEach((level, score) -> {
            if (isValidLevel(level) && score != null && score >= 0) {
                bestScoreByLevel.put(level, score);
                int stars = data.starsByLevel().getOrDefault(level, MIN_STARS);
                starsByLevel.put(level, isValidStars(stars) ? stars : MIN_STARS);
                lastUnlockedLevel = Math.max(lastUnlockedLevel, clampLevel(level + 1));
            }
        });
        notifyChanged();
    }

    /** Back to how the application starts: level 1 unlocked and no stars. */
    public void reset() {
        starsByLevel.clear();
        bestScoreByLevel.clear();
        lastUnlockedLevel = FIRST_LEVEL;
        notifyChanged();
    }

    public void addListener(ProgressListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeListener(ProgressListener listener) {
        listeners.remove(listener);
    }

    private void notifyChanged() {
        List.copyOf(listeners).forEach(listener -> listener.progressChanged(this));
    }

    private boolean isValidLevel(Integer level) {
        return level != null && level >= FIRST_LEVEL && level <= levelCount;
    }

    private static boolean isValidStars(Integer stars) {
        return stars != null && stars >= MIN_STARS && stars <= MAX_STARS;
    }

    private int clampLevel(int level) {
        return Math.max(FIRST_LEVEL, Math.min(level, levelCount));
    }

    private void requireLevel(int level) {
        if (!isValidLevel(level)) {
            throw new IllegalArgumentException("Level " + level + " is out of range " + FIRST_LEVEL + ".." + levelCount);
        }
    }

    private static void requireStars(int stars) {
        if (!isValidStars(stars)) {
            throw new IllegalArgumentException("Stars must be between " + MIN_STARS + " and " + MAX_STARS + ", got " + stars);
        }
    }
}
