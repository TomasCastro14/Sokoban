package service.sound;

public abstract class AbstractAudioControl implements AudioControl {
    private Volume volume = Volume.DEFAULT;
    private boolean muted;

    @Override
    public final void setVolume(double level) {
        volume = new Volume(level);
        settingsChanged();
    }

    @Override
    public final double volume() {
        return volume.level();
    }

    @Override
    public final void setMuted(boolean muted) {
        this.muted = muted;
        settingsChanged();
    }

    @Override
    public final boolean isMuted() {
        return muted;
    }

    /** Hook for channels that must push the new settings to something already playing. */
    protected void settingsChanged() {
    }
}