package juego.arbol;

import juego.modelo.Sospecha;

public class NodoVeredicto extends NodoDecision {

    // Atributos
    private final Sospecha sospecha;

    // Constructor
    public NodoVeredicto(boolean mentira, double confianza) {
        this.sospecha = new Sospecha(mentira, confianza);
    }

    @Override
    public String getTexto() {
        return sospecha.toString();
    }

    @Override
    public String evidencia(Investigacion investigacion) {
        if (sospecha.esCertera()) {
            return "Con lo que sabés, no hay duda.";
        }
        return "No hay prueba: es una corazonada con lo que viste.";
    }

    @Override
    public Sospecha concluir(Investigacion investigacion) {
        return sospecha;
    }
}
