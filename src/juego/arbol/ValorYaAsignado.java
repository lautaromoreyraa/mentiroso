package juego.arbol;

import juego.modelo.Afirmacion;

public class ValorYaAsignado implements Criterio {

    @Override
    public String getPregunta() {
        return "¿Ese valor es de otro?";
    }

    @Override
    public boolean evaluar(Investigacion inv) {
        String duenio = duenio(inv);
        return duenio != null && !duenio.equals(inv.getAfirmacion().getObjetivo());
    }

    @Override
    public String evidencia(Investigacion inv) {
        Afirmacion a = inv.getAfirmacion();
        String duenio = duenio(inv);
        if (!evaluar(inv)) {
            return "No sabés de quién es " + a.getValor() + ".";
        }
        if (duenio.equals(inv.getEvaluador().getNombre())) {
            return a.getValor() + " es tuyo: nadie más puede tenerlo.";
        }
        return "Sabés que " + a.getValor() + " es de " + duenio + ": cada valor es de uno solo.";
    }

    // Quién se sabe que tiene ese valor
    private String duenio(Investigacion inv) {
        Afirmacion a = inv.getAfirmacion();
        return inv.getEvaluador().quienTiene(a.getRasgo(), a.getValor());
    }
}
