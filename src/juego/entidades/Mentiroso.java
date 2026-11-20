package juego.entidades;

import juego.modelo.Hecho;
import juego.modelo.Resultado;

public class Mentiroso extends Rol {

    // Identidad
    @Override
    public String getNombre() {
        return "Mentiroso";
    }

    @Override
    public String getObjetivo() {
        return "Colá mentiras sin que te pesquen: cada mentira que nadie acusa suma un punto extra.";
    }

    // Afirma: miente seguido y disimula poco
    @Override
    public double probabilidadDeMentir() {
        return 0.60;
    }

    @Override
    public double probabilidadDeGesto() {
        return 0.30;
    }

    @Override
    public double astucia() {
        return 0.50;
    }

    @Override
    public boolean mentiraAceptable(Personaje quien, Hecho mentira) {
        return !quien.contradiceLoPublico(mentira);
    }

    // Juzga: acusa con poca evidencia, para sembrar caos
    @Override
    public double umbralDeAcusacion() {
        return 0.50;
    }

    // Bonus: mentira que pasó sin acusación
    @Override
    public int bonificar(Resultado resultado, String nombre) {
        boolean colada = resultado.esHablante(nombre) && !resultado.eraVerdad() && !resultado.huboAcusacion();
        return colada ? 2 : 0;
    }
}
