package view.gameplay;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import view.components.buttons.ButtonIcon;
import view.components.buttons.ButtonProvider;
import view.components.buttons.ButtonSize;
import view.components.buttons.RoundStyle;

import java.util.Objects;

/**
 * Top strip of the game screen: a white bar that grows with its content (level name and the three
 * counters) and, on the right, the undo and pause buttons. The gaps grow with the screen width.
 */
public final class HudView implements HudScreen {
    private static final String COUNTER_FORMAT = "%04d";      // siempre 4 dígitos como mínimo, sin tope
    private static final double GAP_RATIO = 0.03;             // separación = 3% del ancho de pantalla
    private static final double MIN_GAP = 16;
    private static final double MAX_GAP = 72;
    private static final double LABEL_GAP = 8;
    private static final double BUTTON_GAP = 16;
    private static final HudListener NO_LISTENER = new HudListener() {
        @Override
        public void onUndoPressed() {
        }

        @Override
        public void onPausePressed() {
        }
    };

    private final HBox root = new HBox();
    private final Label levelName = new Label();
    private final Label time = counter();
    private final Label pushes = counter();
    private final Label moves = counter();
    private final Button undo;
    private HudListener listener = NO_LISTENER;

    public HudView(ButtonProvider buttons) {
        Objects.requireNonNull(buttons, "buttons");
        undo = buttons.createRound(RoundStyle.BLUE, ButtonIcon.UNDO, ButtonSize.REGULAR,
                () -> listener.onUndoPressed());
        Button pause = buttons.createRound(RoundStyle.GOLD, ButtonIcon.PLAY, ButtonSize.REGULAR,
                () -> listener.onPausePressed());
        undo.setDisable(true);

        HBox bar = bar();
        HBox actions = new HBox(BUTTON_GAP, undo, pause);
        actions.setAlignment(Pos.CENTER);
        actions.setMinWidth(Region.USE_PREF_SIZE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.minWidthProperty().bind(bar.spacingProperty());

        root.getChildren().addAll(bar, spacer, actions);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setMaxHeight(Region.USE_PREF_SIZE);
        root.setPickOnBounds(false);                          // los clics fuera de la barra y los botones pasan de largo
        root.getStyleClass().add("hud");
        showStats(0, 0, 0);
    }

    public Node getRoot() {
        return root;
    }

    @Override
    public void showLevelName(String name) {
        levelName.setText(name);
    }

    @Override
    public void showStats(int elapsedSeconds, int pushCount, int moveCount) {
        time.setText(format(elapsedSeconds));
        pushes.setText(format(pushCount));
        moves.setText(format(moveCount));
    }

    @Override
    public void setUndoEnabled(boolean enabled) {
        undo.setDisable(!enabled);
    }

    @Override
    public void setListener(HudListener listener) {
        this.listener = Objects.requireNonNull(listener, "listener");
    }

    /** Never wider than its content; if the screen gets too narrow, only the level name is cut ("..."). */
    private HBox bar() {
        levelName.getStyleClass().add("hud-level-name");
        levelName.setTextOverrun(OverrunStyle.ELLIPSIS);
        StackPane nameTag = new StackPane(levelName);
        nameTag.getStyleClass().add("hud-name-tag");
        nameTag.setMinWidth(0);
        HBox.setHgrow(nameTag, Priority.NEVER);

        HBox bar = new HBox(nameTag,
                stat("Tiempo:", time), stat("Empujes:", pushes), stat("Movimientos:", moves));
        bar.getStyleClass().add("hud-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(Region.USE_PREF_SIZE);
        bar.spacingProperty().bind(Bindings.createDoubleBinding(
                () -> Math.clamp(root.getWidth() * GAP_RATIO, MIN_GAP, MAX_GAP), root.widthProperty()));
        return bar;
    }

    private static HBox stat(String title, Label value) {
        Label name = new Label(title);
        name.getStyleClass().add("hud-stat");
        HBox stat = new HBox(LABEL_GAP, name, value);
        stat.setAlignment(Pos.CENTER_LEFT);
        stat.setMinWidth(Region.USE_PREF_SIZE);
        return stat;
    }

    /** The "1" is thinner than the other digits: a minimum width (gameplay.css) stops the bar from wobbling every second. */
    private static Label counter() {
        Label label = new Label();
        label.getStyleClass().addAll("hud-stat", "hud-counter");
        return label;
    }

    private static String format(int value) {
        return String.format(COUNTER_FORMAT, value);
    }
}