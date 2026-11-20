package juego.entidades;

import estructuras.Lista;
import java.util.Random;
import juego.modelo.Ficha;
import juego.modelo.Hecho;
import juego.modelo.Palabra;
import juego.modelo.Sospecha;

public class Jugador extends Personaje {

    // Decisiones que carga la pantalla
    private Hecho afirmacionElegida;
    private Palabra palabraElegida;

    // Constructor
    public Jugador(String nombre, Ficha ficha, Rol rol) {
        super(nombre, ficha, rol);
    }

    // El humano decide desde las escenas
    @Override
    public boolean decideSolo() {
        return false;
    }

    public void setAfirmacion(Hecho elegida) {
        if (elegida == null || elegida.getSujeto().equals(getNombre())) {
            throw new IllegalArgumentException("hay que elegir una afirmación sobre otro jugador");
        }
        this.afirmacionElegida = elegida;
    }

    public void setPalabra(Palabra elegida) {
        if (elegida == null) {
            throw new IllegalArgumentException("hay que elegir Verdad o ¡Mentiroso!");
        }
        this.palabraElegida = elegida;
    }

    public boolean tieneAfirmacion() {
        return afirmacionElegida != null;
    }

    public boolean tienePalabra() {
        return palabraElegida != null;
    }

    // Entrega lo elegido y lo consume, para no repetirlo en el turno siguiente
    @Override
    public Hecho afirmar(Lista<String> otros, Random azar) {
        if (afirmacionElegida == null) {
            throw new IllegalStateException("el jugador todavía no eligió qué afirmar");
        }
        if (otros.indiceDe(afirmacionElegida.getSujeto()) < 0) {
            throw new IllegalStateException(afirmacionElegida.getSujeto() + " no está en juego");
        }
        Hecho elegida = afirmacionElegida;
        afirmacionElegida = null;
        return elegida;
    }

    @Override
    public Palabra juzgar(Sospecha sospecha) {
        if (palabraElegida == null) {
            throw new IllegalStateException("el jugador todavía no eligió Verdad o ¡Mentiroso!");
        }
        Palabra elegida = palabraElegida;
        palabraElegida = null;
        return elegida;
    }
}
