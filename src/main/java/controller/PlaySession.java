package controller;

import javafx.scene.Scene;

import java.util.Objects;

/**
 * Whether the player is playing right now: arrow keys bound and clock running.
 * It keeps two facts apart, so their order never matters: whether the game screen is
 * attached to the window, and whether play was let go (resumed) or held by a popup.
 */
public final class PlaySession {
    private static final Runnable NOTHING_BOUND = () -> { };

    private final InputController input;
    private final GameClock clock;
    private Scene scene;
    private boolean active;
    private Runnable unbindKeys = NOTHING_BOUND;

    public PlaySession(InputController input, GameClock clock) {
        this.input = Objects.requireNonNull(input, "input");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /** The game screen is now in the window: if play was going on, it goes on. */
    public void attach(Scene scene) {
        this.scene = Objects.requireNonNull(scene, "scene");
        if (active) {
            run();
        }
    }

    /** The game screen left the window: keys off and clock paused, but play is still "on". */
    public void detach() {
        freeze();
        scene = null;
    }

    public void resume() {
        active = true;
        if (scene != null) {
            run();
        }
    }

    /** A popup is open: nothing reaches the game and time stands still until resume(). */
    public void hold() {
        active = false;
        freeze();
    }

    /** The level is over: like hold(), but the clock starts from zero next time. */
    public void stop() {
        hold();
        clock.stop();
    }

    public boolean isActive() {
        return active;
    }

    private void run() {
        if (unbindKeys == NOTHING_BOUND) {
            unbindKeys = input.bindKeys(scene);
        }
        clock.start();
    }

    private void freeze() {
        unbindKeys.run();
        unbindKeys = NOTHING_BOUND;
        clock.pause();
    }
}
