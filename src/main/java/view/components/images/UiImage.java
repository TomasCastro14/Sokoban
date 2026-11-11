package view.components.images;

public enum UiImage implements ImageAsset {
    STAR_ON("Items/star.png"),
    STAR_OFF("Items/star_outline.png"),
    BIG_STAR_ON("Items/big_star/star.png"),
    BIG_STAR_OFF("Items/big_star/star_outline.png"),
    GOAL_MET("Items/objetives/check_square_grey_checkmark.png"),
    GOAL_MISSED("Items/objetives/check_square_grey_cross.png"),
    MASCOT("Items/sokoban/player_03.png"),
    EMOTE_HAPPY("Items/emotes/emote_faceHappy.png"),
    EMOTE_SAD("Items/emotes/emote_faceSad.png"),
    EMOTE_HEART("Items/emotes/emote_heart.png"),
    EMOTE_EXCLAMATION("Items/emotes/emote_exclamation.png"),
    ICON_ARROW("buttons/icons/arrow_decorative_e.png"),
    ICON_PLAY("buttons/icons/icon_play_outline.png"),
    ICON_REPLAY("buttons/icons/icon_repeat_outline.png"),
    ICON_CLOSE("buttons/icons/icon_outline_cross.png"),
    SPLASH_LOGO("logos/splash_screen.png"),
    TITLE_TOP("logos/main_title_a.png"),
    TITLE_BOTTOM("logos/main_title_b.png");

    static final String ROOT = "/assets/images/UI/";

    private final String path;

    UiImage(String file) {
        this.path = ROOT + file;
    }

    @Override
    public String path() {
        return path;
    }
}
