package juego.reglas;

import estructuras.Cola;
import estructuras.ColaEnlazada;
import estructuras.Lista;
import estructuras.ListaDoble;
import juego.entidades.Jugador;
import juego.entidades.Personaje;
import juego.modelo.Afirmacion;
import juego.modelo.Hecho;

public class Mesa {

    // Atributos
    private final ListaDoble<Personaje> personajes = new ListaDoble<>();
    private final Cola<Personaje> turnos = new ColaEnlazada<>();
    private final ListaDoble<Hecho> publicos = new ListaDoble<>();
    private final ListaDoble<Afirmacion> historial = new ListaDoble<>();
    private final Jugador humano;

    // Constructor: los turnos arrancan desde el asiento indicado
    public Mesa(Lista<Personaje> sentados, Jugador humano, int primerTurno) {
        if (sentados == null || sentados.tamanio() != Repartidor.JUGADORES) {
            throw new IllegalArgumentException("la mesa es de " + Repartidor.JUGADORES + " jugadores");
        }
        if (humano == null || sentados.indiceDe(humano) < 0) {
            throw new IllegalArgumentException("el humano tiene que estar sentado a la mesa");
        }
        if (primerTurno < 0 || primerTurno >= sentados.tamanio()) {
            throw new IllegalArgumentException("asiento inválido para el primer turno: " + primerTurno);
        }
        for (int i = 0; i < sentados.tamanio(); i++) {
            Personaje p = sentados.obtener(i);
            if (buscar(p.getNombre()) != null) {
                throw new IllegalArgumentException("nombre repetido en la mesa: " + p.getNombre());
            }
            personajes.agregar(p);
        }
        for (int i = 0; i < personajes.tamanio(); i++) {
            turnos.encolar(personajes.obtener((primerTurno + i) % personajes.tamanio()));
        }
        this.humano = humano;
    }

    // Turnos: el que habla sale de la cola y vuelve al final; el que quedó afuera no vuelve
    public Personaje siguienteHablante() {
        while (!turnos.estaVacia()) {
            Personaje candidato = turnos.desencolar();
            if (candidato.estaEnJuego()) {
                turnos.encolar(candidato);
                return candidato;
            }
        }
        return null;
    }

    public Personaje proximoEnHablar() {
        return turnos.estaVacia() ? null : turnos.frente();
    }

    // Búsquedas: por nombre o por asiento
    public Personaje buscar(String nombre) {
        for (Personaje p : personajes) {
            if (p.getNombre().equals(nombre)) {
                return p;
            }
        }
        return null;
    }

    public Personaje buscar(int asiento) {
        return personajes.obtener(asiento);
    }

    public int asientoDe(Personaje personaje) {
        return personajes.indiceDe(personaje);
    }

    // Consultas
    public ListaDoble<Personaje> getPersonajes() {
        return personajes;
    }

    public Jugador getHumano() {
        return humano;
    }

    public int cantidad() {
        return personajes.tamanio();
    }

    public int enJuego() {
        int cantidad = 0;
        for (Personaje p : personajes) {
            if (p.estaEnJuego()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public Lista<String> otrosEnJuego(Personaje yo) {
        Lista<String> otros = new ListaDoble<>();
        for (Personaje p : personajes) {
            if (p != yo && p.estaEnJuego()) {
                otros.agregar(p.getNombre());
            }
        }
        return otros;
    }

    public Lista<Personaje> jueces(Afirmacion afirmacion) {
        Lista<Personaje> jueces = new ListaDoble<>();
        for (Personaje p : personajes) {
            boolean involucrado = p.getNombre().equals(afirmacion.getHablante())
                    || p.getNombre().equals(afirmacion.getObjetivo());
            if (!involucrado && p.estaEnJuego()) {
                jueces.agregar(p);
            }
        }
        return jueces;
    }

    public ListaDoble<Hecho> getPublicos() {
        return publicos;
    }

    public ListaDoble<Afirmacion> getHistorial() {
        return historial;
    }

    // Lo que se vuelve público
    public void registrar(Afirmacion afirmacion) {
        historial.agregar(afirmacion);
    }

    public void revelar(Hecho hecho) {
        if (publicos.contiene(hecho)) {
            return;
        }
        publicos.agregar(hecho);
        for (Personaje p : personajes) {
            p.enterarse(hecho);
        }
    }

    public void anunciarMentira(String mentiroso) {
        for (Personaje p : personajes) {
            p.enterarseDeMentira(mentiroso);
        }
    }

    // Ranking por puntaje, ordenado por inserción en una Lista nueva
    public Lista<Personaje> ranking() {
        Lista<Personaje> orden = new ListaDoble<>();
        for (Personaje p : personajes) {
            int lugar = 0;
            while (lugar < orden.tamanio() && orden.obtener(lugar).getPuntaje() >= p.getPuntaje()) {
                lugar++;
            }
            orden.insertar(lugar, p);
        }
        return orden;
    }
}
