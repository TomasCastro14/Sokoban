package service;

import model.game.LevelStats;
import model.game.event.LevelCompletedListener;
import port.LevelRecord;
import port.Progress;
import port.ProgressRepository;

import java.util.Objects;


public final class ProgressSaver implements LevelCompletedListener {
    private final ProgressRepository repository;
    private final int levelCount;
    private Progress progress;

    public ProgressSaver(ProgressRepository repository, int levelCount) {
        if (levelCount < 1) {
            throw new IllegalArgumentException("The game needs at least one level, got " + levelCount);
        }
        this.repository = Objects.requireNonNull(repository, "repository");
        this.levelCount = levelCount;
        this.progress = repository.load();
    }

    @Override
    public void levelCompleted(int levelNumber, LevelStats stats) {
        LevelRecord won = new LevelRecord(levelNumber, stats.getMoves(), stats.getPushes());
        progress = progress.withLevelWon(won, levelCount);
        repository.save(progress);
    }
}