package juego.pantallas;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import juego.entidades.Personaje;
import juego.modelo.Afirmacion;
import juego.reglas.Juicio;
import juego.reglas.Partida;
import ui.Texto;

public class MinimapaArbol {

    // Constantes: arriba a la derecha
    public static final double X = 870;
    public static final double Y = 16;
    public static final double ANCHO = 314;
    public static final double ALTO = 232;

    // Atributos
    private final VistaArbol vista;

    // Constructor
    public MinimapaArbol(VistaArbol vista) {
        if (vista == null) {
            throw new IllegalArgumentException("el minimapa necesita una vista del árbol");
        }
        this.vista = vista;
    }

    // Dibujo: el camino del seleccionado, sin decir si acertó
    public void dibujar(Graphics2D g, Partida partida, Personaje seleccionado) {
        g.setColor(Paleta.PANEL);
        RoundRectangle2D fondo = new RoundRectangle2D.Double(X, Y, ANCHO, ALTO, 16, 16);
        g.fill(fondo);
        g.setColor(Paleta.PANEL_BORDE);
        g.setStroke(new BasicStroke(1f));
        g.draw(fondo);

        Texto.izquierda(g, "Árbol de " + seleccionado.getNombre(), X + 14, Y + 24, 15, Paleta.TEXTO);
        Texto.derecha(g, "1-5 / TAB / click", X + ANCHO - 14, Y + 24, 11, Paleta.TEXTO_TENUE);

        Juicio juicio = partida.juicioVisibleDe(seleccionado);
        vista.dibujarCompacto(g, juicio == null ? null : juicio.getRecorrido(), X + 12, Y + 36, ANCHO - 24, 132);

        double renglon = Y + ALTO - 42;
        if (juicio != null) {
            String hoja = juicio.getRecorrido().actual().getDato().getTexto();
            Texto.izquierda(g, "Llegó a: " + hoja, X + 14, renglon, 13, Paleta.TEXTO_SUAVE);
            Texto.izquierda(g, "Dijo: " + juicio.getPalabra().getDicho(), X + 14, renglon + 20, 13, Paleta.CAMINO);
        } else {
            Parrafo.izquierda(g, estado(partida, seleccionado), X + 14, renglon, 13, Paleta.TEXTO_TENUE, ANCHO - 28);
        }
    }

    // Por qué no hay camino para mostrar
    private String estado(Partida partida, Personaje p) {
        Afirmacion a = partida.getAfirmacion();
        if (!p.estaEnJuego()) {
            return "Quedó fuera de juego.";
        }
        if (a == null || partida.getFase() == Partida.Fase.HABLANDO) {
            return "Espera la próxima afirmación.";
        }
        if (p.getNombre().equals(a.getHablante())) {
            return "Habló: no juzga su propia afirmación.";
        }
        if (p.getNombre().equals(a.getObjetivo())) {
            return "La afirmación habla de este personaje: no juzga.";
        }
        return "Está juzgando… su camino se ve cuando juzguen todos.";
    }
}
