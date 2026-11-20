package juego.pantallas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import juego.entidades.Personaje;
import juego.modelo.Afirmacion;
import juego.modelo.Hecho;
import juego.modelo.Resultado;
import juego.reglas.Juicio;
import juego.reglas.Mesa;
import juego.reglas.Partida;
import ui.Texto;

public class VistaMesa {

    // Constantes de la mesa
    public static final double CENTRO_X = 600;
    public static final double CENTRO_Y = 372;
    private static final double RADIO_X_TAPETE = 228;
    private static final double RADIO_Y_TAPETE = 128;
    private static final double RADIO_X_ASIENTOS = 292;
    private static final double RADIO_Y_ASIENTOS = 188;
    private static final double RADIO_ASIENTO = 32;

    // Posición de cada asiento: el humano abajo y el resto en sentido horario
    public static double asientoX(int asiento) {
        return CENTRO_X + RADIO_X_ASIENTOS * Math.cos(angulo(asiento));
    }

    public static double asientoY(int asiento) {
        return CENTRO_Y + RADIO_Y_ASIENTOS * Math.sin(angulo(asiento));
    }

    private static double angulo(int asiento) {
        return Math.toRadians(90 + 72 * asiento);
    }

    // Asiento bajo el mouse, o -1
    public static int asientoEn(double x, double y) {
        for (int i = 0; i < 5; i++) {
            double dx = x - asientoX(i);
            double dy = y - asientoY(i);
            if (dx * dx + dy * dy <= (RADIO_ASIENTO + 8) * (RADIO_ASIENTO + 8)) {
                return i;
            }
        }
        return -1;
    }

    // Dibujo completo de la mesa
    public static void dibujar(Graphics2D g, Partida partida, Personaje seleccionado) {
        tapete(g);
        Mesa mesa = partida.getMesa();
        for (int i = 0; i < mesa.cantidad(); i++) {
            asiento(g, partida, mesa.buscar(i), i, mesa.buscar(i) == seleccionado);
        }
        centro(g, partida);
        for (int i = 0; i < mesa.cantidad(); i++) {
            palabra(g, partida, mesa.buscar(i), i);
        }
    }

    // El tapete
    private static void tapete(Graphics2D g) {
        Ellipse2D borde = new Ellipse2D.Double(CENTRO_X - RADIO_X_TAPETE - 12, CENTRO_Y - RADIO_Y_TAPETE - 12,
                2 * (RADIO_X_TAPETE + 12), 2 * (RADIO_Y_TAPETE + 12));
        g.setColor(Paleta.TAPETE_BORDE);
        g.fill(borde);
        g.setColor(Paleta.TAPETE);
        g.fill(new Ellipse2D.Double(CENTRO_X - RADIO_X_TAPETE, CENTRO_Y - RADIO_Y_TAPETE, 2 * RADIO_X_TAPETE,
                2 * RADIO_Y_TAPETE));
    }

    // Un personaje en su asiento
    private static void asiento(Graphics2D g, Partida partida, Personaje p, int asiento, boolean seleccionado) {
        double x = asientoX(asiento);
        double y = asientoY(asiento);
        double r = RADIO_ASIENTO;
        boolean enJuego = p.estaEnJuego();
        Color color = enJuego ? Paleta.dePersonaje(asiento) : Paleta.TEXTO_TENUE;

        if (p == partida.getHablante() && !partida.estaTerminada()) {
            g.setColor(Paleta.ACENTO);
            g.setStroke(new BasicStroke(4f));
            g.draw(new Ellipse2D.Double(x - r - 7, y - r - 7, 2 * r + 14, 2 * r + 14));
        }
        if (seleccionado) {
            g.setColor(Paleta.TEXTO);
            g.setStroke(new BasicStroke(2f));
            g.draw(new Ellipse2D.Double(x - r - 3, y - r - 3, 2 * r + 6, 2 * r + 6));
        }
        g.setColor(color);
        g.fill(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        Texto.centrado(g, p.getNombre().substring(0, 1), x, y + 9, 26, Paleta.FONDO);
        Texto.centrado(g, (asiento + 1) + "  " + nombreVisible(partida, p), x, y + r + 20, 15,
                enJuego ? Paleta.TEXTO : Paleta.TEXTO_TENUE);

        double corazones = x - (Personaje.VIDAS_INICIALES - 1) * 7;
        for (int v = 0; v < Personaje.VIDAS_INICIALES; v++) {
            g.setColor(v < p.getVidas() ? Paleta.VIDA : Paleta.NODO);
            g.fill(new Ellipse2D.Double(corazones + v * 14 - 4.5, y + r + 28, 9, 9));
        }
        Texto.centrado(g, p.getPuntaje() + " pts", x, y + r + 56, 13, Paleta.TEXTO_SUAVE);
    }

    private static String nombreVisible(Partida partida, Personaje p) {
        return p == partida.getMesa().getHumano() ? p.getNombre() + " (vos)" : p.getNombre();
    }

    // Lo que pasa en el centro: la afirmación y la revelación
    private static void centro(Graphics2D g, Partida partida) {
        Afirmacion a = partida.getAfirmacion();
        Personaje hablante = partida.getHablante();
        if (partida.getFase() == Partida.Fase.HABLANDO && hablante != null) {
            Texto.centrado(g, hablante.getNombre() + " está por decir algo…", CENTRO_X, CENTRO_Y, 18,
                    Paleta.TEXTO_SUAVE);
            return;
        }
        if (a == null) {
            return;
        }
        RoundRectangle2D globo = new RoundRectangle2D.Double(CENTRO_X - 200, CENTRO_Y - 50, 400, 84, 22, 22);
        g.setColor(Paleta.GLOBO);
        g.fill(globo);
        Texto.centrado(g, a.getHablante() + " dice:", CENTRO_X, CENTRO_Y - 24, 14, Paleta.TEXTO_TENUE);
        Parrafo.centrado(g, "«" + a + "»", CENTRO_X, CENTRO_Y + 6, 19, Paleta.GLOBO_TEXTO, 380, true);
        if (a.tieneGesto()) {
            Texto.centrado(g, "(mira para otro lado al decirlo)", CENTRO_X, CENTRO_Y + 54, 14, Paleta.ACENTO);
        }

        Resultado r = partida.getResultado();
        if (r == null) {
            return;
        }
        Hecho revelado = partida.getRevelado();
        String desenlace;
        if (revelado == null) {
            desenlace = "Nadie acusó: no se revela nada.";
        } else {
            desenlace = "Se revela: " + revelado + (r.eraVerdad() ? ". ¡Era verdad!" : ". ¡Era mentira!");
        }
        Parrafo.centrado(g, desenlace, CENTRO_X, CENTRO_Y + 82, 16, Paleta.TEXTO, 440, true);
    }

    // Lo que dijo cada juez, en un globo sobre su asiento
    private static void palabra(Graphics2D g, Partida partida, Personaje p, int asiento) {
        Juicio juicio = partida.juicioVisibleDe(p);
        if (juicio == null) {
            return;
        }
        String texto = juicio.getPalabra().getDicho();
        double x = asientoX(asiento);
        double y = asientoY(asiento) - RADIO_ASIENTO - 26;
        double ancho = texto.length() * 9 + 24;
        g.setColor(juicio.getPalabra().acusa() ? Paleta.ACENTO : Paleta.GLOBO);
        g.fill(new RoundRectangle2D.Double(x - ancho / 2, y - 16, ancho, 28, 14, 14));
        Texto.centrado(g, texto, x, y + 4, 15, Paleta.GLOBO_TEXTO);
    }
}
