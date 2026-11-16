package service.sound;

public class AudioAssets {
    public static AudioAssets INSTANCE;
    private static final String AUDIO_FOLDER = "/assets/audio/";

    private AudioAssets() {
    }

    public static AudioAssets getInstance() {
        if(INSTANCE == null) { AudioAssets INSTANCE = new AudioAssets(); }
        return INSTANCE;
    }

    static String pathOf(String fileName) {
        return AUDIO_FOLDER + fileName;
    }
}