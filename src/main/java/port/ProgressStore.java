package port;

import model.game.progress.ProgressData;

import java.util.Optional;

/**
 * Protected Variations: where the progress is kept between runs (a local file, nowhere, ...) is hidden here.
 * Implementations must never break the game: if something goes wrong they report it and carry on.
 */
public interface ProgressStore {

    /** The saved progress, or empty if there is none (first run) or it could not be read. */
    Optional<ProgressData> load();

    void save(ProgressData data);
}
