package view.gameplay;

/** Cámara 2D pura (sin JavaFX): decide qué porción del mundo se ve. Se testea sin ventana. */
public final class Camera {

    /** Coordenada del mundo (px) que queda en el borde izquierdo y superior de la vista. */
    public record Offset(double x, double y) { }

    private Camera() { }

    /**
     * Centra la vista en el foco. Si el mundo es más grande que la vista, la cámara se frena en
     * los bordes del mapa (no muestra vacío); si cabe entero, queda centrado y quieto.
     * topInset es la franja de arriba tapada por el HUD: el mapa se centra en la pantalla entera,
     * y solo si así quedaría debajo de esa franja pasa a usar únicamente la parte libre de abajo.
     */
    public static Offset follow(double focusX, double focusY,
                                double viewW, double viewH,
                                double worldW, double worldH,
                                double topInset) {
        double y = axis(focusY, viewH, worldH);
        if (-y < topInset) {                       // el borde de arriba del mapa caería bajo el HUD
            y = axis(focusY, viewH - topInset, worldH) - topInset;
        }
        return new Offset(axis(focusX, viewW, worldW), y);
    }

    private static double axis(double focus, double view, double world) {
        if (world <= view) {
            return (world - view) / 2;            // negativo: márgenes iguales a cada lado
        }
        return Math.clamp(focus - view / 2, 0, world - view);
    }
}