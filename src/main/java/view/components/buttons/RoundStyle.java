package view.components.buttons;

/** Color of a round button: each one is a pair of images (resting and pressed) in buttons.css. */
public enum RoundStyle {
    BLUE("round-blue"),
    GOLD("round-gold"),
    GREY("round-grey"),
    RED("round-red");

    private final String cssClass;

    RoundStyle(String cssClass) {
        this.cssClass = cssClass;
    }

    String cssClass() {
        return cssClass;
    }
}
