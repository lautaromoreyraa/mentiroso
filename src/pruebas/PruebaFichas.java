package pruebas;

import estructuras.Lista;
import estructuras.ListaDoble;
import java.util.Random;
import juego.modelo.Afirmacion;
import juego.modelo.Ficha;
import juego.modelo.Hecho;
import juego.modelo.Rasgo;
import juego.modelo.Sospecha;
import juego.reglas.Repartidor;

public class PruebaFichas {

    // Punto de entrada
    public static void main(String[] args) {
        Comprobador c = new Comprobador("Fichas y afirmaciones");

        for (int semilla = 0; semilla < 50; semilla++) {
            Lista<Ficha> fichas = new Repartidor(new Random(semilla)).repartirFichas(5);
            boolean unicas = true;
            for (Rasgo rasgo : Rasgo.todos()) {
                Lista<String> vistos = new ListaDoble<>();
                for (int i = 0; i < fichas.tamanio(); i++) {
                    String valor = fichas.obtener(i).valor(rasgo);
                    if (vistos.indiceDe(valor) >= 0) {
                        unicas = false;
                    }
                    vistos.agregar(valor);
                }
            }
            c.comprobar("semilla " + semilla + ": cada valor le toca a una sola ficha", unicas);
        }

        c.esperarExcepcion("repartir más fichas que valores", IllegalArgumentException.class,
                () -> new Repartidor(new Random(1)).repartirFichas(6));
        c.esperarExcepcion("una ficha con un valor de otro rasgo", IllegalArgumentException.class,
                () -> new Ficha("fútbol", "fútbol", "la pizza", "Salta"));

        Ficha carla = new Ficha("un gato", "tenis", "el sushi", "Rosario");
        Afirmacion verdad = new Afirmacion("Bruno", "Carla", Rasgo.MASCOTA, "un gato", false);
        Afirmacion mentira = new Afirmacion("Bruno", new Hecho("Carla", Rasgo.CIUDAD, "Salta"), true);
        c.comprobar("una afirmación que coincide con la ficha es verdad", verdad.esVerdadSegun(carla));
        c.comprobar("una afirmación que no coincide es mentira", !mentira.esVerdadSegun(carla));
        c.comprobar("texto de la afirmación", verdad.toString(), "Carla tiene un gato");
        c.comprobar("el gesto queda registrado", mentira.tieneGesto());
        c.esperarExcepcion("afirmar sobre uno mismo", IllegalArgumentException.class,
                () -> new Afirmacion("Carla", "Carla", Rasgo.MASCOTA, "un gato", false));

        c.comprobar("hechos iguales por contenido",
                new Hecho("Ana", Rasgo.COMIDA, "el asado").equals(new Hecho("Ana", Rasgo.COMIDA, "el asado")));
        c.comprobar("probabilidad de mentira de una sospecha de verdad al 65%",
                Math.abs(new Sospecha(false, 0.65).probabilidadDeMentira() - 0.35) < 1e-9);
        c.esperarExcepcion("confianza menor a 0,5", IllegalArgumentException.class, () -> new Sospecha(true, 0.3));

        c.terminar();
    }
}
