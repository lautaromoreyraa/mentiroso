package juego.pantallas;

import estructuras.ArbolBinario;
import estructuras.Lista;
import estructuras.NodoArbol;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import juego.arbol.NodoDecision;
import juego.arbol.Recorrido;
import ui.Texto;

public class VistaArbol {

    // Constantes
    private static final double ALTO_NODO = 40;
    private static final double RADIO_COMPACTO = 5.5;
    private static final double ANCHO_MAXIMO_NODO = 140;

    // Atributos
    private final ArbolBinario<NodoDecision> arbol;
    private final Lista<NodoArbol<NodoDecision>> enOrden;

    // Constructor
    public VistaArbol(ArbolBinario<NodoDecision> arbol) {
        if (arbol == null || arbol.estaVacio()) {
            throw new IllegalArgumentException("no hay árbol para dibujar");
        }
        this.arbol = arbol;
        this.enOrden = arbol.nodosInorden();
    }

    // Dibujo con texto en cada nodo, para la investigación
    public void dibujarDetallado(Graphics2D g, Recorrido recorrido, double x, double y, double ancho, double alto) {
        dibujar(g, recorrido, x, y, ancho, alto, true);
    }

    // Dibujo compacto, para el minimapa
    public void dibujarCompacto(Graphics2D g, Recorrido recorrido, double x, double y, double ancho, double alto) {
        dibujar(g, recorrido, x, y, ancho, alto, false);
    }

    // Recorrido inorden para la x, profundidad para la y
    private void dibujar(Graphics2D g, Recorrido recorrido, double x, double y, double ancho, double alto,
            boolean detallado) {
        Lista<NodoArbol<NodoDecision>> camino = recorrido == null ? null : recorrido.camino();
        NodoArbol<NodoDecision> actual = recorrido == null ? null : recorrido.actual();
        double margen = detallado ? ALTO_NODO / 2 : RADIO_COMPACTO + 2;
        double lateral = detallado ? ANCHO_MAXIMO_NODO : 0;
        x += lateral / 2;
        ancho -= lateral;
        double columna = ancho / enOrden.tamanio();
        double fila = (alto - 2 * margen) / Math.max(1, arbol.altura() - 1);

        Stroke anterior = g.getStroke();
        for (int i = 0; i < enOrden.tamanio(); i++) {
            NodoArbol<NodoDecision> nodo = enOrden.obtener(i);
            double px = x + columna * (i + 0.5);
            double py = y + margen + fila * nodo.profundidad();
            rama(g, camino, nodo, nodo.getSi(), px, py, x, y, columna, fila, margen, detallado, "sí");
            rama(g, camino, nodo, nodo.getNo(), px, py, x, y, columna, fila, margen, detallado, "no");
        }
        g.setStroke(anterior);

        for (int i = 0; i < enOrden.tamanio(); i++) {
            NodoArbol<NodoDecision> nodo = enOrden.obtener(i);
            double px = x + columna * (i + 0.5);
            double py = y + margen + fila * nodo.profundidad();
            boolean enCamino = camino != null && camino.indiceDe(nodo) >= 0;
            if (detallado) {
                nodoDetallado(g, nodo, px, py, Math.min(columna * 1.85, ANCHO_MAXIMO_NODO), enCamino, nodo == actual);
            } else {
                nodoCompacto(g, nodo, px, py, enCamino);
            }
        }
        g.setStroke(anterior);
    }

    // Una rama entre un nodo y su hijo
    private void rama(Graphics2D g, Lista<NodoArbol<NodoDecision>> camino, NodoArbol<NodoDecision> padre,
            NodoArbol<NodoDecision> hijo, double px, double py, double x, double y, double columna, double fila,
            double margen, boolean detallado, String etiqueta) {
        if (hijo == null) {
            return;
        }
        double hx = x + columna * (enOrden.indiceDe(hijo) + 0.5);
        double hy = y + margen + fila * hijo.profundidad();
        boolean enCamino = camino != null && camino.indiceDe(padre) >= 0 && camino.indiceDe(hijo) >= 0;
        float grosor = enCamino ? (detallado ? 4f : 2.6f) : (detallado ? 1.6f : 1.1f);
        g.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(enCamino ? Paleta.CAMINO : Paleta.RAMA);
        g.draw(new Line2D.Double(px, py, hx, hy));
        if (detallado) {
            double mx = (px + hx) / 2 + (etiqueta.equals("sí") ? -14 : 14);
            double my = (py + hy) / 2;
            Texto.centrado(g, etiqueta, mx, my, 12, enCamino ? Paleta.CAMINO : Paleta.TEXTO_TENUE);
        }
    }

    // Nodo con su texto
    private void nodoDetallado(Graphics2D g, NodoArbol<NodoDecision> nodo, double cx, double cy, double ancho,
            boolean enCamino, boolean esActual) {
        double arco = nodo.esHoja() ? ALTO_NODO : 12;
        RoundRectangle2D caja = new RoundRectangle2D.Double(cx - ancho / 2, cy - ALTO_NODO / 2, ancho, ALTO_NODO,
                arco, arco);
        g.setColor(enCamino ? Paleta.CAMINO : Paleta.NODO);
        g.fill(caja);
        g.setStroke(new BasicStroke(esActual ? 3f : 1.2f));
        g.setColor(esActual ? Paleta.TEXTO : (enCamino ? Paleta.CAMINO : Paleta.NODO_BORDE));
        g.draw(caja);
        Parrafo.centrado(g, nodo.getDato().getTexto(), cx, cy, 12, enCamino ? Paleta.CAMINO_TEXTO : Paleta.TEXTO,
                ancho - 10, nodo.esHoja());
    }

    // Nodo como un punto: círculo si pregunta, cuadrado redondeado si veredicto
    private void nodoCompacto(Graphics2D g, NodoArbol<NodoDecision> nodo, double cx, double cy, boolean enCamino) {
        double r = RADIO_COMPACTO;
        g.setColor(enCamino ? Paleta.CAMINO : Paleta.NODO);
        if (nodo.esHoja()) {
            g.fill(new RoundRectangle2D.Double(cx - r, cy - r, 2 * r, 2 * r, 4, 4));
        } else {
            g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        }
        g.setStroke(new BasicStroke(1f));
        g.setColor(enCamino ? Paleta.CAMINO : Paleta.NODO_BORDE);
        if (nodo.esHoja()) {
            g.draw(new RoundRectangle2D.Double(cx - r, cy - r, 2 * r, 2 * r, 4, 4));
        } else {
            g.draw(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        }
    }
}
