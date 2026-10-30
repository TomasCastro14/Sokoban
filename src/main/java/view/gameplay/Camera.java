package view.gameplay;

/** Cámara 2D pura (sin JavaFX): decide qué porción del mundo se ve. Se testea sin ventana. */
public final class Camera {

    /** Coordenada del mundo (px) que queda en el borde izquierdo y superior de la vista. */
    public record Offset(double x, double y) { }

    private Camera() { }

    /**
     * Centra la vista en el foco. Si el mundo es más grande que la vista, la cámara se frena en
     * los bordes del mapa (no muestra vacío); si cabe entero, queda centrado y quieto.
     */
    public static Offset follow(double focusX, double focusY,
                                double viewW, double viewH,
                                double worldW, double worldH) {
        return new Offset(axis(focusX, viewW, worldW), axis(focusY, viewH, worldH));
    }

    private static double axis(double focus, double view, double world) {
        if (world <= view) {
            return (world - view) / 2;            // negativo: márgenes iguales a cada lado
        }
        return Math.clamp(focus - view / 2, 0, world - view);
    }
}
