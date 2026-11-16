package service.sound;

/** Background tracks. Adding a new theme is just adding a constant here and its file. */
public enum MusicTrack {
    MAIN_THEME_1("main_theme1.wav"),
    MAIN_THEME_2("main_theme2.wav");

    private final String fileName;

    MusicTrack(String fileName) {
        this.fileName = fileName;
    }

    public String resourcePath() {
        return AudioAssets.pathOf(fileName);
    }
}