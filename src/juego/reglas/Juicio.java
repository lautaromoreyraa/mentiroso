package juego.reglas;

import juego.arbol.Recorrido;
import juego.entidades.Personaje;
import juego.modelo.Palabra;

public final class Juicio {

    // Atributos
    private final Personaje juez;
    private final Recorrido recorrido;
    private final Palabra palabra;

    // Constructor
    public Juicio(Personaje juez, Recorrido recorrido, Palabra palabra) {
        if (juez == null || recorrido == null || palabra == null) {
            throw new IllegalArgumentException("el juicio necesita juez, recorrido y palabra");
        }
        if (!recorrido.estaTerminado()) {
            throw new IllegalArgumentException("el recorrido tiene que llegar a un veredicto");
        }
        this.juez = juez;
        this.recorrido = recorrido;
        this.palabra = palabra;
    }

    // Consultas
    public Personaje getJuez() {
        return juez;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public Palabra getPalabra() {
        return palabra;
    }
}
