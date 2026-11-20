package estructuras;

public class ArbolBinario<T> {

    // Atributos
    private NodoArbol<T> raiz;
    private int cantidad;

    // Altas
    public NodoArbol<T> plantarRaiz(T dato) {
        if (raiz != null) {
            throw new IllegalStateException("el árbol ya tiene raíz");
        }
        raiz = new NodoArbol<>(dato, null);
        cantidad = 1;
        return raiz;
    }

    public NodoArbol<T> agregarSi(NodoArbol<T> padre, T dato) {
        validarPadre(padre);
        if (padre.getSi() != null) {
            throw new IllegalStateException("el nodo ya tiene rama sí");
        }
        NodoArbol<T> hijo = new NodoArbol<>(dato, padre);
        padre.setSi(hijo);
        cantidad++;
        return hijo;
    }

    public NodoArbol<T> agregarNo(NodoArbol<T> padre, T dato) {
        validarPadre(padre);
        if (padre.getNo() != null) {
            throw new IllegalStateException("el nodo ya tiene rama no");
        }
        NodoArbol<T> hijo = new NodoArbol<>(dato, padre);
        padre.setNo(hijo);
        cantidad++;
        return hijo;
    }

    // Consultas
    public NodoArbol<T> getRaiz() {
        return raiz;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int cantidadNodos() {
        return cantidad;
    }

    public int altura() {
        return altura(raiz);
    }

    public int cantidadHojas() {
        return cantidadHojas(raiz);
    }

    public boolean contiene(NodoArbol<T> nodo) {
        if (nodo == null) {
            return false;
        }
        NodoArbol<T> actual = nodo;
        while (actual.getPadre() != null) {
            actual = actual.getPadre();
        }
        return actual == raiz;
    }

    // Recorridos de los datos
    public Lista<T> preorden() {
        Lista<T> salida = new ListaDoble<>();
        preorden(raiz, salida);
        return salida;
    }

    public Lista<T> inorden() {
        Lista<T> salida = new ListaDoble<>();
        inorden(raiz, salida);
        return salida;
    }

    public Lista<T> postorden() {
        Lista<T> salida = new ListaDoble<>();
        postorden(raiz, salida);
        return salida;
    }

    // Recorrido de los nodos, para dibujar
    public Lista<NodoArbol<T>> nodosInorden() {
        Lista<NodoArbol<T>> salida = new ListaDoble<>();
        nodosInorden(raiz, salida);
        return salida;
    }

    // Recursión
    private int altura(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(altura(nodo.getSi()), altura(nodo.getNo()));
    }

    private int cantidadHojas(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        if (nodo.esHoja()) {
            return 1;
        }
        return cantidadHojas(nodo.getSi()) + cantidadHojas(nodo.getNo());
    }

    private void preorden(NodoArbol<T> nodo, Lista<T> salida) {
        if (nodo == null) {
            return;
        }
        salida.agregar(nodo.getDato());
        preorden(nodo.getSi(), salida);
        preorden(nodo.getNo(), salida);
    }

    private void inorden(NodoArbol<T> nodo, Lista<T> salida) {
        if (nodo == null) {
            return;
        }
        inorden(nodo.getSi(), salida);
        salida.agregar(nodo.getDato());
        inorden(nodo.getNo(), salida);
    }

    private void postorden(NodoArbol<T> nodo, Lista<T> salida) {
        if (nodo == null) {
            return;
        }
        postorden(nodo.getSi(), salida);
        postorden(nodo.getNo(), salida);
        salida.agregar(nodo.getDato());
    }

    private void nodosInorden(NodoArbol<T> nodo, Lista<NodoArbol<T>> salida) {
        if (nodo == null) {
            return;
        }
        nodosInorden(nodo.getSi(), salida);
        salida.agregar(nodo);
        nodosInorden(nodo.getNo(), salida);
    }

    // Validación
    private void validarPadre(NodoArbol<T> padre) {
        if (padre == null) {
            throw new IllegalArgumentException("el padre no puede ser nulo");
        }
        if (!contiene(padre)) {
            throw new IllegalArgumentException("el padre no pertenece a este árbol");
        }
    }
}
