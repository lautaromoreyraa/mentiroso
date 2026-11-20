package juego.entidades;

import juego.modelo.Hecho;
import juego.modelo.Resultado;

public abstract class Rol {

    // Constantes
    public static final double GESTO_FALSO = 0.15;

    // Identidad del rol
    public abstract String getNombre();

    public abstract String getObjetivo();

    // Cómo afirma
    public abstract double probabilidadDeMentir();

    public abstract double probabilidadDeGesto();

    public boolean mentiraAceptable(Personaje quien, Hecho mentira) {
        return true;
    }

    // Cómo juzga
    public abstract double umbralDeAcusacion();

    public boolean recuerdaPublico(int antiguedad) {
        return true;
    }

    // Puntos extra según el objetivo del rol
    public abstract int bonificar(Resultado resultado, String nombre);

    // Igualdad por tipo de rol, para adivinar roles al final
    public boolean esMismoRolQue(Rol otro) {
        return otro != null && getNombre().equals(otro.getNombre());
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
