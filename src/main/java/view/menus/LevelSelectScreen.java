package view.menus;

import view.menus.LevelSelectView.LevelInfo;

import java.util.List;

/** Passive view of the level select: it shows what it is given and reports the reset request. */
public interface LevelSelectScreen {

    /** One entry per level, in order: name, best stars of this session and whether it can be played. */
    void showLevels(List<LevelInfo> levels);

    /** Runs only after the player confirmed the reset in the view's own popup. */
    void setOnResetProgress(Runnable onReset);
}
