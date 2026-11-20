package juego.arbol;

public class HizoGesto implements Criterio {

    @Override
    public String getPregunta() {
        return "¿Hizo un gesto?";
    }

    @Override
    public boolean evaluar(Investigacion inv) {
        return inv.getAfirmacion().tieneGesto();
    }

    @Override
    public String evidencia(Investigacion inv) {
        String hablante = inv.getAfirmacion().getHablante();
        if (evaluar(inv)) {
            return hablante + " miró para otro lado al hablar.";
        }
        return hablante + " habló sin dudar.";
    }
}
