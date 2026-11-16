package controller;

import model.game.progress.Progress;
import model.game.progress.ProgressListener;
import view.menus.LevelSelectScreen;
import view.menus.LevelSelectView.LevelInfo;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Keeps the level select in step with the progress: every time the progress changes
 * (a level won or a reset) the view gets the new list of locks and stars.
 * The reset is confirmed by the view; this presenter only carries it to the model.
 */
public final class LevelSelectPresenter implements ProgressListener {
    private static final String LEVEL_NAME = "Nivel ";

    private final Progress progress;
    private final LevelSelectScreen screen;

    public LevelSelectPresenter(Progress progress, LevelSelectScreen screen) {
        this.progress = Objects.requireNonNull(progress, "progress");
        this.screen = Objects.requireNonNull(screen, "screen");
        screen.setOnResetProgress(progress::reset);
        progress.addListener(this);
        progressChanged(progress);
    }

    @Override
    public void progressChanged(Progress progress) {
        screen.showLevels(levelInfos());
    }

    private List<LevelInfo> levelInfos() {
        return IntStream.rangeClosed(Progress.FIRST_LEVEL, progress.getLevelCount())
                .mapToObj(n -> new LevelInfo(LEVEL_NAME + n, progress.getStars(n), progress.isUnlocked(n)))
                .toList();
    }
}
