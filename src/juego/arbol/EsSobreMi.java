package juego.arbol;

public class EsSobreMi implements Criterio {

    @Override
    public String getPregunta() {
        return "¿Habla de mí?";
    }

    @Override
    public boolean evaluar(Investigacion inv) {
        return inv.getAfirmacion().getObjetivo().equals(inv.getEvaluador().getNombre());
    }

    @Override
    public String evidencia(Investigacion inv) {
        if (evaluar(inv)) {
            return "Habla de vos: tu ficha tiene la respuesta.";
        }
        return "Habla de " + inv.getAfirmacion().getObjetivo() + ", no de vos.";
    }
}
