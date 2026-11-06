package controller;

import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.game.Game;

/** Turns real time into game ticks: one tick per second while the clock runs. */
public class GameClock {
    private static final Duration TICK = Duration.seconds(1);

    private final Game game;
    private Timeline timeline;

    public GameClock(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    /** Starts the clock, or resumes it after pause(). */
    public void start() {
        if (timeline == null) { // created on first use, so the clock can be built without the JavaFX toolkit
            timeline = new Timeline(new KeyFrame(TICK, event -> onSecond()));
            timeline.setCycleCount(Animation.INDEFINITE);
        }
        timeline.play();
    }

    /** Stops the ticks but keeps the position: start() resumes from here. */
    public void pause() {
        if (timeline != null) {
            timeline.pause();
        }
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    void onSecond() {
        game.tick();
    }
}