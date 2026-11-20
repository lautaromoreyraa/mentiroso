package juego.pantallas;

import estructuras.ListaDoble;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public final class Parrafo {

    // Sin instancias
    private Parrafo() {
    }

    // Corta el texto en líneas que entran en el ancho
    public static ListaDoble<String> lineas(Graphics2D g, String texto, int tamanio, boolean negrita, double ancho) {
        g.setFont(fuente(tamanio, negrita));
        FontMetrics medidas = g.getFontMetrics();
        ListaDoble<String> lineas = new ListaDoble<>();
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras(texto)) {
            String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
            if (medidas.stringWidth(prueba) > ancho && actual.length() > 0) {
                lineas.agregar(actual.toString());
                actual = new StringBuilder(palabra);
            } else {
                actual = new StringBuilder(prueba);
            }
        }
        if (actual.length() > 0) {
            lineas.agregar(actual.toString());
        }
        return lineas;
    }

    // Dibuja a la izquierda y devuelve la y de la línea siguiente
    public static double izquierda(Graphics2D g, String texto, double x, double y, int tamanio, Color color,
            double ancho) {
        return izquierda(g, texto, x, y, tamanio, color, ancho, false);
    }

    public static double izquierda(Graphics2D g, String texto, double x, double y, int tamanio, Color color,
            double ancho, boolean negrita) {
        double renglon = tamanio * 1.35;
        for (String linea : lineas(g, texto, tamanio, negrita, ancho)) {
            g.setFont(fuente(tamanio, negrita));
            g.setColor(color);
            g.drawString(linea, (float) x, (float) y);
            y += renglon;
        }
        return y;
    }

    // Dibuja centrado en x, con el bloque centrado verticalmente en y
    public static void centrado(Graphics2D g, String texto, double x, double y, int tamanio, Color color,
            double ancho, boolean negrita) {
        ListaDoble<String> lineas = lineas(g, texto, tamanio, negrita, ancho);
        double renglon = tamanio * 1.2;
        double primera = y - (lineas.tamanio() - 1) * renglon / 2.0 + tamanio * 0.35;
        g.setFont(fuente(tamanio, negrita));
        g.setColor(color);
        FontMetrics medidas = g.getFontMetrics();
        for (String linea : lineas) {
            g.drawString(linea, (float) (x - medidas.stringWidth(linea) / 2.0), (float) primera);
            primera += renglon;
        }
    }

    // Palabras separadas por espacios, en una Lista propia
    public static ListaDoble<String> palabras(String texto) {
        ListaDoble<String> palabras = new ListaDoble<>();
        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == ' ') {
                if (actual.length() > 0) {
                    palabras.agregar(actual.toString());
                    actual.setLength(0);
                }
            } else {
                actual.append(c);
            }
        }
        if (actual.length() > 0) {
            palabras.agregar(actual.toString());
        }
        return palabras;
    }

    public static Font fuente(int tamanio, boolean negrita) {
        return new Font(Font.SANS_SERIF, negrita ? Font.BOLD : Font.PLAIN, tamanio);
    }
}
