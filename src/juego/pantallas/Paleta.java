package juego.pantallas;

import java.awt.Color;

public final class Paleta {

    // Fondo y superficies
    public static final Color FONDO = new Color(17, 19, 26);
    public static final Color PANEL = new Color(28, 31, 42);
    public static final Color PANEL_BORDE = new Color(52, 57, 74);
    public static final Color TAPETE = new Color(30, 74, 62);
    public static final Color TAPETE_BORDE = new Color(86, 62, 40);

    // Texto
    public static final Color TEXTO = new Color(236, 233, 224);
    public static final Color TEXTO_SUAVE = new Color(160, 164, 178);
    public static final Color TEXTO_TENUE = new Color(102, 107, 124);

    // Acentos
    public static final Color ACENTO = new Color(240, 182, 72);
    public static final Color VIDA = new Color(222, 92, 92);
    public static final Color GLOBO = new Color(244, 240, 228);
    public static final Color GLOBO_TEXTO = new Color(30, 30, 36);

    // Árbol
    public static final Color NODO = new Color(46, 51, 68);
    public static final Color NODO_BORDE = new Color(84, 90, 112);
    public static final Color RAMA = new Color(70, 76, 96);
    public static final Color CAMINO = new Color(240, 182, 72);
    public static final Color CAMINO_TEXTO = new Color(28, 24, 18);

    // Un color por asiento
    public static Color dePersonaje(int asiento) {
        switch (asiento % 5) {
            case 0:
                return new Color(96, 156, 228);
            case 1:
                return new Color(214, 122, 168);
            case 2:
                return new Color(122, 190, 128);
            case 3:
                return new Color(196, 150, 230);
            default:
                return new Color(228, 150, 98);
        }
    }

    // Sin instancias
    private Paleta() {
    }
}
