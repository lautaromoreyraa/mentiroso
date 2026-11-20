package juego.reglas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.entidades.Analitico;
import juego.entidades.Bot;
import juego.entidades.Jugador;
import juego.entidades.Mentiroso;
import juego.entidades.Personaje;
import juego.entidades.Rol;
import juego.entidades.Sincero;
import juego.modelo.Ficha;
import juego.modelo.Hecho;
import juego.modelo.Rasgo;

public class Repartidor {

    // Constantes
    public static final int JUGADORES = 5;
    public static final int HECHOS_PRIVADOS = 2;

    // Atributos
    private final Random azar;

    // Constructor
    public Repartidor(Random azar) {
        if (azar == null) {
            throw new IllegalArgumentException("el repartidor necesita una fuente de azar");
        }
        this.azar = azar;
    }

    // Fichas: cada valor de cada rasgo le toca a una sola persona
    public Lista<Ficha> repartirFichas(int cantidad) {
        if (cantidad < 1 || cantidad > Rasgo.VALORES_POR_RASGO) {
            throw new IllegalArgumentException("se reparten de 1 a " + Rasgo.VALORES_POR_RASGO + " fichas");
        }
        Lista<String> mascotas = Rasgo.MASCOTA.copiaDeValores();
        Lista<String> deportes = Rasgo.DEPORTE.copiaDeValores();
        Lista<String> comidas = Rasgo.COMIDA.copiaDeValores();
        Lista<String> ciudades = Rasgo.CIUDAD.copiaDeValores();
        Lista<Ficha> fichas = new ListaDoble<>();
        for (int i = 0; i < cantidad; i++) {
            fichas.agregar(new Ficha(sacar(mascotas), sacar(deportes), sacar(comidas), sacar(ciudades)));
        }
        return fichas;
    }

    // Roles: 2 Mentirosos, 2 Sinceros y 1 Analítico, mezclados
    public Lista<Rol> repartirRoles() {
        Lista<Rol> mazo = new ListaDoble<>();
        mazo.agregar(new Mentiroso());
        mazo.agregar(new Mentiroso());
        mazo.agregar(new Sincero());
        mazo.agregar(new Sincero());
        mazo.agregar(new Analitico());
        Lista<Rol> mezclados = new ListaDoble<>();
        while (!mazo.estaVacia()) {
            mezclados.agregar(sacar(mazo));
        }
        return mezclados;
    }

    // La mesa completa: un humano en el asiento indicado, bots en el resto y un primer turno al azar
    public Mesa armarMesa(Lista<String> nombres, int asientoHumano) {
        if (nombres == null || nombres.tamanio() != JUGADORES) {
            throw new IllegalArgumentException("la mesa es de " + JUGADORES + " jugadores");
        }
        if (asientoHumano < 0 || asientoHumano >= JUGADORES) {
            throw new IllegalArgumentException("asiento inválido para el humano: " + asientoHumano);
        }
        Lista<Ficha> fichas = repartirFichas(JUGADORES);
        Lista<Rol> roles = repartirRoles();
        Lista<Personaje> personajes = new ListaDoble<>();
        Jugador humano = null;
        for (int i = 0; i < JUGADORES; i++) {
            String nombre = nombres.obtener(i);
            if (i == asientoHumano) {
                humano = new Jugador(nombre, fichas.obtener(i), roles.obtener(i));
                personajes.agregar(humano);
            } else {
                personajes.agregar(new Bot(nombre, fichas.obtener(i), roles.obtener(i)));
            }
        }
        for (int i = 0; i < JUGADORES; i++) {
            darHechosPrivados(personajes.obtener(i), personajes);
        }
        return new Mesa(personajes, humano, azar.nextInt(JUGADORES));
    }

    public Lista<Personaje> repartirPersonajes(Lista<String> nombres, int asientoHumano) {
        return armarMesa(nombres, asientoHumano).getPersonajes();
    }

    // Cada uno arranca sabiendo algo de otros
    private void darHechosPrivados(Personaje quien, Lista<Personaje> mesa) {
        Lista<Hecho> posibles = new ListaDoble<>();
        for (int i = 0; i < mesa.tamanio(); i++) {
            Personaje otro = mesa.obtener(i);
            if (otro == quien) {
                continue;
            }
            for (int r = 0; r < Rasgo.todos().tamanio(); r++) {
                Rasgo rasgo = Rasgo.todos().obtener(r);
                posibles.agregar(new Hecho(otro.getNombre(), rasgo, otro.getFicha().valor(rasgo)));
            }
        }
        for (int k = 0; k < HECHOS_PRIVADOS; k++) {
            quien.aprender(sacar(posibles));
        }
    }

    // Elección al azar sin reposición
    public <T> T sacar(Lista<T> lista) {
        if (lista.estaVacia()) {
            throw new IllegalStateException("no queda nada para sacar");
        }
        return lista.eliminar(azar.nextInt(lista.tamanio()));
    }
}
