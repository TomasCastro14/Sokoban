package controller;

import model.game.Game;
import model.game.LevelStats;
import model.game.event.GameCompletionListener;
import model.game.event.LevelOutcomeListener;
import model.game.loss.LossReason;
import view.Navigator;
import view.Screen;

import java.util.Objects;
import java.util.concurrent.Executor;

public final class LevelProgression implements LevelOutcomeListener, GameCompletionListener {
    private final Game game;
    private final Navigator navigator;
    private final Executor afterCurrentEvent;

    public LevelProgression(Game game, Navigator navigator, Executor afterCurrentEvent) {
        this.game = Objects.requireNonNull(game, "game");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.afterCurrentEvent = Objects.requireNonNull(afterCurrentEvent, "afterCurrentEvent");
        game.addLevelOutcomeListener(this);
        game.addCompletionListener(this);
    }

    @Override
    public void levelWon(LevelStats stats) {
        afterCurrentEvent.execute(game::nextLevel);
    }

    @Override
    public void levelLost(LossReason reason) {
    }

    @Override
    public void gameCompleted() {
        navigator.show(Screen.MAIN_MENU);
    }
}