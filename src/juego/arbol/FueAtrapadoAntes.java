package juego.arbol;

public class FueAtrapadoAntes implements Criterio {

    @Override
    public String getPregunta() {
        return "¿Ya lo pescaron?";
    }

    @Override
    public boolean evaluar(Investigacion inv) {
        return veces(inv) > 0;
    }

    @Override
    public String evidencia(Investigacion inv) {
        String hablante = inv.getAfirmacion().getHablante();
        int veces = veces(inv);
        if (veces == 0) {
            return "Nunca pescaron a " + hablante + " mintiendo.";
        }
        return hablante + " ya mintió " + veces + (veces == 1 ? " vez." : " veces.");
    }

    // Cuántas veces lo vio pescado quien investiga
    private int veces(Investigacion inv) {
        return inv.getEvaluador().vecesPescado(inv.getAfirmacion().getHablante());
    }
}
