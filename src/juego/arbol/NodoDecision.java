package juego.arbol;

import juego.modelo.Sospecha;

public abstract class NodoDecision {

    // Texto corto que se dibuja en el nodo
    public abstract String getTexto();

    // Explicación para quien investiga
    public abstract String evidencia(Investigacion investigacion);

    // Una pregunta se responde; un veredicto concluye
    public boolean responder(Investigacion investigacion) {
        throw new UnsupportedOperationException(getTexto() + " no es una pregunta");
    }

    public Sospecha concluir(Investigacion investigacion) {
        throw new UnsupportedOperationException(getTexto() + " no es un veredicto");
    }

    @Override
    public String toString() {
        return getTexto();
    }
}
