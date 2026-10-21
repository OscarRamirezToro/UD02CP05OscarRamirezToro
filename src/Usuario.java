/**
 * Hilo que representa a cada usuario del sistema.
 */
class Usuario extends Thread {
    private final int idUsuario;
    private final int pisoOrigen;
    private final int pisoDestino;
    private final GestorAscensor gestor;

    public Usuario(int idUsuario, int pisoOrigen, int pisoDestino, GestorAscensor gestor) {
        this.idUsuario = idUsuario;
        this.pisoOrigen = pisoOrigen;
        this.pisoDestino = pisoDestino;
        this.gestor = gestor;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getPisoOrigen() {
        return pisoOrigen;
    }

    public int getPisoDestino() {
        return pisoDestino;
    }

    @Override
    public void run() {
        try {
            // El usuario espera en su piso de origen
            System.out.println("Usuario " + idUsuario + " espera en piso " + pisoOrigen);

            // Subir al ascensor cuando corresponda y esté en el piso
            gestor.esperarYSubir(this);

            // Bajar del ascensor una vez alcance el destino
            gestor.esperarYBajar(this);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}