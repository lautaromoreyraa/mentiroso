package juego.arbol;

import juego.modelo.Afirmacion;
import juego.modelo.Hecho;

public class ConozcoElRasgo implements Criterio {

    @Override
    public String getPregunta() {
        return "¿Sé ese dato?";
    }

    @Override
    public boolean evaluar(Investigacion inv) {
        return sabido(inv) != null;
    }

    @Override
    public String evidencia(Investigacion inv) {
        Hecho sabido = sabido(inv);
        Afirmacion a = inv.getAfirmacion();
        if (sabido != null) {
            return "Sabés que " + sabido + ".";
        }
        return "No sabés nada de " + a.getRasgo().conArticulo() + " de " + a.getObjetivo() + ".";
    }

    // Lo que recuerda del objetivo en ese rasgo
    static Hecho sabido(Investigacion inv) {
        Afirmacion a = inv.getAfirmacion();
        return inv.getEvaluador().recuerda(a.getObjetivo(), a.getRasgo());
    }
}
