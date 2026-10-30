package view.gameplay;

import model.game.Level;

public interface GameScreen {
    void showLevel(Level level);
    void playerMoved();
    void boardChanged();
    void setLifecycleListener(ScreenLifecycleListener listener);
}
