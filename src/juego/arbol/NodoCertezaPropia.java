package juego.arbol;

import juego.modelo.Sospecha;

public class NodoCertezaPropia extends NodoDecision {

    @Override
    public String getTexto() {
        return "Lo sé por mi ficha";
    }

    @Override
    public String evidencia(Investigacion investigacion) {
        boolean esVerdad = esVerdad(investigacion);
        return "Tu ficha dice que " + (esVerdad ? "es verdad." : "es mentira.");
    }

    @Override
    public Sospecha concluir(Investigacion investigacion) {
        return new Sospecha(!esVerdad(investigacion), 1.0);
    }

    // Comparación directa con la ficha de quien investiga
    private boolean esVerdad(Investigacion investigacion) {
        return investigacion.getAfirmacion().esVerdadSegun(investigacion.getEvaluador().getFicha());
    }
}
