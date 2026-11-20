package pruebas;

import estructuras.ArbolBinario;
import juego.arbol.ArbolDeSospecha;
import juego.arbol.Investigacion;
import juego.arbol.NodoDecision;
import juego.arbol.Recorrido;
import juego.entidades.Analitico;
import juego.entidades.Bot;
import juego.entidades.Personaje;
import juego.entidades.Sincero;
import juego.modelo.Afirmacion;
import juego.modelo.Ficha;
import juego.modelo.Hecho;
import juego.modelo.Rasgo;

public class PruebaCriterios {

    // Atributos compartidos por los escenarios
    private static final ArbolDeSospecha SOSPECHA = new ArbolDeSospecha();

    // Punto de entrada
    public static void main(String[] args) {
        Comprobador c = new Comprobador("Árbol de sospecha");

        formaDelArbol(c);
        cadaHojaEsAlcanzable(c);
        recorridoPasoAPaso(c);
        mismaAfirmacionDistintaMemoria(c);

        c.terminar();
    }

    // El árbol del diseño: 7 preguntas y 8 veredictos
    private static void formaDelArbol(Comprobador c) {
        ArbolBinario<NodoDecision> arbol = SOSPECHA.getArbol();
        c.comprobar("cantidad de nodos", arbol.cantidadNodos(), 15);
        c.comprobar("cantidad de hojas", arbol.cantidadHojas(), 8);
        c.comprobar("altura", arbol.altura(), 6);
        c.comprobar("la raíz pregunta si habla de mí", arbol.getRaiz().getDato().getTexto(), "¿Habla de mí?");
    }

    // Un escenario por hoja, con la sospecha que tiene que dar
    private static void cadaHojaEsAlcanzable(Comprobador c) {
        c.comprobar("habla de mí y es verdad",
                veredicto(evaluador(), "Bruno", "Ana", Rasgo.MASCOTA, "un gato", false), "VERDAD (100%)");
        c.comprobar("habla de mí y es mentira",
                veredicto(evaluador(), "Bruno", "Ana", Rasgo.MASCOTA, "un loro", false), "MENTIRA (100%)");

        Bot sabe = evaluador();
        sabe.aprender(new Hecho("Carla", Rasgo.MASCOTA, "un perro"));
        c.comprobar("sé el dato y coincide",
                veredicto(sabe, "Bruno", "Carla", Rasgo.MASCOTA, "un perro", true), "VERDAD (100%)");
        c.comprobar("sé el dato y no coincide",
                veredicto(sabe, "Bruno", "Carla", Rasgo.MASCOTA, "un loro", false), "MENTIRA (100%)");

        c.comprobar("el valor es mío, entonces es de otro",
                veredicto(evaluador(), "Bruno", "Carla", Rasgo.CIUDAD, "Salta", false), "MENTIRA (100%)");

        Bot desconfiado = evaluador();
        desconfiado.enterarseDeMentira("Bruno");
        c.comprobar("ya lo pescaron y hace un gesto",
                veredicto(desconfiado, "Bruno", "Carla", Rasgo.DEPORTE, "vóley", true), "MENTIRA (85%)");
        c.comprobar("ya lo pescaron y no hace un gesto",
                veredicto(desconfiado, "Bruno", "Carla", Rasgo.DEPORTE, "vóley", false), "MENTIRA (60%)");

        c.comprobar("primera vez y hace un gesto",
                veredicto(evaluador(), "Bruno", "Carla", Rasgo.DEPORTE, "vóley", true), "MENTIRA (55%)");
        c.comprobar("primera vez y no hace un gesto",
                veredicto(evaluador(), "Bruno", "Carla", Rasgo.DEPORTE, "vóley", false), "VERDAD (65%)");
    }

    // La Pila del recorrido: avanzar, retroceder y reconstruir el camino
    private static void recorridoPasoAPaso(Comprobador c) {
        Afirmacion dicha = new Afirmacion("Bruno", "Carla", Rasgo.DEPORTE, "vóley", false);
        Recorrido r = SOSPECHA.iniciar(new Investigacion(evaluador(), dicha));
        c.comprobar("arranca en la raíz", r.actual() == SOSPECHA.getArbol().getRaiz());
        c.comprobar("no se puede retroceder desde la raíz", !r.retroceder());
        c.comprobar("avanza un paso", r.avanzar());
        c.comprobar("va por la rama no de la raíz", r.actual() == SOSPECHA.getArbol().getRaiz().getNo());
        c.comprobar("retrocede", r.retroceder());
        c.comprobar("vuelve a la raíz", r.pasos(), 0);
        r.completar();
        c.comprobar("termina en una hoja", r.estaTerminado());
        c.comprobar("pasos hasta la hoja", r.pasos(), 5);
        c.comprobar("el camino tiene la raíz primero", r.camino().obtener(0) == SOSPECHA.getArbol().getRaiz());
        c.comprobar("leer el camino no lo consume", r.camino().tamanio(), 6);
        c.comprobar("pasa por la raíz", r.pasaPor(SOSPECHA.getArbol().getRaiz()));
        c.comprobar("no pasa por la certeza propia", !r.pasaPor(SOSPECHA.getArbol().getRaiz().getSi()));
        c.comprobar("una explicación por paso", r.explicacion().tamanio(), 6);
        c.comprobar("la primera explicación", r.explicacion().obtener(0), "Habla de Carla, no de vos.");
        c.comprobar("no avanza más allá de la hoja", !r.avanzar());
        c.esperarExcepcion("pedir veredicto antes de terminar", IllegalStateException.class,
                () -> SOSPECHA.iniciar(new Investigacion(evaluador(), dicha)).getSospecha());
        c.esperarExcepcion("juzgar la propia afirmación", IllegalArgumentException.class,
                () -> new Investigacion(new Bot("Bruno", fichaDeAna(), new Sincero()), dicha));
    }

    // El Sincero olvida hechos públicos viejos y llega a otra hoja
    private static void mismaAfirmacionDistintaMemoria(Comprobador c) {
        Bot sincero = new Bot("Ana", fichaDeAna(), new Sincero());
        Bot analitico = new Bot("Ana", fichaDeAna(), new Analitico());
        Hecho viejo = new Hecho("Carla", Rasgo.COMIDA, "el asado");
        sincero.enterarse(viejo);
        analitico.enterarse(viejo);
        for (int i = 0; i < 3; i++) {
            Rasgo rasgo = Rasgo.todos().obtener(i);
            Hecho nuevo = new Hecho("Dante", rasgo, rasgo.valor(3));
            sincero.enterarse(nuevo);
            analitico.enterarse(nuevo);
        }
        c.comprobar("el Analítico recuerda y la pesca",
                veredicto(analitico, "Bruno", "Carla", Rasgo.COMIDA, "los tacos", false), "MENTIRA (100%)");
        c.comprobar("el Sincero ya se olvidó y le cree",
                veredicto(sincero, "Bruno", "Carla", Rasgo.COMIDA, "los tacos", false), "VERDAD (65%)");
    }

    // Ayudas
    private static Ficha fichaDeAna() {
        return new Ficha("un gato", "fútbol", "la pizza", "Salta");
    }

    private static Bot evaluador() {
        return new Bot("Ana", fichaDeAna(), new Analitico());
    }

    private static String veredicto(Personaje quien, String hablante, String objetivo, Rasgo rasgo, String valor,
            boolean gesto) {
        Afirmacion a = new Afirmacion(hablante, objetivo, rasgo, valor, gesto);
        return SOSPECHA.recorrer(new Investigacion(quien, a)).getSospecha().toString();
    }
}
