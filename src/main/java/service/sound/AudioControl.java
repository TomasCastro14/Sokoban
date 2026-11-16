package service.sound;

public interface AudioControl {

    void setVolume(double level);

    double volume();

    void setMuted(boolean muted);

    boolean isMuted();
}