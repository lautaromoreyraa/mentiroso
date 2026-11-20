package estructuras;

public class PilaEnlazada<T> implements Pila<T> {

    // Nodo simple
    private static class Nodo<T> {
        private final T dato;
        private Nodo<T> siguiente;

        private Nodo(T dato) {
            this.dato = dato;
        }
    }

    // Atributos
    private Nodo<T> cima;
    private int cantidad;

    // Operaciones
    @Override
    public void apilar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        nuevo.siguiente = cima;
        cima = nuevo;
        cantidad++;
    }

    @Override
    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("la pila está vacía");
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        cantidad--;
        return dato;
    }

    @Override
    public T tope() {
        if (estaVacia()) {
            throw new IllegalStateException("la pila está vacía");
        }
        return cima.dato;
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
        cima = null;
        cantidad = 0;
    }
}
