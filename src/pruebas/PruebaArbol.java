package pruebas;

import estructuras.ArbolBinario;
import estructuras.Lista;
import estructuras.NodoArbol;

public class PruebaArbol {

    // Punto de entrada
    public static void main(String[] args) {
        Comprobador c = new Comprobador("ArbolBinario");

        ArbolBinario<String> vacio = new ArbolBinario<>();
        c.comprobar("un árbol nuevo está vacío", vacio.estaVacio());
        c.comprobar("altura del árbol vacío", vacio.altura(), 0);
        c.comprobar("preorden del árbol vacío", texto(vacio.preorden()), "");

        //        A
        //      /   \
        //     B     C
        //    / \     \
        //   D   E     F
        ArbolBinario<String> arbol = new ArbolBinario<>();
        NodoArbol<String> a = arbol.plantarRaiz("A");
        NodoArbol<String> b = arbol.agregarSi(a, "B");
        NodoArbol<String> cc = arbol.agregarNo(a, "C");
        NodoArbol<String> d = arbol.agregarSi(b, "D");
        arbol.agregarNo(b, "E");
        NodoArbol<String> f = arbol.agregarNo(cc, "F");

        c.comprobar("cantidad de nodos", arbol.cantidadNodos(), 6);
        c.comprobar("altura", arbol.altura(), 3);
        c.comprobar("cantidad de hojas", arbol.cantidadHojas(), 3);
        c.comprobar("preorden", texto(arbol.preorden()), "ABDECF");
        c.comprobar("inorden", texto(arbol.inorden()), "DBEACF");
        c.comprobar("postorden", texto(arbol.postorden()), "DEBFCA");
        c.comprobar("nodos en inorden", arbol.nodosInorden().obtener(3) == a);
        c.comprobar("profundidad de la raíz", a.profundidad(), 0);
        c.comprobar("profundidad de una hoja", f.profundidad(), 2);
        c.comprobar("D es hoja", d.esHoja());
        c.comprobar("B no es hoja", !b.esHoja());
        c.comprobar("el padre de F es C", f.getPadre() == cc);
        c.comprobar("el árbol contiene a F", arbol.contiene(f));

        ArbolBinario<String> otro = new ArbolBinario<>();
        NodoArbol<String> ajeno = otro.plantarRaiz("X");
        c.comprobar("el árbol no contiene un nodo ajeno", !arbol.contiene(ajeno));

        c.esperarExcepcion("plantar una segunda raíz", IllegalStateException.class, () -> arbol.plantarRaiz("Z"));
        c.esperarExcepcion("ocupar una rama sí ya ocupada", IllegalStateException.class, () -> arbol.agregarSi(a, "Z"));
        c.esperarExcepcion("ocupar una rama no ya ocupada", IllegalStateException.class, () -> arbol.agregarNo(b, "Z"));
        c.esperarExcepcion("agregar bajo un padre nulo", IllegalArgumentException.class, () -> arbol.agregarSi(null, "Z"));
        c.esperarExcepcion("agregar bajo un nodo de otro árbol", IllegalArgumentException.class, () -> arbol.agregarSi(ajeno, "Z"));
        c.esperarExcepcion("un dato nulo", IllegalArgumentException.class, () -> arbol.agregarSi(f, null));
        c.comprobar("los intentos fallidos no cambian la cantidad", arbol.cantidadNodos(), 6);

        c.terminar();
    }

    // Lista de letras como texto
    private static String texto(Lista<String> lista) {
        StringBuilder salida = new StringBuilder();
        for (int i = 0; i < lista.tamanio(); i++) {
            salida.append(lista.obtener(i));
        }
        return salida.toString();
    }
}
