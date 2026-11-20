package juego.modelo;

public final class Sospecha {

    // Atributos
    private final boolean mentira;
    private final double confianza;

    // Constructor
    public Sospecha(boolean mentira, double confianza) {
        if (confianza < 0.5 || confianza > 1.0) {
            throw new IllegalArgumentException("la confianza va de 0,5 a 1: " + confianza);
        }
        this.mentira = mentira;
        this.confianza = confianza;
    }

    // Consultas
    public boolean esMentira() {
        return mentira;
    }

    public double getConfianza() {
        return confianza;
    }

    public double probabilidadDeMentira() {
        return mentira ? confianza : 1.0 - confianza;
    }

    public boolean esCertera() {
        return confianza >= 1.0;
    }

    @Override
    public String toString() {
        return (mentira ? "MENTIRA" : "VERDAD") + " (" + Math.round(confianza * 100) + "%)";
    }
}
