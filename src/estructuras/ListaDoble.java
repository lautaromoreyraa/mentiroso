package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaDoble<T> implements Lista<T>, Iterable<T> {

    // Nodo con enlace al anterior y al siguiente
    private static class Nodo<T> {
        private final T dato;
        private Nodo<T> anterior;
        private Nodo<T> siguiente;

        private Nodo(T dato) {
            this.dato = dato;
        }
    }

    // Atributos
    private Nodo<T> cabeza;
    private Nodo<T> ultimo;
    private int cantidad;

    // Altas
    @Override
    public void agregar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            nuevo.anterior = ultimo;
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        cantidad++;
    }

    @Override
    public void insertar(int indice, T elemento) {
        if (indice < 0 || indice > cantidad) {
            throw new IndexOutOfBoundsException("índice " + indice + " fuera de rango");
        }
        if (indice == cantidad) {
            agregar(elemento);
            return;
        }
        Nodo<T> actual = buscarNodo(indice);
        Nodo<T> nuevo = new Nodo<>(elemento);
        nuevo.siguiente = actual;
        nuevo.anterior = actual.anterior;
        if (actual.anterior == null) {
            cabeza = nuevo;
        } else {
            actual.anterior.siguiente = nuevo;
        }
        actual.anterior = nuevo;
        cantidad++;
    }

    // Consultas
    @Override
    public T obtener(int indice) {
        return buscarNodo(indice).dato;
    }

    @Override
    public int indiceDe(T elemento) {
        int indice = 0;
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.siguiente) {
            boolean iguales = actual.dato == null ? elemento == null : actual.dato.equals(elemento);
            if (iguales) {
                return indice;
            }
            indice++;
        }
        return -1;
    }

    public boolean contiene(T elemento) {
        return indiceDe(elemento) >= 0;
    }

    @Override
    public int tamanio() {
        return cantidad;
    }

    @Override
    public boolean estaVacia() {
        return cantidad == 0;
    }

    // Bajas
    @Override
    public T eliminar(int indice) {
        Nodo<T> actual = buscarNodo(indice);
        if (actual.anterior == null) {
            cabeza = actual.siguiente;
        } else {
            actual.anterior.siguiente = actual.siguiente;
        }
        if (actual.siguiente == null) {
            ultimo = actual.anterior;
        } else {
            actual.siguiente.anterior = actual.anterior;
        }
        cantidad--;
        return actual.dato;
    }

    @Override
    public void vaciar() {
        cabeza = null;
        ultimo = null;
        cantidad = 0;
    }

    // Recorrido con for-each
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }

    // Búsqueda desde la punta más cercana
    private Nodo<T> buscarNodo(int indice) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("índice " + indice + " fuera de rango");
        }
        Nodo<T> actual;
        if (indice < cantidad / 2) {
            actual = cabeza;
            for (int i = 0; i < indice; i++) {
                actual = actual.siguiente;
            }
        } else {
            actual = ultimo;
            for (int i = cantidad - 1; i > indice; i--) {
                actual = actual.anterior;
            }
        }
        return actual;
    }
}
