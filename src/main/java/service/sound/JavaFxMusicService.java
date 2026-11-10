package service.sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Objects;

/**
 * Streams the playlist with {@link MediaPlayer} (not AudioClip: themes are too big to keep in memory)
 * and chains the next track when the current one ends.
 */
public final class JavaFxMusicService extends AbstractAudioControl implements MusicService {
    private static final Logger LOG = System.getLogger(JavaFxMusicService.class.getName());

    private final Playlist playlist;
    private MediaPlayer current;
    private int consecutiveFailures;

    public JavaFxMusicService(Playlist playlist) {
        this.playlist = Objects.requireNonNull(playlist, "playlist");
    }

    @Override
    public void start() {
        if (current == null) {
            playNext();
        }
    }

    @Override
    protected void settingsChanged() {
        if (current != null) {
            applySettings(current);
        }
    }

    private void playNext() {
        disposeCurrent();
        if (consecutiveFailures >= playlist.size()) {
            LOG.log(Level.WARNING, "No music track could be played, background music disabled");
            return;
        }
        MusicTrack track = playlist.next();
        URL resource = JavaFxMusicService.class.getResource(track.resourcePath());
        if (resource == null) {
            LOG.log(Level.WARNING, "Missing music file for {0}: {1}", track, track.resourcePath());
            skip();
            return;
        }
        try {
            current = createPlayer(resource, track);
            current.play();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Could not load music " + track, e);
            skip();
        }
    }

    private MediaPlayer createPlayer(URL resource, MusicTrack track) {
        MediaPlayer player = new MediaPlayer(new Media(resource.toExternalForm()));
        applySettings(player);
        player.setOnPlaying(() -> consecutiveFailures = 0);
        player.setOnEndOfMedia(this::playNext);
        player.setOnError(() -> {
            LOG.log(Level.WARNING, "Could not play music " + track, player.getError());
            skip();
        });
        return player;
    }

    private void skip() {
        consecutiveFailures++;
        playNext();
    }

    private void applySettings(MediaPlayer player) {
        player.setVolume(volume());
        player.setMute(isMuted());
    }

    private void disposeCurrent() {
        if (current != null) {
            current.dispose();
            current = null;
        }
    }
}