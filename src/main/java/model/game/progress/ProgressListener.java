package model.game.progress;

/** Observer: told every time the progress changes (a level won or a reset). */
public interface ProgressListener {

    void progressChanged(Progress progress);
}
