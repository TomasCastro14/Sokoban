package view.gameplay;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.board.Board;
import model.board.Direction;
import model.board.Position;
import model.board.Visibility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Nube plana (estilo caricatura) sobre los casilleros que el jugador todavía no vio. El borde son círculos
 * que asoman sobre lo visible, y al abrir una puerta cada casillero revelado se achica como una bolita
 * hasta desaparecer. Las paredes no llevan nube: BoardView las dibuja encima.
 */
final class FogLayer {

    private static final Color CLOUD = Color.rgb(214, 222, 232);
    private static final Color CLOUD_SHADE = Color.rgb(184, 196, 210);

    // Borde ondulado (medidas en casilleros)
    private static final int PUFFS_PER_EDGE = 2;
    private static final double MIN_PUFF = 0.25;           // radio de los círculos del borde
    private static final double MAX_PUFF = 0.34;
    private static final double PUFF_INSET = 0.08;         // cuánto se mete el centro del círculo hacia la nube
    private static final double PUFF_JITTER = 0.4;         // cuánto se corre cada círculo a lo largo del lado

    // Nubecitas oscuras adentro
    private static final double SHADE_CHANCE = 0.25;       // fracción de casilleros que llevan una
    private static final double SHADE_SIZE = 0.22;

    // Disipación al revelarse
    private static final long FADE_NANOS = 650_000_000L;   // lo que tarda cada casillero en desaparecer
    private static final double MAX_DELAY = 0.35;          // retraso máximo, en fracción de FADE_NANOS
    private static final long TOTAL_NANOS = (long) (FADE_NANOS * (1 + MAX_DELAY));
    private static final double FADE_START_RADIUS = 0.72;  // > media diagonal (0.707): al empezar tapa todo

    // "Sales" para sacar números distintos de la misma posición
    private static final int SALT_PUFF_SHIFT = 0;
    private static final int SALT_PUFF_SIZE = 20;
    private static final int SALT_SHADE = 40;
    private static final int SALT_DELAY = 50;

    private final double tile;
    private final Set<Position> covered = new HashSet<>();
    private final Map<Position, Long> fading = new HashMap<>();

    FogLayer(double tile) {
        this.tile = tile;
    }

    /** Nivel nuevo o restaurado: la nube queda como está, sin animar. */
    void reset(Board board, Visibility visibility) {
        fading.clear();
        covered.clear();
        covered.addAll(coveredCells(board, visibility));
    }

    /** Compara con lo que estaba tapado antes y empieza a disipar lo que se acaba de revelar. */
    void update(Board board, Visibility visibility, long now) {
        fading.values().removeIf(start -> now - start >= TOTAL_NANOS);
        Set<Position> nowCovered = coveredCells(board, visibility);
        for (Position p : covered) {
            if (!nowCovered.contains(p)) {
                fading.put(p, now);
            }
        }
        covered.clear();
        covered.addAll(nowCovered);
    }

    boolean isFading(long now) {
        return fading.values().stream().anyMatch(start -> now - start < TOTAL_NANOS);
    }

    void draw(GraphicsContext g, TileRange range, double offX, double offY, long now) {
        range.forEach(p -> {
            double x = p.col() * tile - offX;
            double y = p.row() * tile - offY;
            if (covered.contains(p)) {
                drawCovered(g, p, x, y);
            } else if (fading.containsKey(p)) {
                drawFading(g, p, x, y, now);
            }
        });
    }

    private void drawCovered(GraphicsContext g, Position p, double x, double y) {
        g.setFill(CLOUD);
        g.fillRect(x, y, tile, tile);
        for (Direction d : Direction.values()) {
            if (!covered.contains(p.move(d))) {
                drawEdgePuffs(g, p, d, x, y);
            }
        }
        if (random(p, SALT_SHADE) < SHADE_CHANCE) {
            drawShade(g, p, x, y);
        }
    }

    /** Círculos sobre el lado del casillero que da a algo descubierto: el borde ondulado de la nube. */
    private void drawEdgePuffs(GraphicsContext g, Position p, Direction d, double x, double y) {
        double half = tile / 2;
        double toEdge = half - tile * PUFF_INSET;          // del centro del casillero al centro del círculo
        for (int i = 0; i < PUFFS_PER_EDGE; i++) {
            int salt = d.ordinal() * PUFFS_PER_EDGE + i;
            double along = ((i + 0.5 + (random(p, SALT_PUFF_SHIFT + salt) - 0.5) * PUFF_JITTER) / PUFFS_PER_EDGE - 0.5) * tile;
            double radius = tile * lerp(MIN_PUFF, MAX_PUFF, random(p, SALT_PUFF_SIZE + salt));
            // a lo largo del lado (eje sin desplazamiento) + hacia el lado (eje de la dirección)
            double cx = x + half + (d.getColOffset() == 0 ? along : d.getColOffset() * toEdge);
            double cy = y + half + (d.getRowOffset() == 0 ? along : d.getRowOffset() * toEdge);
            fillCircle(g, cx, cy, radius);
        }
    }

    private void drawShade(GraphicsContext g, Position p, double x, double y) {
        double cx = x + tile * lerp(0.3, 0.7, random(p, SALT_SHADE + 1));
        double cy = y + tile * lerp(0.35, 0.65, random(p, SALT_SHADE + 2));
        double s = tile * SHADE_SIZE;
        g.setFill(CLOUD_SHADE);
        g.fillOval(cx - s, cy - s * 0.45, s * 2, s * 0.9);              // base aplastada
        g.fillOval(cx - s * 0.45, cy - s * 0.8, s * 1.2, s * 1.1);      // copete
    }

    /** El casillero revelado se convierte en una bolita de nube que se achica hasta desaparecer. */
    private void drawFading(GraphicsContext g, Position p, double x, double y, long now) {
        double delay = random(p, SALT_DELAY) * MAX_DELAY * FADE_NANOS;  // delay + FADE_NANOS <= TOTAL_NANOS
        double t = Math.clamp((now - fading.get(p) - delay) / FADE_NANOS, 0, 1);
        double radius = tile * FADE_START_RADIUS * (1 - t * t);         // arranca lento y termina rápido: "pop"
        if (radius > 0) {
            g.setFill(CLOUD);
            fillCircle(g, x + tile / 2, y + tile / 2, radius);
        }
    }

    private static void fillCircle(GraphicsContext g, double cx, double cy, double radius) {
        g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private static double lerp(double from, double to, double t) {
        return from + (to - from) * t;
    }

    /** Número "al azar" en [0, 1) pero siempre el mismo para la misma posición: la nube no cambia al redibujar. */
    private static double random(Position p, int salt) {
        long h = (p.row() * 73_856_093L ^ p.col() * 19_349_663L) + salt * 0x9E3779B97F4A7C15L;
        h = (h ^ (h >>> 33)) * 0xFF51AFD7ED558CCDL;
        h = (h ^ (h >>> 33)) * 0xC4CEB9FE1A85EC53L;
        h ^= h >>> 33;
        return (h >>> 11) / (double) (1L << 53);
    }

    private static Set<Position> coveredCells(Board board, Visibility visibility) {
        Set<Position> result = new HashSet<>();
        new TileRange(0, board.getRows() - 1, 0, board.getCols() - 1).forEach(p -> {
            if (CellSprites.appearanceOf(board.cellAt(p), visibility) == CellSprites.Appearance.HIDDEN) {
                result.add(p);
            }
        });
        return result;
    }
}
