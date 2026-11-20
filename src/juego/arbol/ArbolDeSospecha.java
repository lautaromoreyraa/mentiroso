package juego.arbol;

import estructuras.ArbolBinario;
import estructuras.NodoArbol;

public class ArbolDeSospecha {

    // Atributos
    private final ArbolBinario<NodoDecision> arbol = new ArbolBinario<>();

    // Constructor: arma el árbol una sola vez
    public ArbolDeSospecha() {
        NodoArbol<NodoDecision> sobreMi = arbol.plantarRaiz(new NodoPregunta(new EsSobreMi()));
        arbol.agregarSi(sobreMi, new NodoCertezaPropia());

        NodoArbol<NodoDecision> conozco = arbol.agregarNo(sobreMi, new NodoPregunta(new ConozcoElRasgo()));
        NodoArbol<NodoDecision> coincide = arbol.agregarSi(conozco, new NodoPregunta(new CoincideConLoConocido()));
        arbol.agregarSi(coincide, new NodoVeredicto(false, 1.0));
        arbol.agregarNo(coincide, new NodoVeredicto(true, 1.0));

        NodoArbol<NodoDecision> deOtro = arbol.agregarNo(conozco, new NodoPregunta(new ValorYaAsignado()));
        arbol.agregarSi(deOtro, new NodoVeredicto(true, 1.0));

        NodoArbol<NodoDecision> pescado = arbol.agregarNo(deOtro, new NodoPregunta(new FueAtrapadoAntes()));
        NodoArbol<NodoDecision> gestoReincidente = arbol.agregarSi(pescado, new NodoPregunta(new HizoGesto()));
        arbol.agregarSi(gestoReincidente, new NodoVeredicto(true, 0.85));
        arbol.agregarNo(gestoReincidente, new NodoVeredicto(true, 0.60));

        NodoArbol<NodoDecision> gesto = arbol.agregarNo(pescado, new NodoPregunta(new HizoGesto()));
        arbol.agregarSi(gesto, new NodoVeredicto(true, 0.55));
        arbol.agregarNo(gesto, new NodoVeredicto(false, 0.65));
    }

    // Consultas
    public ArbolBinario<NodoDecision> getArbol() {
        return arbol;
    }

    // Recorridos
    public Recorrido iniciar(Investigacion investigacion) {
        return new Recorrido(arbol, investigacion);
    }

    public Recorrido recorrer(Investigacion investigacion) {
        Recorrido recorrido = iniciar(investigacion);
        recorrido.completar();
        return recorrido;
    }
}
