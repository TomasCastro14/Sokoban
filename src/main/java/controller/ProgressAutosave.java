package controller;

import model.game.progress.Progress;
import model.game.progress.ProgressListener;
import port.ProgressStore;

import java.util.Objects;

/**
 * Observer: every time the progress changes (a level won or a reset) it is saved,
 * so closing the game at any moment never loses what was achieved.
 */
public final class ProgressAutosave implements ProgressListener {
    private final ProgressStore store;

    public ProgressAutosave(ProgressStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    /** Loads the saved progress (if any) into {@code progress} and keeps saving it from then on. */
    public static void attach(Progress progress, ProgressStore store) {
        store.load().ifPresent(progress::restore);
        progress.addListener(new ProgressAutosave(store));
    }

    @Override
    public void progressChanged(Progress progress) {
        store.save(progress.snapshot());
    }
}
