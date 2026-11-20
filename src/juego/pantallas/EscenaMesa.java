package juego.pantallas;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
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
    private double espera = PAUSA_INICIAL;

    // Constructor
    public EscenaMesa(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("la escena necesita una partida");
        }
        this.partida = partida;
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
        VistaMesa.dibujar(g, partida, null);

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
