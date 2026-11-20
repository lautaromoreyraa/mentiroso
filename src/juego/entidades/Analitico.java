package juego.entidades;

import juego.modelo.Hecho;
import juego.modelo.Resultado;

public class Analitico extends Rol {

    // Identidad
    @Override
    public String getNombre() {
        return "Analítico";
    }

    @Override
    public String getObjetivo() {
        return "Leé la mesa: cada juicio acertado, crea o acuse, suma un punto extra.";
    }

    // Afirma: miente poco y sólo donde no lo pueden deducir
    @Override
    public double probabilidadDeMentir() {
        return 0.30;
    }

    @Override
    public double probabilidadDeGesto() {
        return 0.40;
    }

    @Override
    public boolean mentiraAceptable(Personaje quien, Hecho mentira) {
        return !quien.esDeduciblePublicamente(mentira);
    }

    // Juzga: término medio
    @Override
    public double umbralDeAcusacion() {
        return 0.65;
    }

    // Bonus: cualquier juicio acertado
    @Override
    public int bonificar(Resultado resultado, String nombre) {
        return resultado.acerto(nombre) ? 1 : 0;
    }
}
