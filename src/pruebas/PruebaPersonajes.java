package pruebas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.entidades.Analitico;
import juego.entidades.Bot;
import juego.entidades.Jugador;
import juego.entidades.Mentiroso;
import juego.entidades.Personaje;
import juego.entidades.Sincero;
import juego.modelo.Afirmacion;
import juego.modelo.Ficha;
import juego.modelo.Hecho;
import juego.modelo.Palabra;
import juego.modelo.Rasgo;
import juego.modelo.Resultado;
import juego.modelo.Sospecha;
import juego.reglas.Repartidor;

public class PruebaPersonajes {

    // Punto de entrada
    public static void main(String[] args) {
        Comprobador c = new Comprobador("Roles y personajes");

        repartoDeRoles(c);
        sinceroNuncaMiente(c);
        memoriaYDeduccion(c);
        juicioSegunElRol(c);
        bonusPorRol(c);
        validaciones(c);

        c.terminar();
    }

    // Siempre 2 Mentirosos, 2 Sinceros y 1 Analítico, con un solo humano
    private static void repartoDeRoles(Comprobador c) {
        boolean siempreBien = true;
        for (int semilla = 0; semilla < 100; semilla++) {
            Lista<Personaje> mesa = new Repartidor(new Random(semilla)).repartirPersonajes(nombres(), semilla % 5);
            int mentirosos = 0;
            int sinceros = 0;
            int analiticos = 0;
            int humanos = 0;
            for (int i = 0; i < mesa.tamanio(); i++) {
                Personaje p = mesa.obtener(i);
                String rol = p.getRol().getNombre();
                mentirosos += rol.equals("Mentiroso") ? 1 : 0;
                sinceros += rol.equals("Sincero") ? 1 : 0;
                analiticos += rol.equals("Analítico") ? 1 : 0;
                humanos += p.decideSolo() ? 0 : 1;
                if (p.getPrivados().tamanio() != Repartidor.HECHOS_PRIVADOS) {
                    siempreBien = false;
                }
            }
            if (mentirosos != 2 || sinceros != 2 || analiticos != 1 || humanos != 1) {
                siempreBien = false;
            }
        }
        c.comprobar("100 repartos: 2-2-1, un humano y 2 hechos privados cada uno", siempreBien);
    }

    // Un Sincero sólo cuenta lo que sabe, y lo que sabe es verdad
    private static void sinceroNuncaMiente(Comprobador c) {
        boolean nuncaMintio = true;
        int afirmaciones = 0;
        for (int semilla = 0; semilla < 60; semilla++) {
            Random azar = new Random(semilla);
            Lista<Personaje> mesa = new Repartidor(azar).repartirPersonajes(nombres(), 0);
            for (int i = 0; i < mesa.tamanio(); i++) {
                Personaje p = mesa.obtener(i);
                if (!p.decideSolo() || !p.getRol().getNombre().equals("Sincero")) {
                    continue;
                }
                for (int vez = 0; vez < 20; vez++) {
                    Hecho dicho = p.afirmar(otros(mesa, p), azar);
                    Personaje objetivo = buscar(mesa, dicho.getSujeto());
                    afirmaciones++;
                    if (!objetivo.getFicha().tiene(dicho.getRasgo(), dicho.getValor())) {
                        nuncaMintio = false;
                    }
                }
            }
        }
        c.comprobar("un Sincero no afirma nada falso en " + afirmaciones + " afirmaciones", nuncaMintio);
    }

    // Lo que cada uno recuerda y lo que puede deducir
    private static void memoriaYDeduccion(Comprobador c) {
        Ficha fichaAna = new Ficha("un gato", "fútbol", "la pizza", "Salta");
        Bot ana = new Bot("Ana", fichaAna, new Sincero());
        Bot bruno = new Bot("Bruno", new Ficha("un perro", "tenis", "el asado", "Rosario"), new Analitico());

        c.comprobar("cada uno sabe su propia ficha", ana.recuerda("Ana", Rasgo.MASCOTA).getValor(), "un gato");
        c.comprobar("sin hechos no sabe nada de otro", ana.recuerda("Carla", Rasgo.CIUDAD) == null);
        c.comprobar("quién tiene un valor propio", ana.quienTiene(Rasgo.MASCOTA, "un gato"), "Ana");

        ana.aprender(new Hecho("Carla", Rasgo.CIUDAD, "Córdoba"));
        c.comprobar("recuerda un hecho privado", ana.recuerda("Carla", Rasgo.CIUDAD).getValor(), "Córdoba");
        c.comprobar("deduce de quién es un valor", ana.quienTiene(Rasgo.CIUDAD, "Córdoba"), "Carla");

        Hecho viejo = new Hecho("Dante", Rasgo.COMIDA, "el sushi");
        ana.enterarse(viejo);
        bruno.enterarse(viejo);
        for (int i = 0; i < 3; i++) {
            Hecho nuevo = new Hecho("Eva", Rasgo.todos().obtener(i), Rasgo.todos().obtener(i).valor(4));
            ana.enterarse(nuevo);
            bruno.enterarse(nuevo);
        }
        c.comprobar("el Sincero olvida hechos públicos viejos", ana.recuerda("Dante", Rasgo.COMIDA) == null);
        c.comprobar("el Analítico los recuerda", bruno.recuerda("Dante", Rasgo.COMIDA) != null);

        bruno.enterarseDeMentira("Carla");
        bruno.enterarseDeMentira("Carla");
        c.comprobar("cuenta las veces que pescaron a alguien", bruno.vecesPescado("Carla"), 2);
        c.comprobar("deducible por un hecho público del mismo sujeto",
                bruno.esDeduciblePublicamente(new Hecho("Dante", Rasgo.COMIDA, "la pizza")));
        c.comprobar("deducible porque el valor ya es de otro",
                bruno.esDeduciblePublicamente(new Hecho("Carla", Rasgo.COMIDA, "el sushi")));
        c.comprobar("no deducible si lo público no dice nada",
                !bruno.esDeduciblePublicamente(new Hecho("Carla", Rasgo.MASCOTA, "un loro")));
    }

    // Mismo nivel de sospecha, distinta palabra según el rol
    private static void juicioSegunElRol(Comprobador c) {
        Ficha f = new Ficha("un gato", "fútbol", "la pizza", "Salta");
        Sospecha dudosa = new Sospecha(true, 0.60);
        c.comprobar("el Mentiroso acusa con 60%", new Bot("A", f, new Mentiroso()).juzgar(dudosa), Palabra.MENTIROSO);
        c.comprobar("el Analítico cree con 60%", new Bot("B", f, new Analitico()).juzgar(dudosa), Palabra.TE_CREO);
        c.comprobar("el Sincero cree con 60%", new Bot("C", f, new Sincero()).juzgar(dudosa), Palabra.TE_CREO);
        c.comprobar("el Sincero acusa con certeza",
                new Bot("C", f, new Sincero()).juzgar(new Sospecha(true, 1.0)), Palabra.MENTIROSO);

        Jugador vos = new Jugador("Eva", f, new Sincero());
        vos.setPalabra(Palabra.MENTIROSO);
        c.comprobar("el humano dice lo que eligió", vos.juzgar(dudosa), Palabra.MENTIROSO);
        c.comprobar("la elección se consume", !vos.tienePalabra());
        c.esperarExcepcion("juzgar sin haber elegido", IllegalStateException.class, () -> vos.juzgar(dudosa));
    }

    // Cada rol suma su bonus en una situación distinta
    private static void bonusPorRol(Comprobador c) {
        Afirmacion mentira = new Afirmacion("Ana", "Bruno", Rasgo.MASCOTA, "un loro", false);
        Resultado colada = new Resultado(mentira, false);
        colada.registrar("Carla", Palabra.TE_CREO);
        c.comprobar("Mentiroso: mentira colada", new Mentiroso().bonificar(colada, "Ana"), 1);
        c.comprobar("Analítico: creer una mentira no suma", new Analitico().bonificar(colada, "Carla"), 0);

        Resultado pescada = new Resultado(mentira, false);
        pescada.registrar("Carla", Palabra.MENTIROSO);
        pescada.registrar("Dante", Palabra.TE_CREO);
        c.comprobar("Mentiroso: mentira pescada no suma", new Mentiroso().bonificar(pescada, "Ana"), 0);
        c.comprobar("Sincero: acusación acertada", new Sincero().bonificar(pescada, "Carla"), 1);
        c.comprobar("Analítico: acusación acertada", new Analitico().bonificar(pescada, "Carla"), 1);
        c.comprobar("Sincero: creer una mentira no suma", new Sincero().bonificar(pescada, "Dante"), 0);

        Resultado verdad = new Resultado(new Afirmacion("Ana", "Bruno", Rasgo.MASCOTA, "un perro", false), true);
        verdad.registrar("Carla", Palabra.TE_CREO);
        c.comprobar("Analítico: creer una verdad suma", new Analitico().bonificar(verdad, "Carla"), 1);
        c.comprobar("Sincero: creer una verdad no suma", new Sincero().bonificar(verdad, "Carla"), 0);
    }

    // Setters y constructores que validan
    private static void validaciones(Comprobador c) {
        Bot ana = new Bot("Ana", new Ficha("un gato", "fútbol", "la pizza", "Salta"), new Sincero());
        c.esperarExcepcion("vidas negativas", IllegalArgumentException.class, () -> ana.setVidas(-1));
        c.esperarExcepcion("más vidas que las iniciales", IllegalArgumentException.class, () -> ana.setVidas(4));
        c.esperarExcepcion("restar puntos", IllegalArgumentException.class, () -> ana.sumarPuntos(-2));
        c.esperarExcepcion("un hecho privado sobre uno mismo", IllegalArgumentException.class,
                () -> ana.aprender(new Hecho("Ana", Rasgo.MASCOTA, "un gato")));
        c.esperarExcepcion("un personaje sin nombre", IllegalArgumentException.class,
                () -> new Bot(" ", ana.getFicha(), new Sincero()));
        ana.perderVida();
        ana.perderVida();
        ana.perderVida();
        ana.perderVida();
        c.comprobar("las vidas no bajan de 0", ana.getVidas(), 0);
        c.comprobar("sin vidas queda fuera de juego", !ana.estaEnJuego());
    }

    // Ayudas
    static Lista<String> nombres() {
        Lista<String> nombres = new ListaDoble<>();
        nombres.agregar("Eva");
        nombres.agregar("Ana");
        nombres.agregar("Bruno");
        nombres.agregar("Carla");
        nombres.agregar("Dante");
        return nombres;
    }

    private static Lista<String> otros(Lista<Personaje> mesa, Personaje yo) {
        Lista<String> otros = new ListaDoble<>();
        for (int i = 0; i < mesa.tamanio(); i++) {
            if (mesa.obtener(i) != yo) {
                otros.agregar(mesa.obtener(i).getNombre());
            }
        }
        return otros;
    }

    private static Personaje buscar(Lista<Personaje> mesa, String nombre) {
        for (int i = 0; i < mesa.tamanio(); i++) {
            if (mesa.obtener(i).getNombre().equals(nombre)) {
                return mesa.obtener(i);
            }
        }
        throw new IllegalArgumentException("no está en la mesa: " + nombre);
    }
}
