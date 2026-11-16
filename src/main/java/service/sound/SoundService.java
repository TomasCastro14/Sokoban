package service.sound;

public interface SoundService extends AudioControl {

    void play(SoundEffect effect);
}