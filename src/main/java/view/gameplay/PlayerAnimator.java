package view.gameplay;

import model.board.Direction;
import model.board.Position;
import view.SpriteKey;

/**
 * Estado visual de Sokoban: dónde se lo ve y con qué sprite. El modelo mueve al jugador de golpe;
 * esta clase lo desliza de un casillero al otro y alterna las piernas. Es solo cosmética: no frena
 * ni retrasa al modelo. Recibe el tiempo por parámetro (nanosegundos), así que se testea sin reloj.
 * La dirección a la que mira sale del propio desplazamiento: no depende de ningún dato del modelo.
 */
public final class PlayerAnimator {
    static final long TILE_NANOS = 110_000_000L;        // lo que tarda en cruzar un casillero
    private static final long MAX_NANOS = 330_000_000L; // tope (deslizamientos largos sobre hielo)
    private static final double EPS = 1e-9;

    private double fromRow, fromCol, toRow, toCol;
    private long startNanos;
    private long durationNanos;
    private double tiles;
    private boolean legA = true;
    private boolean placed;
    private Direction facing = Direction.DOWN;

    /** Coloca al jugador sin animar (nivel nuevo, undo, restart). */
    public void snapTo(Position position, Direction facing) {
        fromRow = toRow = position.row();
        fromCol = toCol = position.col();
        durationNanos = 0;
        this.facing = facing;
        placed = true;
    }

    /** Desliza desde donde se lo ve ahora hasta el casillero nuevo, mirando hacia donde va. */
    public void moveTo(Position target, long now) {
        if (!placed) {
            snapTo(target, facing);
            return;
        }
        double row = row(now);
        double col = col(now);
        boolean sameRow = Math.abs(row - target.row()) < EPS;
        boolean sameCol = Math.abs(col - target.col()) < EPS;
        if (!sameRow && !sameCol) {            // no es una línea recta (portal): aparece directamente
            snapTo(target, facing);
            return;
        }
        double distance = Math.abs(target.row() - row) + Math.abs(target.col() - col);
        if (distance < EPS) {
            return;
        }
        facing = sameCol
                ? (target.row() > row ? Direction.DOWN : Direction.UP)
                : (target.col() > col ? Direction.RIGHT : Direction.LEFT);
        fromRow = row;
        fromCol = col;
        toRow = target.row();
        toCol = target.col();
        startNanos = now;
        tiles = Math.ceil(distance);
        durationNanos = Math.min(MAX_NANOS, (long) tiles * TILE_NANOS);
        legA = !legA;                           // cada movimiento cambia de pierna
    }

    public double row(long now) {
        return fromRow + (toRow - fromRow) * progress(now);
    }

    public double col(long now) {
        return fromCol + (toCol - fromCol) * progress(now);
    }

    public boolean isMoving(long now) {
        return durationNanos > 0 && progress(now) < 1.0;
    }

    public SpriteKey sprite(long now) {
        if (!isMoving(now)) {
            return sprite(facing, Pose.IDLE);
        }
        int tileIndex = (int) (progress(now) * tiles);
        boolean poseA = (tileIndex % 2 == 0) == legA;
        return sprite(facing, poseA ? Pose.STEP_A : Pose.STEP_B);
    }

    private double progress(long now) {
        if (durationNanos == 0) {
            return 1.0;
        }
        return Math.clamp((now - startNanos) / (double) durationNanos, 0.0, 1.0);
    }

    private enum Pose { IDLE, STEP_A, STEP_B }

    private static SpriteKey sprite(Direction direction, Pose pose) {
        return switch (direction) {
            case UP -> switch (pose) {
                case IDLE -> SpriteKey.PLAYER_UP;
                case STEP_A -> SpriteKey.PLAYER_UP_A;
                case STEP_B -> SpriteKey.PLAYER_UP_B;
            };
            case RIGHT -> switch (pose) {
                case IDLE -> SpriteKey.PLAYER_RIGHT;
                case STEP_A -> SpriteKey.PLAYER_RIGHT_A;
                case STEP_B -> SpriteKey.PLAYER_RIGHT_B;
            };
            case LEFT -> switch (pose) {
                case IDLE -> SpriteKey.PLAYER_LEFT;
                case STEP_A -> SpriteKey.PLAYER_LEFT_A;
                case STEP_B -> SpriteKey.PLAYER_LEFT_B;
            };
            case DOWN -> switch (pose) {
                case IDLE -> SpriteKey.PLAYER_DOWN;
                case STEP_A -> SpriteKey.PLAYER_DOWN_A;
                case STEP_B -> SpriteKey.PLAYER_DOWN_B;
            };
        };
    }
}
