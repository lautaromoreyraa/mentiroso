package juego.arbol;

import estructuras.ArbolBinario;
import estructuras.Lista;
import estructuras.ListaDoble;
import estructuras.NodoArbol;
import estructuras.Pila;
import estructuras.PilaEnlazada;
import juego.modelo.Sospecha;

public class Recorrido {

    // Atributos
    private final Investigacion investigacion;
    private final Pila<NodoArbol<NodoDecision>> camino = new PilaEnlazada<>();

    // Constructor: arranca parado en la raíz
    public Recorrido(ArbolBinario<NodoDecision> arbol, Investigacion investigacion) {
        if (arbol == null || arbol.estaVacio()) {
            throw new IllegalArgumentException("no se puede recorrer un árbol vacío");
        }
        if (investigacion == null) {
            throw new IllegalArgumentException("el recorrido necesita una investigación");
        }
        this.investigacion = investigacion;
        camino.apilar(arbol.getRaiz());
    }

    // Avance: responde la pregunta actual y baja por la rama que corresponde
    public boolean avanzar() {
        if (estaTerminado()) {
            return false;
        }
        NodoArbol<NodoDecision> actual = camino.tope();
        boolean respuesta = actual.getDato().responder(investigacion);
        camino.apilar(respuesta ? actual.getSi() : actual.getNo());
        return true;
    }

    // Retroceso: desapila el último paso, nunca la raíz
    public boolean retroceder() {
        if (camino.tamanio() <= 1) {
            return false;
        }
        camino.desapilar();
        return true;
    }

    public void completar() {
        while (avanzar()) {
            continue;
        }
    }

    // Consultas
    public Investigacion getInvestigacion() {
        return investigacion;
    }

    public NodoArbol<NodoDecision> actual() {
        return camino.tope();
    }

    public boolean estaTerminado() {
        return camino.tope().esHoja();
    }

    public int pasos() {
        return camino.tamanio() - 1;
    }

    public Sospecha getSospecha() {
        if (!estaTerminado()) {
            throw new IllegalStateException("el recorrido todavía no llegó a un veredicto");
        }
        return camino.tope().getDato().concluir(investigacion);
    }

    // El camino de la raíz al nodo actual: se desapila todo y se vuelve a apilar
    public Lista<NodoArbol<NodoDecision>> camino() {
        Pila<NodoArbol<NodoDecision>> invertida = new PilaEnlazada<>();
        while (!camino.estaVacia()) {
            invertida.apilar(camino.desapilar());
        }
        Lista<NodoArbol<NodoDecision>> salida = new ListaDoble<>();
        while (!invertida.estaVacia()) {
            NodoArbol<NodoDecision> nodo = invertida.desapilar();
            salida.agregar(nodo);
            camino.apilar(nodo);
        }
        return salida;
    }

    public boolean pasaPor(NodoArbol<NodoDecision> nodo) {
        return camino().indiceDe(nodo) >= 0;
    }

    // La explicación de cada paso, de la raíz a la hoja
    public Lista<String> explicacion() {
        Lista<String> salida = new ListaDoble<>();
        Lista<NodoArbol<NodoDecision>> nodos = camino();
        for (int i = 0; i < nodos.tamanio(); i++) {
            salida.agregar(nodos.obtener(i).getDato().evidencia(investigacion));
        }
        return salida;
    }
}
