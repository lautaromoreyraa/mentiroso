package pruebas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.arbol.Recorrido;
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
import juego.reglas.Juicio;
import juego.reglas.Mesa;
import juego.reglas.Partida;
import juego.reglas.Puntuacion;
import juego.reglas.Repartidor;

public class PruebaReglas {

    // Punto de entrada
    public static void main(String[] args) {
        Comprobador c = new Comprobador("Reglas de la partida");

        colaDeTurnos(c);
        tablaDePuntaje(c);
        revelacion(c);
        partidasSimuladas(c);
        minimapaSinSpoilers(c);

        c.terminar();
    }

    // La Cola de turnos gira siempre igual y saltea a los eliminados
    private static void colaDeTurnos(Comprobador c) {
        Mesa mesa = mesaFija(0);
        StringBuilder orden = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            orden.append(mesa.siguienteHablante().getNombre().charAt(0));
        }
        c.comprobar("los turnos giran en orden", orden.toString(), "EABCDE");
        mesa.buscar("Bruno").setVidas(0);
        StringBuilder sinBruno = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sinBruno.append(mesa.siguienteHablante().getNombre().charAt(0));
        }
        c.comprobar("un eliminado no vuelve a hablar", sinBruno.toString(), "ACDEA");
        c.comprobar("buscar por asiento", mesa.buscar(2).getNombre(), "Bruno");
        c.comprobar("buscar por nombre", mesa.buscar("Carla") == mesa.buscar(3));
    }

    // Los cuatro casos de la tabla base, más el bonus de cada rol
    private static void tablaDePuntaje(Comprobador c) {
        Mesa m1 = mesaFija(0);
        Resultado colada = resultado(m1, false, Palabra.TE_CREO);
        Puntuacion.aplicar(colada, m1);
        c.comprobar("mentira colada: +2 y +2 de bonus de Mentiroso", m1.buscar("Ana").getPuntaje(), 4);

        Mesa m2 = mesaFija(0);
        Puntuacion.aplicar(resultado(m2, false, Palabra.MENTIROSO), m2);
        c.comprobar("mentira pescada: el hablante pierde una vida", m2.buscar("Ana").getVidas(), 2);
        c.comprobar("mentira pescada: el acusador Sincero suma 1 y 1 de bonus", m2.buscar("Carla").getPuntaje(), 2);

        Mesa m3 = mesaFija(0);
        Puntuacion.aplicar(resultado(m3, true, Palabra.MENTIROSO), m3);
        c.comprobar("verdad acusada: el hablante suma 1", m3.buscar("Ana").getPuntaje(), 1);
        c.comprobar("verdad acusada: el acusador pierde una vida", m3.buscar("Carla").getVidas(), 2);

        Mesa m4 = mesaFija(0);
        Puntuacion.aplicar(resultado(m4, true, Palabra.TE_CREO), m4);
        c.comprobar("verdad creída: el hablante suma 1", m4.buscar("Ana").getPuntaje(), 1);
        c.comprobar("verdad creída: el que creyó no suma", m4.buscar("Carla").getPuntaje(), 0);
    }

    // Al acusar, el valor real pasa a ser público para todos
    private static void revelacion(Comprobador c) {
        Mesa mesa = mesaFija(0);
        Hecho real = new Hecho("Bruno", Rasgo.MASCOTA, "un perro");
        mesa.revelar(real);
        mesa.revelar(real);
        c.comprobar("un hecho se revela una sola vez", mesa.getPublicos().tamanio(), 1);
        c.comprobar("todos se enteran", mesa.buscar("Dante").recuerda("Bruno", Rasgo.MASCOTA) != null);
        mesa.anunciarMentira("Ana");
        c.comprobar("todos saben a quién pescaron", mesa.buscar("Eva").vecesPescado("Ana"), 1);
    }

    // Partidas completas con el humano jugando en automático
    private static void partidasSimuladas(Comprobador c) {
        boolean todasTerminan = true;
        boolean rankingOrdenado = true;
        boolean vidasValidas = true;
        boolean revelacionesPublicas = true;
        int maximo = 0;
        for (int semilla = 0; semilla < 200; semilla++) {
            Random azar = new Random(semilla);
            Mesa mesa = new Repartidor(azar).armarMesa(PruebaPersonajes.nombres(), semilla % 5);
            Partida partida = new Partida(mesa, azar);
            int pasos = 0;
            while (!partida.estaTerminada() && pasos < 1000) {
                jugarComoHumano(partida, azar);
                partida.avanzar();
                if (partida.getRevelado() != null && !mesa.getPublicos().contiene(partida.getRevelado())) {
                    revelacionesPublicas = false;
                }
                pasos++;
            }
            todasTerminan &= partida.estaTerminada();
            maximo = Math.max(maximo, partida.getAfirmacionesHechas());
            Lista<Personaje> ranking = mesa.ranking();
            for (int i = 1; i < ranking.tamanio(); i++) {
                rankingOrdenado &= ranking.obtener(i - 1).getPuntaje() >= ranking.obtener(i).getPuntaje();
            }
            for (Personaje p : mesa.getPersonajes()) {
                vidasValidas &= p.getVidas() >= 0 && p.getVidas() <= Personaje.VIDAS_INICIALES;
            }
        }
        c.comprobar("200 partidas terminan", todasTerminan);
        c.comprobar("ninguna pasa de " + Partida.AFIRMACIONES_TOTALES + " afirmaciones", maximo <= Partida.AFIRMACIONES_TOTALES);
        c.comprobar("el ranking queda ordenado", rankingOrdenado);
        c.comprobar("las vidas quedan en rango", vidasValidas);
        c.comprobar("lo revelado queda público", revelacionesPublicas);
    }

    // Mientras se juzga, el humano no ve los caminos de los bots
    private static void minimapaSinSpoilers(Comprobador c) {
        boolean probado = false;
        for (int semilla = 0; semilla < 200 && !probado; semilla++) {
            Random azar = new Random(semilla);
            Mesa mesa = new Repartidor(azar).armarMesa(PruebaPersonajes.nombres(), 0);
            Partida partida = new Partida(mesa, azar);
            for (int pasos = 0; pasos < 200 && !partida.estaTerminada() && !probado; pasos++) {
                if (partida.humanoDebeJuzgar()) {
                    partida.avanzar();
                    boolean ocultos = true;
                    boolean juzgaron = false;
                    for (Personaje p : mesa.getPersonajes()) {
                        if (p != mesa.getHumano() && partida.esJuez(p)) {
                            ocultos &= partida.juicioVisibleDe(p) == null;
                            juzgaron |= partida.juicioDe(p) != null;
                        }
                    }
                    c.comprobar("los bots ya juzgaron", juzgaron);
                    c.comprobar("sus caminos no se ven todavía", ocultos);
                    Recorrido mio = partida.iniciarInvestigacionHumana();
                    partida.humanoJuzga(Palabra.TE_CREO, mio);
                    partida.avanzar();
                    Juicio propio = partida.juicioVisibleDe(mesa.getHumano());
                    c.comprobar("después de juzgar se ve el camino propio", propio != null && propio.getRecorrido() == mio);
                    c.comprobar("y el de los bots", algunJuicioDeBotVisible(partida, mesa));
                    probado = true;
                } else {
                    jugarComoHumano(partida, azar);
                    partida.avanzar();
                }
            }
        }
        c.comprobar("hubo un turno para probar el minimapa", probado);
    }

    // Ayudas
    private static void jugarComoHumano(Partida partida, Random azar) {
        if (partida.humanoDebeAfirmar()) {
            Jugador humano = partida.getMesa().getHumano();
            Lista<String> otros = partida.getMesa().otrosEnJuego(humano);
            String objetivo = otros.obtener(azar.nextInt(otros.tamanio()));
            partida.humanoAfirma(new Hecho(objetivo, Rasgo.MASCOTA, Rasgo.MASCOTA.valor(azar.nextInt(5))));
        } else if (partida.humanoDebeJuzgar()) {
            Palabra palabra = azar.nextBoolean() ? Palabra.TE_CREO : Palabra.MENTIROSO;
            partida.humanoJuzga(palabra, partida.iniciarInvestigacionHumana());
        }
    }

    private static boolean algunJuicioDeBotVisible(Partida partida, Mesa mesa) {
        for (Personaje p : mesa.getPersonajes()) {
            if (p != mesa.getHumano() && partida.juicioVisibleDe(p) != null) {
                return true;
            }
        }
        return false;
    }

    // Eva (humana, Sincera), Ana y Bruno Mentirosos, Carla y Dante Sinceros
    private static Mesa mesaFija(int primerTurno) {
        Jugador eva = new Jugador("Eva", new Ficha("un pez", "pádel", "los tacos", "Córdoba"), new Sincero());
        Lista<Personaje> sentados = new ListaDoble<>();
        sentados.agregar(eva);
        sentados.agregar(new Bot("Ana", new Ficha("un gato", "fútbol", "la pizza", "Salta"), new Mentiroso()));
        sentados.agregar(new Bot("Bruno", new Ficha("un perro", "tenis", "el asado", "Rosario"), new Mentiroso()));
        sentados.agregar(new Bot("Carla", new Ficha("un loro", "vóley", "el sushi", "Resistencia"), new Sincero()));
        sentados.agregar(new Bot("Dante", new Ficha("una tortuga", "básquet", "las empanadas", "Corrientes"),
                new Sincero()));
        return new Mesa(sentados, eva, primerTurno);
    }

    // Ana afirma algo de Bruno y Carla juzga
    private static Resultado resultado(Mesa mesa, boolean eraVerdad, Palabra deCarla) {
        String valor = eraVerdad ? "un perro" : "un pez";
        Resultado r = new Resultado(new Afirmacion("Ana", "Bruno", Rasgo.MASCOTA, valor, false), eraVerdad);
        r.registrar("Carla", deCarla);
        return r;
    }
}
