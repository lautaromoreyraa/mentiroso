package juego.arbol;

import juego.entidades.Personaje;
import juego.modelo.Afirmacion;

public final class Investigacion {

    // Atributos
    private final Personaje evaluador;
    private final Afirmacion afirmacion;

    // Constructor
    public Investigacion(Personaje evaluador, Afirmacion afirmacion) {
        if (evaluador == null || afirmacion == null) {
            throw new IllegalArgumentException("la investigación necesita evaluador y afirmación");
        }
        if (evaluador.getNombre().equals(afirmacion.getHablante())) {
            throw new IllegalArgumentException("nadie juzga su propia afirmación");
        }
        this.evaluador = evaluador;
        this.afirmacion = afirmacion;
    }

    // Consultas
    public Personaje getEvaluador() {
        return evaluador;
    }

    public Afirmacion getAfirmacion() {
        return afirmacion;
    }
}
