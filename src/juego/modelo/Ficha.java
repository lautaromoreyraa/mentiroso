package juego.modelo;

public class Ficha {

    // Atributos
    private final String mascota;
    private final String deporte;
    private final String comida;
    private final String ciudad;

    // Constructor
    public Ficha(String mascota, String deporte, String comida, String ciudad) {
        this.mascota = validar(Rasgo.MASCOTA, mascota);
        this.deporte = validar(Rasgo.DEPORTE, deporte);
        this.comida = validar(Rasgo.COMIDA, comida);
        this.ciudad = validar(Rasgo.CIUDAD, ciudad);
    }

    // Consultas
    public String valor(Rasgo rasgo) {
        switch (rasgo) {
            case MASCOTA:
                return mascota;
            case DEPORTE:
                return deporte;
            case COMIDA:
                return comida;
            case CIUDAD:
                return ciudad;
            default:
                throw new IllegalArgumentException("rasgo desconocido: " + rasgo);
        }
    }

    public boolean tiene(Rasgo rasgo, String valor) {
        return valor(rasgo).equals(valor);
    }

    // Validación
    private static String validar(Rasgo rasgo, String valor) {
        if (valor == null || !rasgo.admite(valor)) {
            throw new IllegalArgumentException("valor inválido para " + rasgo.getNombre() + ": " + valor);
        }
        return valor;
    }
}
