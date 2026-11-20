package juego.pantallas;

import motor.Ventana;

public class Main {

    // Constantes
    public static final int ANCHO = 1200;
    public static final int ALTO = 720;

    // Punto de entrada
    public static void main(String[] args) {
        Ventana.abrir("Mentiroso", ANCHO, ALTO, new EscenaMenu());
    }
}
