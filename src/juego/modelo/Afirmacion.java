package juego.modelo;

public final class Afirmacion {

    // Atributos
    private final String hablante;
    private final Hecho dicho;
    private final boolean conGesto;

    // Constructores: por partes o a partir de un hecho
    public Afirmacion(String hablante, String objetivo, Rasgo rasgo, String valor, boolean conGesto) {
        this(hablante, new Hecho(objetivo, rasgo, valor), conGesto);
    }

    public Afirmacion(String hablante, Hecho dicho, boolean conGesto) {
        if (hablante == null || hablante.trim().isEmpty()) {
            throw new IllegalArgumentException("la afirmación necesita un hablante");
        }
        if (dicho == null) {
            throw new IllegalArgumentException("la afirmación necesita qué se dice");
        }
        if (dicho.getSujeto().equals(hablante)) {
            throw new IllegalArgumentException("se afirma sobre otro jugador, no sobre uno mismo");
        }
        this.hablante = hablante;
        this.dicho = dicho;
        this.conGesto = conGesto;
    }

    // Consultas
    public String getHablante() {
        return hablante;
    }

    public String getObjetivo() {
        return dicho.getSujeto();
    }

    public Rasgo getRasgo() {
        return dicho.getRasgo();
    }

    public String getValor() {
        return dicho.getValor();
    }

    public Hecho comoHecho() {
        return dicho;
    }

    public boolean tieneGesto() {
        return conGesto;
    }

    public boolean esVerdadSegun(Ficha fichaDelObjetivo) {
        return fichaDelObjetivo.tiene(getRasgo(), getValor());
    }

    @Override
    public String toString() {
        return dicho.toString();
    }
}
