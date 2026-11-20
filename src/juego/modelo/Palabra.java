package juego.modelo;

public enum Palabra {

    // Lo que dice un personaje al juzgar, y cómo lo ve el humano en su botón
    TE_CREO("Te creo", "Verdad"),
    MENTIROSO("¡Mentiroso!", "¡Mentiroso!");

    // Atributos
    private final String dicho;
    private final String opcion;

    // Constructor
    Palabra(String dicho, String opcion) {
        this.dicho = dicho;
        this.opcion = opcion;
    }

    // Consultas
    public String getDicho() {
        return dicho;
    }

    public String getOpcion() {
        return opcion;
    }

    public boolean acusa() {
        return this == MENTIROSO;
    }
}
