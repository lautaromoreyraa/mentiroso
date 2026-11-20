package juego.reglas;

import estructuras.ListaDoble;
import juego.entidades.Personaje;
import juego.modelo.Resultado;

public final class Puntuacion {

    // Constantes
    public static final int MENTIRA_COLADA = 2;
    public static final int ACUSACION_ACERTADA = 1;
    public static final int VERDAD_ACUSADA = 1;
    public static final int VERDAD_CREIDA = 1;
    public static final int ROL_ADIVINADO = 2;

    // Sin instancias
    private Puntuacion() {
    }

    // Aplica la tabla base y después el bonus de cada rol
    public static void aplicar(Resultado resultado, Mesa mesa) {
        Personaje hablante = mesa.buscar(resultado.getAfirmacion().getHablante());
        ListaDoble<String> acusadores = resultado.getAcusadores();
        if (!resultado.eraVerdad() && !resultado.huboAcusacion()) {
            hablante.sumarPuntos(MENTIRA_COLADA);
        } else if (!resultado.eraVerdad()) {
            hablante.perderVida();
            for (String nombre : acusadores) {
                mesa.buscar(nombre).sumarPuntos(ACUSACION_ACERTADA);
            }
        } else if (resultado.huboAcusacion()) {
            hablante.sumarPuntos(VERDAD_ACUSADA);
            for (String nombre : acusadores) {
                mesa.buscar(nombre).perderVida();
            }
        } else {
            hablante.sumarPuntos(VERDAD_CREIDA);
        }
        for (Personaje p : mesa.getPersonajes()) {
            p.sumarPuntos(p.getRol().bonificar(resultado, p.getNombre()));
        }
    }
}
