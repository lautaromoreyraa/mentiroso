package juego.entidades;

import juego.modelo.Resultado;

public class Sincero extends Rol {

    // Constantes
    private static final int MEMORIA_PUBLICA = 3;

    // Identidad
    @Override
    public String getNombre() {
        return "Sincero";
    }

    @Override
    public String getObjetivo() {
        return "Decí la verdad y desenmascará mentirosos: cada mentira que acusás bien suma un punto extra.";
    }

    // Afirma: nunca miente
    @Override
    public double probabilidadDeMentir() {
        return 0.0;
    }

    @Override
    public double probabilidadDeGesto() {
        return 0.0;
    }

    // Juzga: sólo acusa cuando está muy seguro
    @Override
    public double umbralDeAcusacion() {
        return 0.80;
    }

    // Memoria: se acuerda sólo de los últimos hechos públicos
    @Override
    public boolean recuerdaPublico(int antiguedad) {
        return antiguedad < MEMORIA_PUBLICA;
    }

    // Bonus: acusación acertada
    @Override
    public int bonificar(Resultado resultado, String nombre) {
        return resultado.acuso(nombre) && !resultado.eraVerdad() ? 1 : 0;
    }
}
