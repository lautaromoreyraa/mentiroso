package juego.arbol;

import juego.modelo.Hecho;

public class CoincideConLoConocido implements Criterio {

    @Override
    public String getPregunta() {
        return "¿Coincide?";
    }

    @Override
    public boolean evaluar(Investigacion inv) {
        Hecho sabido = ConozcoElRasgo.sabido(inv);
        return sabido != null && sabido.getValor().equals(inv.getAfirmacion().getValor());
    }

    @Override
    public String evidencia(Investigacion inv) {
        Hecho sabido = ConozcoElRasgo.sabido(inv);
        if (sabido == null) {
            return "No hay nada con qué comparar.";
        }
        if (evaluar(inv)) {
            return "Coincide con lo que sabés: " + sabido + ".";
        }
        return "Sabés que " + sabido + ", no lo que dice.";
    }
}
