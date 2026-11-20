package juego.pantallas;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import juego.entidades.Jugador;
import juego.modelo.Hecho;
import juego.modelo.Rasgo;
import juego.reglas.Partida;
import motor.Escena;
import ui.Texto;

public class EscenaRol extends Escena {

    // Atributos
    private final Partida partida;

    // Constructor
    public EscenaRol(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("la escena necesita una partida");
        }
        this.partida = partida;
    }

    // Actualización
    @Override
    public void actualizar(double dt) {
        if (getEntrada().fuePresionada(KeyEvent.VK_ENTER)) {
            getContexto().irA(new EscenaMesa(partida));
        }
        if (getEntrada().fuePresionada(KeyEvent.VK_ESCAPE)) {
            getContexto().irA(new EscenaMenu());
        }
    }

    // Dibujo
    @Override
    public void dibujar(Graphics2D g) {
        int ancho = getContexto().getAncho();
        int alto = getContexto().getAlto();
        Jugador vos = partida.getMesa().getHumano();
        g.setColor(Paleta.FONDO);
        g.fillRect(0, 0, ancho, alto);

        Texto.centrado(g, "Te sentás a la mesa como " + vos.getNombre(), ancho / 2.0, 90, 20, Paleta.TEXTO_SUAVE);
        Texto.centrado(g, "Sos " + vos.getRol().getNombre(), ancho / 2.0, 150, 54, Paleta.ACENTO);
        Parrafo.centrado(g, vos.getRol().getObjetivo(), ancho / 2.0, 200, 18, Paleta.TEXTO, 700, false);

        double x = ancho / 2.0 - 330;
        Hud.panel(g, x - 20, 250, 700, 300);
        double y = 284;
        Texto.izquierda(g, "Tu ficha secreta", x, y, 17, Paleta.TEXTO);
        y += 26;
        for (Rasgo rasgo : Rasgo.todos()) {
            Texto.izquierda(g, rasgo.getVerbo() + " " + vos.getFicha().valor(rasgo), x + 12, y, 16, Paleta.TEXTO_SUAVE);
            y += 24;
        }
        double y2 = 284;
        double x2 = x + 340;
        Texto.izquierda(g, "Lo que sabés de otros", x2, y2, 17, Paleta.TEXTO);
        y2 += 26;
        for (int i = 0; i < vos.getPrivados().tamanio(); i++) {
            Hecho h = vos.getPrivados().obtener(i);
            y2 = Parrafo.izquierda(g, h.toString(), x2 + 12, y2, 16, Paleta.TEXTO_SUAVE, 300);
        }
        Parrafo.izquierda(g, "Cada valor le toca a una sola persona: si un valor es tuyo, nadie más lo tiene.",
                x, 440, 15, Paleta.TEXTO_SUAVE, 640);
        Parrafo.izquierda(g, "En la mesa hay 2 Mentirosos, 2 Sinceros y 1 Analítico. Al final vas a tener que "
                + "adivinar el rol de cada uno.", x, 490, 15, Paleta.TEXTO_SUAVE, 640);

        Texto.centrado(g, "ENTER: sentarse a la mesa", ancho / 2.0, alto - 70, 20, Paleta.TEXTO);
    }
}
