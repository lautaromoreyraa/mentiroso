package juego.pantallas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import juego.entidades.Analitico;
import juego.entidades.Mentiroso;
import juego.entidades.Personaje;
import juego.entidades.Rol;
import juego.entidades.Sincero;
import juego.reglas.Partida;
import juego.reglas.Puntuacion;
import motor.Entrada;
import motor.Escena;
import ui.Texto;

public class EscenaAdivinarRoles extends Escena {

    // Constantes
    private static final int SIN_ELEGIR = -1;

    // Atributos
    private final Partida partida;
    private final Lista<Personaje> otros = new ListaDoble<>();
    private final Lista<Rol> roles = new ListaDoble<>();
    private final Lista<Integer> apuestas = new ListaDoble<>();
    private int fila;

    // Constructor
    public EscenaAdivinarRoles(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("la escena necesita una partida");
        }
        this.partida = partida;
        for (Personaje p : partida.getMesa().getPersonajes()) {
            if (p != partida.getMesa().getHumano()) {
                otros.agregar(p);
                apuestas.agregar(SIN_ELEGIR);
            }
        }
        roles.agregar(new Mentiroso());
        roles.agregar(new Sincero());
        roles.agregar(new Analitico());
    }

    // Actualización
    @Override
    public void actualizar(double dt) {
        Entrada e = getEntrada();
        if (e.fuePresionada(KeyEvent.VK_DOWN)) {
            fila = (fila + 1) % otros.tamanio();
        }
        if (e.fuePresionada(KeyEvent.VK_UP)) {
            fila = (fila + otros.tamanio() - 1) % otros.tamanio();
        }
        if (e.fuePresionada(KeyEvent.VK_RIGHT)) {
            cambiar(1);
        }
        if (e.fuePresionada(KeyEvent.VK_LEFT)) {
            cambiar(roles.tamanio() - 1);
        }
        if (e.fuePresionada(KeyEvent.VK_ENTER) && todasElegidas()) {
            int aciertos = 0;
            for (int i = 0; i < otros.tamanio(); i++) {
                if (partida.puntuarAdivinanza(otros.obtener(i), roles.obtener(apuestas.obtener(i)))) {
                    aciertos++;
                }
            }
            getContexto().irA(new EscenaFin(partida, aciertos));
        }
        if (e.fuePresionada(KeyEvent.VK_ESCAPE)) {
            getContexto().irA(new EscenaMenu());
        }
    }

    // Rota el rol apostado en la fila elegida
    private void cambiar(int salto) {
        int actual = apuestas.obtener(fila);
        int siguiente = actual == SIN_ELEGIR ? 0 : (actual + salto) % roles.tamanio();
        apuestas.eliminar(fila);
        apuestas.insertar(fila, siguiente);
    }

    private boolean todasElegidas() {
        return apuestas.indiceDe(SIN_ELEGIR) < 0;
    }

    // Cuántos de cada rol quedan entre los otros cuatro
    private String quedan() {
        Rol propio = partida.getMesa().getHumano().getRol();
        int mentirosos = 2 - (propio.esMismoRolQue(roles.obtener(0)) ? 1 : 0);
        int sinceros = 2 - (propio.esMismoRolQue(roles.obtener(1)) ? 1 : 0);
        int analiticos = 1 - (propio.esMismoRolQue(roles.obtener(2)) ? 1 : 0);
        return "Vos sos " + propio.getNombre() + ": entre ellos hay " + mentirosos + " Mentiroso"
                + (mentirosos == 1 ? "" : "s") + ", " + sinceros + " Sincero" + (sinceros == 1 ? "" : "s") + " y "
                + analiticos + " Analítico" + (analiticos == 1 ? "" : "s") + ".";
    }

    // Dibujo
    @Override
    public void dibujar(Graphics2D g) {
        int ancho = getContexto().getAncho();
        int alto = getContexto().getAlto();
        g.setColor(Paleta.FONDO);
        g.fillRect(0, 0, ancho, alto);

        Texto.centrado(g, "¿Quién era quién?", ancho / 2.0, 100, 40, Paleta.ACENTO);
        Texto.centrado(g, "Cada rol adivinado suma " + Puntuacion.ROL_ADIVINADO + " puntos. " + quedan(), ancho / 2.0,
                140, 16, Paleta.TEXTO_SUAVE);

        double x = ancho / 2.0 - 300;
        for (int i = 0; i < otros.tamanio(); i++) {
            double y = 220 + i * 80;
            boolean activa = i == fila;
            Hud.panel(g, x, y - 34, 600, 60);
            Personaje p = otros.obtener(i);
            int asiento = partida.getMesa().asientoDe(p);
            Texto.izquierda(g, p.getNombre(), x + 24, y + 4, 22, Paleta.dePersonaje(asiento));
            int apuesta = apuestas.obtener(i);
            String texto = apuesta == SIN_ELEGIR ? "?" : roles.obtener(apuesta).getNombre();
            Texto.derecha(g, (activa ? "←  " : "") + texto + (activa ? "  →" : ""), x + 576, y + 4, 22,
                    activa ? Paleta.ACENTO : Paleta.TEXTO);
        }
        String pie = todasElegidas() ? "ENTER: ver resultados" : "↑ ↓: elegir persona     ← →: elegir rol";
        Hud.controles(g, pie, ancho, alto);
    }
}
