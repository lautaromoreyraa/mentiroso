package juego.pantallas;

import estructuras.Lista;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import juego.entidades.Jugador;
import juego.modelo.Hecho;
import juego.modelo.Rasgo;
import juego.reglas.Partida;
import motor.Entrada;
import motor.Escena;
import ui.Texto;

public class EscenaAfirmar extends Escena {

    // Atributos
    private final Partida partida;
    private final EscenaMesa volver;
    private final Lista<String> otros;
    private int objetivo;
    private int rasgo;
    private int valor;

    // Constructor
    public EscenaAfirmar(Partida partida, EscenaMesa volver) {
        if (partida == null || volver == null) {
            throw new IllegalArgumentException("la escena necesita partida y mesa");
        }
        this.partida = partida;
        this.volver = volver;
        this.otros = partida.getMesa().otrosEnJuego(partida.getMesa().getHumano());
    }

    // Actualización
    @Override
    public void actualizar(double dt) {
        Entrada e = getEntrada();
        for (int i = 0; i < otros.tamanio(); i++) {
            if (e.fuePresionada(KeyEvent.VK_1 + i)) {
                objetivo = i;
            }
        }
        if (e.fuePresionada(KeyEvent.VK_TAB)) {
            objetivo = (objetivo + 1) % otros.tamanio();
        }
        int rasgos = Rasgo.todos().tamanio();
        if (e.fuePresionada(KeyEvent.VK_RIGHT)) {
            rasgo = (rasgo + 1) % rasgos;
        }
        if (e.fuePresionada(KeyEvent.VK_LEFT)) {
            rasgo = (rasgo + rasgos - 1) % rasgos;
        }
        if (e.fuePresionada(KeyEvent.VK_DOWN)) {
            valor = (valor + 1) % Rasgo.VALORES_POR_RASGO;
        }
        if (e.fuePresionada(KeyEvent.VK_UP)) {
            valor = (valor + Rasgo.VALORES_POR_RASGO - 1) % Rasgo.VALORES_POR_RASGO;
        }
        if (e.fuePresionada(KeyEvent.VK_ENTER)) {
            partida.humanoAfirma(elegida());
            volver.continuar();
            getContexto().irA(volver);
        }
        if (e.fuePresionada(KeyEvent.VK_ESCAPE)) {
            getContexto().irA(new EscenaMenu());
        }
    }

    // Lo elegido hasta ahora
    private Hecho elegida() {
        Rasgo r = Rasgo.todos().obtener(rasgo);
        return new Hecho(otros.obtener(objetivo), r, r.valor(valor));
    }

    // Lo que el humano sabe de lo que está por decir
    private String loQueSabes(Hecho h) {
        Jugador vos = partida.getMesa().getHumano();
        Hecho sabido = vos.recuerda(h.getSujeto(), h.getRasgo());
        if (sabido != null) {
            return sabido.getValor().equals(h.getValor()) ? "Sabés que es verdad."
                    : "Sabés que es mentira: " + sabido + ".";
        }
        String duenio = vos.quienTiene(h.getRasgo(), h.getValor());
        if (duenio != null && duenio.equals(vos.getNombre())) {
            return "Es mentira: " + h.getValor() + " es tuyo. Sólo vos sabés de quién es.";
        }
        if (duenio != null) {
            return "Es mentira: sabés que " + h.getValor() + " es de " + duenio + ".";
        }
        return "No sabés si es verdad.";
    }

    // Dibujo
    @Override
    public void dibujar(Graphics2D g) {
        int ancho = getContexto().getAncho();
        int alto = getContexto().getAlto();
        g.setColor(Paleta.FONDO);
        g.fillRect(0, 0, ancho, alto);
        Hud.dibujar(g, partida, alto - 52);

        double x = 330;
        Texto.izquierda(g, "Te toca hablar", x, 70, 34, Paleta.ACENTO);
        Texto.izquierda(g, "Afirmá algo sobre otro jugador. Puede ser verdad o mentira.", x, 104, 16,
                Paleta.TEXTO_SUAVE);

        double y = 160;
        Texto.izquierda(g, "Sobre quién (1-" + otros.tamanio() + " o TAB)", x, y, 15, Paleta.TEXTO);
        for (int i = 0; i < otros.tamanio(); i++) {
            boolean elegido = i == objetivo;
            Texto.izquierda(g, (i + 1) + " " + otros.obtener(i), x + 20 + i * 150, y + 32, 18,
                    elegido ? Paleta.ACENTO : Paleta.TEXTO_TENUE);
        }
        y += 84;
        Texto.izquierda(g, "Qué rasgo (← →)", x, y, 15, Paleta.TEXTO);
        for (int i = 0; i < Rasgo.todos().tamanio(); i++) {
            Texto.izquierda(g, Rasgo.todos().obtener(i).getNombre(), x + 20 + i * 150, y + 32, 18,
                    i == rasgo ? Paleta.ACENTO : Paleta.TEXTO_TENUE);
        }
        y += 84;
        Rasgo r = Rasgo.todos().obtener(rasgo);
        Texto.izquierda(g, "Qué valor (↑ ↓)", x, y, 15, Paleta.TEXTO);
        for (int i = 0; i < r.cantidadDeValores(); i++) {
            Texto.izquierda(g, r.valor(i), x + 20 + i * 150, y + 32, 18, i == valor ? Paleta.ACENTO : Paleta.TEXTO_TENUE);
        }

        Hecho h = elegida();
        Hud.panel(g, x - 16, 450, 820, 150);
        Texto.izquierda(g, "Vas a decir:", x, 486, 15, Paleta.TEXTO_SUAVE);
        Texto.izquierda(g, "«" + h + "»", x, 526, 28, Paleta.TEXTO);
        Texto.izquierda(g, loQueSabes(h), x, 568, 16, Paleta.ACENTO);

        Hud.controles(g, "ENTER: afirmar     ESC: menú", ancho, alto);
    }
}
