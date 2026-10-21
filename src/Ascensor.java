/**
 * Hilo que representa el ascensor y gestiona su ciclo de vida.
 */
class Ascensor extends Thread {
    private final GestorAscensor gestor;

    public Ascensor(GestorAscensor gestor) {
        this.gestor = gestor;
    }

    @Override
    public void run() {
        try {
            // Atender a todos los usuarios en la cola de peticiones
            while (gestor.quedanUsuariosPendientes()) {
                gestor.atenderSiguienteUsuario();
            }

            // Cuando no hay más usuarios pendientes, baja solo hasta el piso -1
            while (gestor.getPisoActual() > -1) {
                gestor.moverHacia(-1);
                Thread.sleep(300);
            }

            // Mensaje de finalización según el enunciado
            System.out.println("Ascensor llegó a piso -1. Fin del programa.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}