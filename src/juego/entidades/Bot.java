package juego.entidades;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.modelo.Ficha;
import juego.modelo.Hecho;
import juego.modelo.Palabra;
import juego.modelo.Rasgo;
import juego.modelo.Sospecha;

public class Bot extends Personaje {

    // Constantes
    private static final int INTENTOS_DE_MENTIRA = 12;

    // Constructor
    public Bot(String nombre, Ficha ficha, Rol rol) {
        super(nombre, ficha, rol);
    }

    // Decide sin esperar a nadie
    @Override
    public boolean decideSolo() {
        return true;
    }

    @Override
    public boolean listoParaAfirmar() {
        return true;
    }

    @Override
    public boolean listoParaJuzgar() {
        return true;
    }

    // Afirma según su rol: miente o cuenta algo que sabe
    @Override
    public Hecho afirmar(Lista<String> otros, Random azar) {
        if (otros == null || otros.estaVacia()) {
            throw new IllegalArgumentException("no hay sobre quién afirmar");
        }
        if (azar.nextDouble() < getRol().probabilidadDeMentir()) {
            Hecho mentira = inventarMentira(otros, azar);
            if (mentira != null) {
                return mentira;
            }
        }
        return contarAlgoQueSabe(otros, azar);
    }

    // Juzga comparando la sospecha con el umbral de su rol
    @Override
    public Palabra juzgar(Sospecha sospecha) {
        if (sospecha == null) {
            throw new IllegalArgumentException("para juzgar hace falta una sospecha");
        }
        return sospecha.probabilidadDeMentira() >= getRol().umbralDeAcusacion() ? Palabra.MENTIROSO : Palabra.TE_CREO;
    }

    // Una mentira: con astucia, le adjudica a otro un valor propio, que sólo él sabe falso
    private Hecho inventarMentira(Lista<String> otros, Random azar) {
        for (int intento = 0; intento < INTENTOS_DE_MENTIRA; intento++) {
            String objetivo = otros.obtener(azar.nextInt(otros.tamanio()));
            Rasgo rasgo = Rasgo.todos().obtener(azar.nextInt(Rasgo.todos().tamanio()));
            Hecho sabido = recuerda(objetivo, rasgo);
            String valor = azar.nextDouble() < getRol().astucia() ? getFicha().valor(rasgo)
                    : rasgo.valor(azar.nextInt(rasgo.cantidadDeValores()));
            if (sabido != null && sabido.getValor().equals(valor)) {
                continue;
            }
            Hecho mentira = new Hecho(objetivo, rasgo, valor);
            if (getRol().mentiraAceptable(this, mentira)) {
                return mentira;
            }
        }
        return null;
    }

    // Una verdad: prefiere lo que sabe en privado, que la mesa todavía no conoce
    private Hecho contarAlgoQueSabe(Lista<String> otros, Random azar) {
        Lista<Hecho> conocidos = hechosSobre(otros);
        Lista<Hecho> ineditos = new ListaDoble<>();
        for (int i = 0; i < conocidos.tamanio(); i++) {
            if (!esPublico(conocidos.obtener(i))) {
                ineditos.agregar(conocidos.obtener(i));
            }
        }
        Lista<Hecho> fuente = ineditos.estaVacia() ? conocidos : ineditos;
        if (!fuente.estaVacia()) {
            return fuente.obtener(azar.nextInt(fuente.tamanio()));
        }
        String objetivo = otros.obtener(azar.nextInt(otros.tamanio()));
        Rasgo rasgo = Rasgo.todos().obtener(azar.nextInt(Rasgo.todos().tamanio()));
        return new Hecho(objetivo, rasgo, rasgo.valor(azar.nextInt(rasgo.cantidadDeValores())));
    }
}
