package service.sound;

public enum SoundEffect {
    STEP("step.wav"),
    PUSH_BOX("push_box.wav"),
    BOX_ON_TARGET("box_on_target.wav"),
    DOOR_OPEN("door_open.wav"),
    UNDO("undo.wav"),
    LEVEL_WON("level_won.wav"),
    LEVEL_LOST("level_lost.wav"),
    MAIN_THEME_ONE("main_theme1.wav"),
    MAIN_THEME_TWO("main_theme2.wav");

    private static final String AUDIO_FOLDER = "/assets/audio/";

    private final String fileName;

    SoundEffect(String fileName) {
        this.fileName = fileName;
    }

    public String resourcePath() {
        return AUDIO_FOLDER + fileName;
    }
}
