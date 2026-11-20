package juego.pantallas;

import estructuras.Lista;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import juego.entidades.Personaje;
import juego.reglas.Partida;
import juego.reglas.Puntuacion;
import motor.Escena;
import ui.Texto;

public class EscenaFin extends Escena {

    // Atributos
    private final Partida partida;
    private final int aciertos;

    // Constructor
    public EscenaFin(Partida partida, int aciertos) {
        if (partida == null) {
            throw new IllegalArgumentException("la escena necesita una partida");
        }
        if (aciertos < 0 || aciertos > partida.getMesa().cantidad() - 1) {
            throw new IllegalArgumentException("cantidad de aciertos inválida: " + aciertos);
        }
        this.partida = partida;
        this.aciertos = aciertos;
    }

    // Actualización
    @Override
    public void actualizar(double dt) {
        if (getEntrada().fuePresionada(KeyEvent.VK_ENTER)) {
            getContexto().irA(new EscenaMenu());
        }
        if (getEntrada().fuePresionada(KeyEvent.VK_ESCAPE)) {
            getContexto().salir();
        }
    }

    // Dibujo: puntaje, ranking y roles revelados
    @Override
    public void dibujar(Graphics2D g) {
        int ancho = getContexto().getAncho();
        int alto = getContexto().getAlto();
        g.setColor(Paleta.FONDO);
        g.fillRect(0, 0, ancho, alto);

        Personaje vos = partida.getMesa().getHumano();
        Lista<Personaje> ranking = partida.getMesa().ranking();
        int lugar = ranking.indiceDe(vos) + 1;
        Texto.centrado(g, "Terminaste en el puesto " + lugar + " de " + ranking.tamanio(), ancho / 2.0, 90, 22,
                Paleta.TEXTO_SUAVE);
        Texto.centrado(g, vos.getPuntaje() + " puntos", ancho / 2.0, 150, 56, Paleta.ACENTO);
        Texto.centrado(g, "Adivinaste " + aciertos + " de " + (ranking.tamanio() - 1) + " roles (+"
                + aciertos * Puntuacion.ROL_ADIVINADO + ")", ancho / 2.0, 190, 18, Paleta.TEXTO);

        double x = ancho / 2.0 - 330;
        Hud.panel(g, x - 20, 230, 700, 60 + ranking.tamanio() * 52);
        Texto.izquierda(g, "Puesto", x, 266, 14, Paleta.TEXTO_TENUE);
        Texto.izquierda(g, "Jugador", x + 90, 266, 14, Paleta.TEXTO_TENUE);
        Texto.izquierda(g, "Rol", x + 290, 266, 14, Paleta.TEXTO_TENUE);
        Texto.derecha(g, "Vidas", x + 560, 266, 14, Paleta.TEXTO_TENUE);
        Texto.derecha(g, "Puntos", x + 650, 266, 14, Paleta.TEXTO_TENUE);
        for (int i = 0; i < ranking.tamanio(); i++) {
            Personaje p = ranking.obtener(i);
            double y = 310 + i * 52;
            int asiento = partida.getMesa().asientoDe(p);
            String nombre = p == vos ? p.getNombre() + " (vos)" : p.getNombre();
            Texto.izquierda(g, (i + 1) + "°", x, y, 20, Paleta.TEXTO_SUAVE);
            Texto.izquierda(g, nombre, x + 90, y, 20, Paleta.dePersonaje(asiento));
            Texto.izquierda(g, p.getRol().getNombre(), x + 290, y, 20, Paleta.TEXTO);
            Texto.derecha(g, String.valueOf(p.getVidas()), x + 560, y, 20, Paleta.TEXTO_SUAVE);
            Texto.derecha(g, String.valueOf(p.getPuntaje()), x + 650, y, 20, p == vos ? Paleta.ACENTO : Paleta.TEXTO);
        }

        Hud.controles(g, "ENTER: volver al menú     ESC: salir", ancho, alto);
    }
}
