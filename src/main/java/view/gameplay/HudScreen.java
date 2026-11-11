package view.gameplay;

/** Passive HUD: shows what it is told and reports the button presses to its listener. */
public interface HudScreen {
    void showLevelName(String name);

    void showStats(int elapsedSeconds, int pushes, int moves);

    void setUndoEnabled(boolean enabled);

    void setListener(HudListener listener);
}