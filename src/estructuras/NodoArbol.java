package estructuras;

public class NodoArbol<T> {

    // Atributos
    private final T dato;
    private final NodoArbol<T> padre;
    private NodoArbol<T> si;
    private NodoArbol<T> no;

    // Constructor: sólo el árbol crea nodos
    NodoArbol(T dato, NodoArbol<T> padre) {
        if (dato == null) {
            throw new IllegalArgumentException("el dato del nodo no puede ser nulo");
        }
        this.dato = dato;
        this.padre = padre;
    }

    // Consultas
    public T getDato() {
        return dato;
    }

    public NodoArbol<T> getPadre() {
        return padre;
    }

    public NodoArbol<T> getSi() {
        return si;
    }

    public NodoArbol<T> getNo() {
        return no;
    }

    public boolean esHoja() {
        return si == null && no == null;
    }

    public boolean esRaiz() {
        return padre == null;
    }

    public int profundidad() {
        int niveles = 0;
        for (NodoArbol<T> actual = padre; actual != null; actual = actual.padre) {
            niveles++;
        }
        return niveles;
    }

    // Enlaces, sólo desde el árbol
    void setSi(NodoArbol<T> hijo) {
        this.si = hijo;
    }

    void setNo(NodoArbol<T> hijo) {
        this.no = hijo;
    }
}
