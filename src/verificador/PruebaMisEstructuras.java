package verificador;

import estructuras.ColaEnlazada;
import estructuras.ListaDoble;
import estructuras.PilaEnlazada;

public class PruebaMisEstructuras {

    // Punto de entrada
    public static void main(String[] args) {
        boolean lista = VerificadorEstructuras.verificarLista(new ListaDoble<Integer>());
        boolean pila = VerificadorEstructuras.verificarPila(new PilaEnlazada<Integer>());
        boolean cola = VerificadorEstructuras.verificarCola(new ColaEnlazada<Integer>());
        boolean todas = lista && pila && cola;
        System.out.println("Resultado general: " + (todas ? "OK" : "HAY FALLAS"));
        if (!todas) {
            System.exit(1);
        }
    }
}
