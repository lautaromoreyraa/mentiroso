package juego.pantallas;

import estructuras.Lista;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import juego.arbol.Recorrido;
import juego.modelo.Afirmacion;
import juego.modelo.Palabra;
import juego.reglas.Partida;
import motor.Entrada;
import motor.Escena;
import motor.Temporizador;
import ui.Texto;

public class EscenaInvestigacion extends Escena {

    // Constantes
    private static final double PASO_AUTOMATICO = 0.9;

    // Atributos
    private final Partida partida;
    private final EscenaMesa volver;
    private final Recorrido recorrido;
    private final VistaArbol vista;
    private final Temporizador reloj = new Temporizador(PASO_AUTOMATICO);
    private boolean automatico = true;

    // Constructor
    public EscenaInvestigacion(Partida partida, EscenaMesa volver) {
        if (partida == null || volver == null) {
            throw new IllegalArgumentException("la escena necesita partida y mesa");
        }
        this.partida = partida;
        this.volver = volver;
        this.recorrido = partida.iniciarInvestigacionHumana();
        this.vista = new VistaArbol(partida.getArbol().getArbol());
    }

    // Actualización: el árbol se abre solo, o paso a paso con las flechas
    @Override
    public void actualizar(double dt) {
        Entrada e = getEntrada();
        if (e.fuePresionada(KeyEvent.VK_RIGHT)) {
            automatico = false;
            recorrido.avanzar();
        }
        if (e.fuePresionada(KeyEvent.VK_LEFT)) {
            automatico = false;
            recorrido.retroceder();
        }
        if (automatico && reloj.avanzar(dt)) {
            recorrido.avanzar();
        }
        if (recorrido.estaTerminado()) {
            if (e.fuePresionada(KeyEvent.VK_V)) {
                decidir(Palabra.TE_CREO);
            } else if (e.fuePresionada(KeyEvent.VK_M)) {
                decidir(Palabra.MENTIROSO);
            }
        }
        if (e.fuePresionada(KeyEvent.VK_ESCAPE)) {
            getContexto().irA(new EscenaMenu());
        }
    }

    private void decidir(Palabra palabra) {
        partida.humanoJuzga(palabra, recorrido);
        volver.continuar();
        getContexto().irA(volver);
    }

    // Dibujo
    @Override
    public void dibujar(Graphics2D g) {
        int ancho = getContexto().getAncho();
        int alto = getContexto().getAlto();
        g.setColor(Paleta.FONDO);
        g.fillRect(0, 0, ancho, alto);

        Afirmacion a = partida.getAfirmacion();
        Texto.izquierda(g, "¿Verdad o mentira?", 24, 44, 28, Paleta.ACENTO);
        Texto.izquierda(g, a.getHablante() + " dice: «" + a + "»" + (a.tieneGesto() ? "  (mira para otro lado)" : ""),
                24, 78, 19, Paleta.TEXTO);

        vista.dibujarDetallado(g, recorrido, 24, 104, ancho - 48, 400);

        Hud.panel(g, 24, 520, ancho - 48, 156);
        Lista<String> razones = recorrido.explicacion();
        double y = 548;
        for (int i = 0; i < razones.tamanio(); i++) {
            Texto.izquierda(g, (i + 1) + ".", 44, y, 14, Paleta.TEXTO_TENUE);
            Texto.izquierda(g, razones.obtener(i), 68, y, 14, i == razones.tamanio() - 1 ? Paleta.TEXTO : Paleta.TEXTO_SUAVE);
            y += 21;
        }
        if (recorrido.estaTerminado()) {
            double x = ancho - 400;
            Texto.izquierda(g, "El árbol sugiere:", x, 556, 15, Paleta.TEXTO_SUAVE);
            Texto.izquierda(g, recorrido.getSospecha().toString(), x, 590, 26, Paleta.ACENTO);
            Texto.izquierda(g, "[V] " + Palabra.TE_CREO.getOpcion() + "      [M] " + Palabra.MENTIROSO.getOpcion(), x, 640,
                    22, Paleta.TEXTO);
            Hud.controles(g, "V: Verdad     M: ¡Mentiroso!     ← →: recorrer el árbol", ancho, alto);
        } else {
            Hud.controles(g, "→: avanzar     ←: retroceder     el árbol se abre solo si no tocás nada", ancho, alto);
        }
    }
}
