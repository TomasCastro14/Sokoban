package service.sound;

/** Background music channel: once started it keeps playing for the whole session. */
public interface MusicService extends AudioControl {

    void start();
}