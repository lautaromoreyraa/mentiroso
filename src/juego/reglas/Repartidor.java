package juego.reglas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.modelo.Ficha;
import juego.modelo.Rasgo;

public class Repartidor {

    // Atributos
    private final Random azar;

    // Constructor
    public Repartidor(Random azar) {
        if (azar == null) {
            throw new IllegalArgumentException("el repartidor necesita una fuente de azar");
        }
        this.azar = azar;
    }

    // Fichas: cada valor de cada rasgo le toca a una sola persona
    public Lista<Ficha> repartirFichas(int cantidad) {
        if (cantidad < 1 || cantidad > Rasgo.VALORES_POR_RASGO) {
            throw new IllegalArgumentException("se reparten de 1 a " + Rasgo.VALORES_POR_RASGO + " fichas");
        }
        Lista<String> mascotas = Rasgo.MASCOTA.copiaDeValores();
        Lista<String> deportes = Rasgo.DEPORTE.copiaDeValores();
        Lista<String> comidas = Rasgo.COMIDA.copiaDeValores();
        Lista<String> ciudades = Rasgo.CIUDAD.copiaDeValores();
        Lista<Ficha> fichas = new ListaDoble<>();
        for (int i = 0; i < cantidad; i++) {
            fichas.agregar(new Ficha(sacar(mascotas), sacar(deportes), sacar(comidas), sacar(ciudades)));
        }
        return fichas;
    }

    // Elección al azar sin reposición
    public <T> T sacar(Lista<T> lista) {
        if (lista.estaVacia()) {
            throw new IllegalStateException("no queda nada para sacar");
        }
        return lista.eliminar(azar.nextInt(lista.tamanio()));
    }
}
