package service.sound;

public interface SoundService {

    void play(SoundEffect effect);

    void setVolume(double level);

    double volume();

    void setMuted(boolean muted);

    boolean isMuted();
}
