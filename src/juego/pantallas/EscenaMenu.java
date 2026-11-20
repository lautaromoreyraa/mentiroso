package juego.pantallas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Random;
import juego.reglas.Mesa;
import juego.reglas.Partida;
import juego.reglas.Repartidor;
import motor.Escena;
import ui.Texto;

public class EscenaMenu extends Escena {

    // Constantes
    public static final int ASIENTO_HUMANO = 0;

    // Actualización
    @Override
    public void actualizar(double dt) {
        if (getEntrada().fuePresionada(KeyEvent.VK_ENTER)) {
            Random azar = new Random();
            Mesa mesa = new Repartidor(azar).armarMesa(nombres(), ASIENTO_HUMANO);
            getContexto().irA(new EscenaRol(new Partida(mesa, azar)));
        }
        if (getEntrada().fuePresionada(KeyEvent.VK_ESCAPE)) {
            getContexto().salir();
        }
    }

    // Los cinco de la mesa: el primero es el humano
    public static Lista<String> nombres() {
        Lista<String> nombres = new ListaDoble<>();
        nombres.agregar("Eli");
        nombres.agregar("Ana");
        nombres.agregar("Bruno");
        nombres.agregar("Carla");
        nombres.agregar("Dante");
        return nombres;
    }

    // Dibujo
    @Override
    public void dibujar(Graphics2D g) {
        int ancho = getContexto().getAncho();
        int alto = getContexto().getAlto();
        g.setColor(Paleta.FONDO);
        g.fillRect(0, 0, ancho, alto);

        Texto.centrado(g, "MENTIROSO", ancho / 2.0, 150, 72, Paleta.ACENTO);
        Texto.centrado(g, "Cinco en la mesa, cuatro rasgos cada uno y un árbol para saber quién miente",
                ancho / 2.0, 196, 18, Paleta.TEXTO_SUAVE);

        double x = ancho / 2.0 - 320;
        double y = 270;
        Hud.panel(g, x - 24, y - 36, 688, 240);
        y = Parrafo.izquierda(g, "Cada uno tiene una ficha secreta: mascota, deporte, comida y ciudad. Cada valor le "
                + "toca a una sola persona de la mesa.", x, y, 16, Paleta.TEXTO, 640);
        y = Parrafo.izquierda(g, "Por turno, alguien afirma algo de otro jugador. Los demás dicen «Te creo» o "
                + "«¡Mentiroso!», recorriendo un árbol de decisión con lo que saben.", x, y + 8, 16, Paleta.TEXTO, 640);
        y = Parrafo.izquierda(g, "En la mesa hay 2 Mentirosos, 2 Sinceros y 1 Analítico. A vos te toca un rol al "
                + "azar, y los demás roles son secretos hasta el final.", x, y + 8, 16, Paleta.TEXTO, 640);
        Parrafo.izquierda(g, "Arriba a la derecha, el minimapa muestra el camino que siguió cada uno en su árbol. "
                + "No dice si acertó: eso lo descubrís vos.", x, y + 8, 16, Paleta.TEXTO_SUAVE, 640);

        Texto.centrado(g, "ENTER: jugar     ESC: salir", ancho / 2.0, alto - 70, 20, Paleta.TEXTO);
    }
}
