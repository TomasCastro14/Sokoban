package view.menus;

public interface SettingsListener {

    void volumeChanged(double level);

    void muteToggled(boolean muted);

    double currentVolume();

    boolean isMuted();
}
