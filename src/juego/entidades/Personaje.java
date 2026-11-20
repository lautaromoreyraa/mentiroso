package juego.entidades;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.modelo.Ficha;
import juego.modelo.Hecho;
import juego.modelo.Palabra;
import juego.modelo.Rasgo;
import juego.modelo.Sospecha;

public abstract class Personaje {

    // Constantes
    public static final int VIDAS_INICIALES = 3;

    // Atributos
    private final String nombre;
    private final Ficha ficha;
    private final Rol rol;
    private int vidas = VIDAS_INICIALES;
    private int puntaje;
    private final ListaDoble<Hecho> privados = new ListaDoble<>();
    private final ListaDoble<Hecho> publicos = new ListaDoble<>();
    private final ListaDoble<String> pescados = new ListaDoble<>();

    // Constructor
    protected Personaje(String nombre, Ficha ficha, Rol rol) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("el personaje necesita un nombre");
        }
        if (ficha == null || rol == null) {
            throw new IllegalArgumentException("el personaje necesita ficha y rol");
        }
        this.nombre = nombre;
        this.ficha = ficha;
        this.rol = rol;
    }

    // Decisiones: cada subclase decide a su manera
    public abstract boolean decideSolo();

    public abstract Hecho afirmar(Lista<String> otros, Random azar);

    public abstract Palabra juzgar(Sospecha sospecha);

    // Identidad
    public String getNombre() {
        return nombre;
    }

    public Ficha getFicha() {
        return ficha;
    }

    public Rol getRol() {
        return rol;
    }

    // Vidas y puntaje
    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        if (vidas < 0 || vidas > VIDAS_INICIALES) {
            throw new IllegalArgumentException("las vidas van de 0 a " + VIDAS_INICIALES + ": " + vidas);
        }
        this.vidas = vidas;
    }

    public void perderVida() {
        if (vidas > 0) {
            setVidas(vidas - 1);
        }
    }

    public boolean estaEnJuego() {
        return vidas > 0;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void sumarPuntos(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException("los puntos se suman, no se restan: " + puntos);
        }
        puntaje += puntos;
    }

    // Lo que aprende durante la partida
    public void aprender(Hecho hecho) {
        if (hecho == null || hecho.getSujeto().equals(nombre)) {
            throw new IllegalArgumentException("un hecho privado tiene que ser sobre otro");
        }
        if (!privados.contiene(hecho)) {
            privados.agregar(hecho);
        }
    }

    public void enterarse(Hecho publico) {
        if (publico == null) {
            throw new IllegalArgumentException("el hecho público no puede ser nulo");
        }
        if (!publicos.contiene(publico)) {
            publicos.agregar(publico);
        }
    }

    public void enterarseDeMentira(String mentiroso) {
        if (mentiroso == null) {
            throw new IllegalArgumentException("falta quién mintió");
        }
        pescados.agregar(mentiroso);
    }

    // Lo que recuerda, según su rol
    public Hecho recuerda(String sujeto, Rasgo rasgo) {
        if (nombre.equals(sujeto)) {
            return new Hecho(nombre, rasgo, ficha.valor(rasgo));
        }
        for (Hecho h : privados) {
            if (h.esSobre(sujeto, rasgo)) {
                return h;
            }
        }
        for (int i = publicos.tamanio() - 1; i >= 0; i--) {
            Hecho h = publicos.obtener(i);
            if (rol.recuerdaPublico(publicos.tamanio() - 1 - i) && h.esSobre(sujeto, rasgo)) {
                return h;
            }
        }
        return null;
    }

    public String quienTiene(Rasgo rasgo, String valor) {
        if (ficha.tiene(rasgo, valor)) {
            return nombre;
        }
        for (Hecho h : privados) {
            if (h.getRasgo() == rasgo && h.getValor().equals(valor)) {
                return h.getSujeto();
            }
        }
        for (int i = publicos.tamanio() - 1; i >= 0; i--) {
            Hecho h = publicos.obtener(i);
            boolean recordado = rol.recuerdaPublico(publicos.tamanio() - 1 - i);
            if (recordado && h.getRasgo() == rasgo && h.getValor().equals(valor)) {
                return h.getSujeto();
            }
        }
        return null;
    }

    public int vecesPescado(String quien) {
        int veces = 0;
        for (String nombrePescado : pescados) {
            if (nombrePescado.equals(quien)) {
                veces++;
            }
        }
        return veces;
    }

    public boolean esDeduciblePublicamente(Hecho afirmado) {
        for (Hecho h : publicos) {
            if (h.esSobre(afirmado.getSujeto(), afirmado.getRasgo())) {
                return true;
            }
            if (h.getRasgo() == afirmado.getRasgo() && h.getValor().equals(afirmado.getValor())) {
                return true;
            }
        }
        return false;
    }

    public Lista<Hecho> hechosSobre(Lista<String> sujetos) {
        Lista<Hecho> salida = new ListaDoble<>();
        for (Hecho h : privados) {
            if (sujetos.indiceDe(h.getSujeto()) >= 0) {
                salida.agregar(h);
            }
        }
        for (Hecho h : publicos) {
            if (sujetos.indiceDe(h.getSujeto()) >= 0 && salida.indiceDe(h) < 0) {
                salida.agregar(h);
            }
        }
        return salida;
    }

    public boolean esPublico(Hecho hecho) {
        return publicos.contiene(hecho);
    }

    public Lista<Hecho> getPrivados() {
        return privados;
    }

    // El gesto nervioso que ven todos al hablar
    public boolean haceGesto(boolean dijoLaVerdad, Random azar) {
        double probabilidad = dijoLaVerdad ? Rol.GESTO_FALSO : Math.max(Rol.GESTO_FALSO, rol.probabilidadDeGesto());
        return azar.nextDouble() < probabilidad;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
