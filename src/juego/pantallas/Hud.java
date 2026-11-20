package juego.pantallas;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import juego.entidades.Jugador;
import juego.modelo.Hecho;
import juego.modelo.Rasgo;
import juego.reglas.Partida;
import ui.Texto;

public final class Hud {

    // Constantes: panel izquierdo
    public static final double X = 16;
    public static final double Y = 16;
    public static final double ANCHO = 268;
    private static final int PUBLICOS_VISIBLES = 6;

    // Sin instancias
    private Hud() {
    }

    // Encabezado de la partida y lo que sabe el humano
    public static void dibujar(Graphics2D g, Partida partida, double alto) {
        Jugador vos = partida.getMesa().getHumano();
        panel(g, X, Y, ANCHO, alto);

        double y = Y + 28;
        int numero = Math.min(partida.getAfirmacionesHechas() + 1, Partida.AFIRMACIONES_TOTALES);
        Texto.izquierda(g, "Afirmación " + numero + " de " + Partida.AFIRMACIONES_TOTALES, X + 14, y, 15,
                Paleta.TEXTO_SUAVE);
        y += 30;
        Texto.izquierda(g, "Sos " + vos.getRol().getNombre(), X + 14, y, 20, Paleta.ACENTO);
        y += 22;
        y = Parrafo.izquierda(g, vos.getRol().getObjetivo(), X + 14, y, 12, Paleta.TEXTO_SUAVE, ANCHO - 28);
        y += 8;

        Texto.izquierda(g, "Tu ficha", X + 14, y, 14, Paleta.TEXTO);
        y += 20;
        for (Rasgo rasgo : Rasgo.todos()) {
            Texto.izquierda(g, rasgo.getVerbo() + " " + vos.getFicha().valor(rasgo), X + 22, y, 13, Paleta.TEXTO_SUAVE);
            y += 18;
        }
        y += 8;

        Texto.izquierda(g, "Lo que sabés en secreto", X + 14, y, 14, Paleta.TEXTO);
        y += 20;
        for (int i = 0; i < vos.getPrivados().tamanio(); i++) {
            Hecho h = vos.getPrivados().obtener(i);
            y = Parrafo.izquierda(g, h.toString(), X + 22, y, 13, Paleta.TEXTO_SUAVE, ANCHO - 36);
        }
        y += 8;

        Texto.izquierda(g, "Lo que sabe toda la mesa", X + 14, y, 14, Paleta.TEXTO);
        y += 20;
        int publicos = partida.getMesa().getPublicos().tamanio();
        if (publicos == 0) {
            Texto.izquierda(g, "Todavía nada.", X + 22, y, 13, Paleta.TEXTO_TENUE);
        }
        for (int i = publicos - 1; i >= Math.max(0, publicos - PUBLICOS_VISIBLES); i--) {
            Hecho h = partida.getMesa().getPublicos().obtener(i);
            y = Parrafo.izquierda(g, h.toString(), X + 22, y, 13, Paleta.TEXTO_SUAVE, ANCHO - 36);
        }
    }

    // Un panel con borde
    public static void panel(Graphics2D g, double x, double y, double ancho, double alto) {
        RoundRectangle2D fondo = new RoundRectangle2D.Double(x, y, ancho, alto, 16, 16);
        g.setColor(Paleta.PANEL);
        g.fill(fondo);
        g.setColor(Paleta.PANEL_BORDE);
        g.setStroke(new BasicStroke(1f));
        g.draw(fondo);
    }

    // Pie con los controles
    public static void controles(Graphics2D g, String texto, double ancho, double alto) {
        Texto.centrado(g, texto, ancho / 2, alto - 14, 14, Paleta.TEXTO_SUAVE);
    }
}
