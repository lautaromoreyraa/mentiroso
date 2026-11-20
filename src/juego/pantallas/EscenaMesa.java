package juego.pantallas;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import juego.entidades.Personaje;
import juego.reglas.Partida;
import motor.Entrada;
import motor.Escena;
import ui.Texto;

public class EscenaMesa extends Escena {

    // Constantes: cuánto se ve cada momento del turno
    private static final double PAUSA_INICIAL = 0.6;
    private static final double PAUSA_HABLANDO = 1.0;
    private static final double PAUSA_JUZGANDO = 2.0;

    // Atributos
    private final Partida partida;
    private final MinimapaArbol minimapa;
    private Personaje seleccionado;
    private double espera = PAUSA_INICIAL;

    // Constructor
    public EscenaMesa(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("la escena necesita una partida");
        }
        this.partida = partida;
        this.minimapa = new MinimapaArbol(new VistaArbol(partida.getArbol().getArbol()));
        this.seleccionado = partida.getMesa().getHumano();
    }

    // Al volver de afirmar o de investigar, la partida sigue enseguida
    public void continuar() {
        espera = 0;
    }

    // Actualización
    @Override
    public void actualizar(double dt) {
        Entrada entrada = getEntrada();
        if (entrada.fuePresionada(KeyEvent.VK_ESCAPE)) {
            getContexto().irA(new EscenaMenu());
            return;
        }
        elegirPersonaje(entrada);
        boolean apurar = entrada.fuePresionada(KeyEvent.VK_SPACE) || entrada.fuePresionada(KeyEvent.VK_ENTER);

        if (partida.estaTerminada()) {
            if (apurar) {
                getContexto().irA(new EscenaAdivinarRoles(partida));
            }
            return;
        }
        if (partida.getFase() == Partida.Fase.RESUELTO) {
            if (apurar) {
                avanzarPartida();
            }
            return;
        }
        espera -= dt;
        if (espera > 0 && !apurar) {
            return;
        }
        if (partida.humanoDebeAfirmar()) {
            getContexto().irA(new EscenaAfirmar(partida, this));
            return;
        }
        if (partida.humanoDebeJuzgar()) {
            partida.avanzar();
            getContexto().irA(new EscenaInvestigacion(partida, this));
            return;
        }
        avanzarPartida();
    }

    // A quién muestra el minimapa: teclas 1 a 5, TAB o click en su asiento
    private void elegirPersonaje(Entrada entrada) {
        int cantidad = partida.getMesa().cantidad();
        for (int i = 0; i < cantidad; i++) {
            if (entrada.fuePresionada(KeyEvent.VK_1 + i)) {
                seleccionado = partida.getMesa().buscar(i);
            }
        }
        if (entrada.fuePresionada(KeyEvent.VK_TAB)) {
            int siguiente = (partida.getMesa().asientoDe(seleccionado) + 1) % cantidad;
            seleccionado = partida.getMesa().buscar(siguiente);
        }
        if (entrada.huboClick()) {
            int asiento = VistaMesa.asientoEn(entrada.getMouseX(), entrada.getMouseY());
            if (asiento >= 0) {
                seleccionado = partida.getMesa().buscar(asiento);
            }
        }
    }

    // Un paso de la partida y la pausa que le corresponde
    private void avanzarPartida() {
        partida.avanzar();
        switch (partida.getFase()) {
            case HABLANDO:
                espera = PAUSA_HABLANDO;
                break;
            case JUZGANDO:
                espera = PAUSA_JUZGANDO;
                break;
            default:
                espera = 0;
                break;
        }
    }

    // Dibujo
    @Override
    public void dibujar(Graphics2D g) {
        int ancho = getContexto().getAncho();
        int alto = getContexto().getAlto();
        g.setColor(Paleta.FONDO);
        g.fillRect(0, 0, ancho, alto);

        Hud.dibujar(g, partida, alto - 52);
        VistaMesa.dibujar(g, partida, seleccionado);
        minimapa.dibujar(g, partida, seleccionado);

        if (partida.estaTerminada()) {
            String motivo = partida.getMesa().getHumano().estaEnJuego() ? "Se terminaron las afirmaciones."
                    : "Te quedaste sin vidas.";
            Texto.centrado(g, "Fin de la partida. " + motivo, VistaMesa.CENTRO_X, 70, 22, Paleta.ACENTO);
            Hud.controles(g, "ENTER: adivinar los roles     ESC: menú", ancho, alto);
        } else if (partida.getFase() == Partida.Fase.RESUELTO) {
            Hud.controles(g, "ESPACIO: siguiente turno     ESC: menú", ancho, alto);
        } else {
            Hud.controles(g, "ESPACIO: apurar     ESC: menú", ancho, alto);
        }
    }
}
