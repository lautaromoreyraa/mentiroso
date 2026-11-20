package juego.reglas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.arbol.ArbolDeSospecha;
import juego.arbol.Investigacion;
import juego.arbol.Recorrido;
import juego.entidades.Jugador;
import juego.entidades.Personaje;
import juego.entidades.Rol;
import juego.modelo.Afirmacion;
import juego.modelo.Hecho;
import juego.modelo.Palabra;
import juego.modelo.Resultado;

public class Partida {

    // Constantes
    public static final int VUELTAS = 3;
    public static final int AFIRMACIONES_TOTALES = VUELTAS * Repartidor.JUGADORES;
    public static final int MINIMO_EN_JUEGO = 3;

    // Fases del turno
    public enum Fase {
        INICIO, HABLANDO, JUZGANDO, RESUELTO, TERMINADA
    }

    // Atributos
    private final Mesa mesa;
    private final Random azar;
    private final ArbolDeSospecha arbol = new ArbolDeSospecha();
    private Fase fase = Fase.INICIO;
    private int afirmacionesHechas;
    private Personaje hablante;
    private Afirmacion afirmacion;
    private Resultado resultado;
    private Hecho revelado;
    private ListaDoble<Personaje> jueces = new ListaDoble<>();
    private ListaDoble<Juicio> juicios = new ListaDoble<>();
    private Recorrido recorridoHumano;

    // Constructor
    public Partida(Mesa mesa, Random azar) {
        if (mesa == null || azar == null) {
            throw new IllegalArgumentException("la partida necesita mesa y azar");
        }
        this.mesa = mesa;
        this.azar = azar;
    }

    // Avance: hace todo lo que puede sin el humano y se detiene cuando lo necesita
    public void avanzar() {
        switch (fase) {
            case INICIO:
            case RESUELTO:
                empezarTurno();
                break;
            case HABLANDO:
                tomarAfirmacion();
                break;
            case JUZGANDO:
                tomarJuicios();
                break;
            default:
                break;
        }
    }

    // Decisiones del humano, cargadas desde las pantallas
    public void humanoAfirma(Hecho elegida) {
        if (!humanoDebeAfirmar()) {
            throw new IllegalStateException("no es el turno del humano para afirmar");
        }
        mesa.getHumano().setAfirmacion(elegida);
    }

    public Recorrido iniciarInvestigacionHumana() {
        if (!humanoDebeJuzgar()) {
            throw new IllegalStateException("el humano no tiene que juzgar ahora");
        }
        return arbol.iniciar(new Investigacion(mesa.getHumano(), afirmacion));
    }

    public void humanoJuzga(Palabra palabra, Recorrido recorrido) {
        if (!humanoDebeJuzgar()) {
            throw new IllegalStateException("el humano no tiene que juzgar ahora");
        }
        if (recorrido == null || recorrido.getInvestigacion().getEvaluador() != mesa.getHumano()) {
            throw new IllegalArgumentException("el recorrido tiene que ser el del humano");
        }
        recorrido.completar();
        recorridoHumano = recorrido;
        mesa.getHumano().setPalabra(palabra);
    }

    public boolean puntuarAdivinanza(Personaje adivinado, Rol apuesta) {
        if (adivinado == null || adivinado == mesa.getHumano()) {
            throw new IllegalArgumentException("se adivina el rol de otro personaje");
        }
        boolean acerto = adivinado.getRol().esMismoRolQue(apuesta);
        if (acerto) {
            mesa.getHumano().sumarPuntos(Puntuacion.ROL_ADIVINADO);
        }
        return acerto;
    }

    // Qué espera la partida
    public boolean humanoDebeAfirmar() {
        Jugador humano = mesa.getHumano();
        return fase == Fase.HABLANDO && hablante == humano && !humano.listoParaAfirmar();
    }

    public boolean humanoDebeJuzgar() {
        Jugador humano = mesa.getHumano();
        return fase == Fase.JUZGANDO && jueces.contiene(humano) && juicioDe(humano) == null
                && !humano.listoParaJuzgar();
    }

    public boolean esperaAlHumano() {
        return humanoDebeAfirmar() || humanoDebeJuzgar();
    }

    // Consultas
    public Fase getFase() {
        return fase;
    }

    public boolean estaTerminada() {
        return fase == Fase.TERMINADA;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public ArbolDeSospecha getArbol() {
        return arbol;
    }

    public Personaje getHablante() {
        return hablante;
    }

    public Afirmacion getAfirmacion() {
        return afirmacion;
    }

    public Resultado getResultado() {
        return fase == Fase.RESUELTO || fase == Fase.TERMINADA ? resultado : null;
    }

    public Hecho getRevelado() {
        return revelado;
    }

    public int getAfirmacionesHechas() {
        return afirmacionesHechas;
    }

    public boolean esJuez(Personaje p) {
        return jueces.contiene(p);
    }

    public Juicio juicioDe(Personaje p) {
        for (Juicio j : juicios) {
            if (j.getJuez() == p) {
                return j;
            }
        }
        return null;
    }

    // Lo que se puede mostrar: los juicios se ven recién cuando juzgaron todos
    public Juicio juicioVisibleDe(Personaje p) {
        if (fase != Fase.RESUELTO && fase != Fase.TERMINADA) {
            return p == mesa.getHumano() ? juicioDe(p) : null;
        }
        return juicioDe(p);
    }

    // Fase INICIO o RESUELTO: termina la partida o pasa al hablante siguiente
    private void empezarTurno() {
        if (debeTerminar()) {
            fase = Fase.TERMINADA;
            return;
        }
        hablante = mesa.siguienteHablante();
        afirmacion = null;
        resultado = null;
        revelado = null;
        jueces = new ListaDoble<>();
        juicios = new ListaDoble<>();
        recorridoHumano = null;
        fase = Fase.HABLANDO;
    }

    private boolean debeTerminar() {
        return afirmacionesHechas >= AFIRMACIONES_TOTALES || !mesa.getHumano().estaEnJuego()
                || mesa.enJuego() < MINIMO_EN_JUEGO;
    }

    // Fase HABLANDO: el hablante afirma y se le escapa, o no, un gesto
    private void tomarAfirmacion() {
        if (!hablante.listoParaAfirmar()) {
            return;
        }
        Hecho dicho = hablante.afirmar(mesa.otrosEnJuego(hablante), azar);
        Personaje objetivo = mesa.buscar(dicho.getSujeto());
        boolean esVerdad = objetivo.getFicha().tiene(dicho.getRasgo(), dicho.getValor());
        afirmacion = new Afirmacion(hablante.getNombre(), dicho, hablante.haceGesto(esVerdad, azar));
        mesa.registrar(afirmacion);
        resultado = new Resultado(afirmacion, esVerdad);
        Lista<Personaje> elegidos = mesa.jueces(afirmacion);
        for (int i = 0; i < elegidos.tamanio(); i++) {
            jueces.agregar(elegidos.obtener(i));
        }
        fase = Fase.JUZGANDO;
    }

    // Fase JUZGANDO: juzgan los bots; el humano, cuando eligió
    private void tomarJuicios() {
        for (Personaje juez : jueces) {
            if (juicioDe(juez) != null || !juez.listoParaJuzgar()) {
                continue;
            }
            Recorrido recorrido = juez == mesa.getHumano() ? recorridoHumano
                    : arbol.recorrer(new Investigacion(juez, afirmacion));
            Palabra palabra = juez.juzgar(recorrido.getSospecha());
            juicios.agregar(new Juicio(juez, recorrido, palabra));
            resultado.registrar(juez.getNombre(), palabra);
        }
        if (juicios.tamanio() == jueces.tamanio()) {
            resolver();
        }
    }

    // Puntaje, revelación y mentira pescada
    private void resolver() {
        Puntuacion.aplicar(resultado, mesa);
        if (resultado.huboAcusacion()) {
            Personaje objetivo = mesa.buscar(afirmacion.getObjetivo());
            revelado = new Hecho(objetivo.getNombre(), afirmacion.getRasgo(),
                    objetivo.getFicha().valor(afirmacion.getRasgo()));
            mesa.revelar(revelado);
            if (!resultado.eraVerdad()) {
                mesa.anunciarMentira(afirmacion.getHablante());
            }
        }
        afirmacionesHechas++;
        fase = Fase.RESUELTO;
    }
}
