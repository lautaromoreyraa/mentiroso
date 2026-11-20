package juego.modelo;

import estructuras.Lista;
import estructuras.ListaDoble;

public enum Rasgo {

    // Los cuatro rasgos de una ficha, con sus cinco valores
    MASCOTA("mascota", "tiene", "un perro", "un gato", "un loro", "un pez", "una tortuga"),
    DEPORTE("deporte", "juega al", "fútbol", "básquet", "vóley", "tenis", "pádel"),
    COMIDA("comida", "ama", "la pizza", "las empanadas", "el asado", "el sushi", "los tacos"),
    CIUDAD("ciudad", "vive en", "Resistencia", "Corrientes", "Rosario", "Córdoba", "Salta");

    // Constantes
    public static final int VALORES_POR_RASGO = 5;

    // Atributos
    private final String nombre;
    private final String verbo;
    private final ListaDoble<String> valores = new ListaDoble<>();

    // Constructor
    Rasgo(String nombre, String verbo, String v1, String v2, String v3, String v4, String v5) {
        this.nombre = nombre;
        this.verbo = verbo;
        valores.agregar(v1);
        valores.agregar(v2);
        valores.agregar(v3);
        valores.agregar(v4);
        valores.agregar(v5);
    }

    // Consultas
    public String getNombre() {
        return nombre;
    }

    public String getVerbo() {
        return verbo;
    }

    public int cantidadDeValores() {
        return valores.tamanio();
    }

    public String valor(int indice) {
        return valores.obtener(indice);
    }

    public int indiceDe(String valor) {
        return valores.indiceDe(valor);
    }

    public boolean admite(String valor) {
        return valores.contiene(valor);
    }

    public Lista<String> copiaDeValores() {
        Lista<String> copia = new ListaDoble<>();
        for (String valor : valores) {
            copia.agregar(valor);
        }
        return copia;
    }

    // Todos los rasgos, en orden, sin usar el arreglo de values()
    public static ListaDoble<Rasgo> todos() {
        ListaDoble<Rasgo> lista = new ListaDoble<>();
        lista.agregar(MASCOTA);
        lista.agregar(DEPORTE);
        lista.agregar(COMIDA);
        lista.agregar(CIUDAD);
        return lista;
    }
}
