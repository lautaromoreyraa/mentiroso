package estructuras;

public class ColaEnlazada<T> implements Cola<T> {

    // Nodo simple
    private static class Nodo<T> {
        private final T dato;
        private Nodo<T> siguiente;

        private Nodo(T dato) {
            this.dato = dato;
        }
    }

    // Atributos
    private Nodo<T> primero;
    private Nodo<T> fin;
    private int cantidad;

    // Operaciones
    @Override
    public void encolar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (primero == null) {
            primero = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        cantidad++;
    }

    @Override
    public T desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("la cola está vacía");
        }
        T dato = primero.dato;
        primero = primero.siguiente;
        if (primero == null) {
            fin = null;
        }
        cantidad--;
        return dato;
    }

    @Override
    public T frente() {
        if (estaVacia()) {
            throw new IllegalStateException("la cola está vacía");
        }
        return primero.dato;
    }

    // Consultas
    @Override
    public boolean estaVacia() {
        return cantidad == 0;
    }

    @Override
    public int tamanio() {
        return cantidad;
    }

    @Override
    public void vaciar() {
        primero = null;
        fin = null;
        cantidad = 0;
    }
}
