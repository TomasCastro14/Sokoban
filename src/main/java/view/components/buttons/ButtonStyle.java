package view.components.buttons;

public enum ButtonStyle {
    PRIMARY("button-primary"),
    SECONDARY("button-secondary"),
    GOLD("button-gold");

    private final String cssClass;
    ButtonStyle(String cssClass) { this.cssClass = cssClass; }
    String cssClass() { return cssClass; }
}
