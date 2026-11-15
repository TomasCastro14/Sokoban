package model.game.progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * What the player achieved during this run of the application, and nothing else:
 * which levels are unlocked, the best stars and the best score of each level.
 *
 * It lives only in memory. Nothing is written to disk, so every launch starts from scratch:
 * level 1 unlocked and 0 stars everywhere. It never holds the state of a level being played
 * (board, boxes, moves, undo history): a level is always rebuilt from its .txt file.
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

    /** The best stars of this session; 0 if the level was never won. */
    public int getStars(int level) {
        requireLevel(level);
        return starsByLevel.getOrDefault(level, MIN_STARS);
    }

    /** The best score of this session; empty if the level was never won. */
    public OptionalInt getBestScore(int level) {
        requireLevel(level);
        Integer best = bestScoreByLevel.get(level);
        return best == null ? OptionalInt.empty() : OptionalInt.of(best);
    }

    /**
     * Records a won level: keeps the stars and the score only if they beat this session's best,
     * and unlocks the next level. Only a win is recorded; losing or leaving a level never calls this.
     *
     * @return true if it is a new record for the session (first win, more stars or more points)
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

    private void requireLevel(int level) {
        if (level < FIRST_LEVEL || level > levelCount) {
            throw new IllegalArgumentException("Level " + level + " is out of range " + FIRST_LEVEL + ".." + levelCount);
        }
    }

    private static void requireStars(int stars) {
        if (stars < MIN_STARS || stars > MAX_STARS) {
            throw new IllegalArgumentException("Stars must be between " + MIN_STARS + " and " + MAX_STARS + ", got " + stars);
        }
    }
}
