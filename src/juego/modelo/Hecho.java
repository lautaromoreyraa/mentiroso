package juego.modelo;

public final class Hecho {

    // Atributos
    private final String sujeto;
    private final Rasgo rasgo;
    private final String valor;

    // Constructor
    public Hecho(String sujeto, Rasgo rasgo, String valor) {
        if (sujeto == null || sujeto.trim().isEmpty()) {
            throw new IllegalArgumentException("el hecho necesita un sujeto");
        }
        if (rasgo == null) {
            throw new IllegalArgumentException("el hecho necesita un rasgo");
        }
        if (valor == null || !rasgo.admite(valor)) {
            throw new IllegalArgumentException("valor inválido para " + rasgo.getNombre() + ": " + valor);
        }
        this.sujeto = sujeto;
        this.rasgo = rasgo;
        this.valor = valor;
    }

    // Consultas
    public String getSujeto() {
        return sujeto;
    }

    public Rasgo getRasgo() {
        return rasgo;
    }

    public String getValor() {
        return valor;
    }

    public boolean esSobre(String sujeto, Rasgo rasgo) {
        return this.sujeto.equals(sujeto) && this.rasgo == rasgo;
    }

    // Igualdad por contenido, para buscarlo en una Lista
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Hecho)) {
            return false;
        }
        Hecho h = (Hecho) otro;
        return sujeto.equals(h.sujeto) && rasgo == h.rasgo && valor.equals(h.valor);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * sujeto.hashCode() + rasgo.hashCode()) + valor.hashCode();
    }

    @Override
    public String toString() {
        return sujeto + " " + rasgo.getVerbo() + " " + valor;
    }
}
