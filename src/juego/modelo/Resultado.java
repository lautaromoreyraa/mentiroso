package juego.modelo;

import estructuras.ListaDoble;

public final class Resultado {

    // Atributos
    private final Afirmacion afirmacion;
    private final boolean eraVerdad;
    private final ListaDoble<String> acusadores = new ListaDoble<>();
    private final ListaDoble<String> creyentes = new ListaDoble<>();

    // Constructor
    public Resultado(Afirmacion afirmacion, boolean eraVerdad) {
        if (afirmacion == null) {
            throw new IllegalArgumentException("el resultado necesita una afirmación");
        }
        this.afirmacion = afirmacion;
        this.eraVerdad = eraVerdad;
    }

    // Registro de cada juicio
    public void registrar(String juez, Palabra palabra) {
        if (juez == null || palabra == null) {
            throw new IllegalArgumentException("el juicio necesita juez y palabra");
        }
        if (juez.equals(afirmacion.getHablante()) || juez.equals(afirmacion.getObjetivo())) {
            throw new IllegalArgumentException(juez + " no puede juzgar esta afirmación");
        }
        if (acusadores.contiene(juez) || creyentes.contiene(juez)) {
            throw new IllegalStateException(juez + " ya juzgó");
        }
        if (palabra.acusa()) {
            acusadores.agregar(juez);
        } else {
            creyentes.agregar(juez);
        }
    }

    // Consultas
    public Afirmacion getAfirmacion() {
        return afirmacion;
    }

    public boolean eraVerdad() {
        return eraVerdad;
    }

    public boolean huboAcusacion() {
        return !acusadores.estaVacia();
    }

    public boolean acuso(String juez) {
        return acusadores.contiene(juez);
    }

    public boolean creyo(String juez) {
        return creyentes.contiene(juez);
    }

    public boolean acerto(String juez) {
        return (acuso(juez) && !eraVerdad) || (creyo(juez) && eraVerdad);
    }

    public boolean esHablante(String nombre) {
        return afirmacion.getHablante().equals(nombre);
    }

    public ListaDoble<String> getAcusadores() {
        return acusadores;
    }

    public ListaDoble<String> getCreyentes() {
        return creyentes;
    }
}
