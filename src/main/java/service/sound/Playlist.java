package service.sound;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Endless, ordered rotation over a fixed list of tracks. Only the starting point may be random;
 * after that it always advances in order, so no track repeats until all the others have played.
 */
public final class Playlist {
    private final List<MusicTrack> tracks;
    private int next;

    private Playlist(List<MusicTrack> tracks, int start) {
        this.tracks = List.copyOf(Objects.requireNonNull(tracks, "tracks"));
        if (this.tracks.isEmpty()) {
            throw new IllegalArgumentException("A playlist needs at least one track");
        }
        if (start < 0 || start >= this.tracks.size()) {
            throw new IllegalArgumentException("Start index out of range: " + start);
        }
        this.next = start;
    }

    public static Playlist startingAt(List<MusicTrack> tracks, int start) {
        return new Playlist(tracks, start);
    }

    public static Playlist randomStart(List<MusicTrack> tracks, RandomGenerator random) {
        Objects.requireNonNull(random, "random");
        return new Playlist(tracks, random.nextInt(tracks.size()));
    }

    public MusicTrack next() {
        MusicTrack track = tracks.get(next);
        next = (next + 1) % tracks.size();
        return track;
    }

    public int size() {
        return tracks.size();
    }
}