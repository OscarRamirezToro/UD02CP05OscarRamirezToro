/**
 * Clase principal para lanzar la simulación con los datos del ejemplo.
 */
public class Main {
    public static void main(String[] args) {
        GestorAscensor gestor = new GestorAscensor();

        // Instanciación del hilo del ascensor
        Ascensor ascensor = new Ascensor(gestor);
        ascensor.start();

        // Datos del ejemplo del caso práctico:
        // Usuario 1: de piso 0 a piso 3
        // Usuario 2: de piso 2 a piso -1
        // Usuario 3: de piso 1 a piso 5
        Usuario u1 = new Usuario(1, 0, 3, gestor);
        Usuario u2 = new Usuario(2, 2, -1, gestor);
        Usuario u3 = new Usuario(3, 1, 5, gestor);

        // Se encolan los usuarios en el orden establecido
        gestor.encolarUsuario(u1);
        gestor.encolarUsuario(u2);
        gestor.encolarUsuario(u3);

        // Se inician los hilos de los usuarios
        u1.start();
        u2.start();
        u3.start();

        // Se indica que no habrá más usuarios en la simulación
        gestor.setTodosAtendidos();
    }
}