package juego.arbol;

public class NodoPregunta extends NodoDecision {

    // Atributos
    private final Criterio criterio;

    // Constructor
    public NodoPregunta(Criterio criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException("la pregunta necesita un criterio");
        }
        this.criterio = criterio;
    }

    @Override
    public String getTexto() {
        return criterio.getPregunta();
    }

    @Override
    public String evidencia(Investigacion investigacion) {
        return criterio.evidencia(investigacion);
    }

    @Override
    public boolean responder(Investigacion investigacion) {
        return criterio.evaluar(investigacion);
    }
}
