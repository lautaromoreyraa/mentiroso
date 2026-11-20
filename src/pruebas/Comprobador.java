package pruebas;

public final class Comprobador {

    // Atributos
    private final String nombre;
    private int total;
    private int fallas;

    // Constructor
    public Comprobador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("la prueba necesita un nombre");
        }
        this.nombre = nombre;
        System.out.println("Probando " + nombre + "...");
    }

    // Comprobaciones
    public void comprobar(String descripcion, boolean correcto) {
        total++;
        if (!correcto) {
            fallas++;
            System.out.println("  [FALLA] " + descripcion);
        }
    }

    public void comprobar(String descripcion, Object obtenido, Object esperado) {
        boolean iguales = obtenido == null ? esperado == null : obtenido.equals(esperado);
        comprobar(descripcion + ": dio " + obtenido + " y debía dar " + esperado, iguales);
    }

    public void esperarExcepcion(String descripcion, Class<? extends Throwable> tipo, Runnable accion) {
        total++;
        try {
            accion.run();
            fallas++;
            System.out.println("  [FALLA] " + descripcion + ": debía lanzar " + tipo.getSimpleName());
        } catch (Throwable t) {
            if (!tipo.isInstance(t)) {
                fallas++;
                System.out.println("  [FALLA] " + descripcion + ": lanzó " + t.getClass().getSimpleName());
            }
        }
    }

    // Cierre: sale con código 1 si algo falló
    public void terminar() {
        int correctas = total - fallas;
        System.out.println(nombre + ": " + correctas + " de " + total + " comprobaciones correctas"
                + (fallas == 0 ? "  ->  OK" : "  ->  HAY FALLAS"));
        if (fallas > 0) {
            System.exit(1);
        }
    }
}
