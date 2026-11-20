package juego.arbol;

public interface Criterio {

    // La pregunta tal como se ve en el árbol
    String getPregunta();

    // La respuesta, con lo que sabe quien investiga
    boolean evaluar(Investigacion investigacion);

    // Por qué la respuesta es esa, contado a quien investiga
    String evidencia(Investigacion investigacion);
}
