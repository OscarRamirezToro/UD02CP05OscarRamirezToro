import java.util.LinkedList;
import java.util.Queue;

/**
 * Recurso compartido que gestiona el estado del ascensor, la cola de usuarios
 * y la sincronización entre los hilos usando wait() y notifyAll().
 */
class GestorAscensor {
    private int pisoActual = 0;              // El ascensor inicia en el piso 0
    private Usuario usuarioActual = null;     // Usuario que está dentro del ascensor
    private boolean usuarioDentro = false;    // Indica si el usuario ya ha subido
    private boolean viajeCompletado = false;  // Indica si el usuario ya ha llegado y bajado
    private boolean todosAtendidos = false;   // Bandera para señalar que no habrá más usuarios

    // Cola recomendada para atender a los usuarios en orden FIFO
    private final Queue<Usuario> colaUsuarios = new LinkedList<>();

    /**
     * Registra un usuario en la cola de peticiones.
     */
    public synchronized void encolarUsuario(Usuario u) {
        colaUsuarios.add(u);
        notifyAll(); // Notifica al ascensor de que hay una nueva petición
    }

    /**
     * Informa que ya no se van a añadir más usuarios a la simulación.
     */
    public synchronized void setTodosAtendidos() {
        this.todosAtendidos = true;
        notifyAll();
    }

    public synchronized int getPisoActual() {
        return pisoActual;
    }

    /**
     * Mueve el ascensor un piso hacia el objetivo (arriba o abajo).
     */
    public synchronized void moverHacia(int pisoDestino) {
        if (pisoActual < pisoDestino) {
            pisoActual++;
        } else if (pisoActual > pisoDestino) {
            pisoActual--;
        }
        System.out.println("Ascensor en piso " + pisoActual);
        notifyAll(); // Notifica a los usuarios del cambio de piso
    }

    /**
     * Método ejecutado por el usuario: espera a que el ascensor llegue a su origen y entra.
     */
    public synchronized void esperarYSubir(Usuario u) throws InterruptedException {
        // Espera hasta que el ascensor esté libre, sea su turno de ser atendido y esté en su piso de origen
        while (usuarioActual != u || pisoActual != u.getPisoOrigen()) {
            wait();
        }
        usuarioDentro = true;
        System.out.println("Usuario " + u.getIdUsuario() + " sube al ascensor.");
        notifyAll(); // Notifica al ascensor que ya ha subido para iniciar el trayecto
    }

    /**
     * Método ejecutado por el usuario: espera dentro del ascensor a llegar a su destino para bajar.
     */
    public synchronized void esperarYBajar(Usuario u) throws InterruptedException {
        // Espera a que el ascensor alcance su destino
        while (pisoActual != u.getPisoDestino()) {
            wait();
        }
        System.out.println("Usuario " + u.getIdUsuario() + " baja en piso " + u.getPisoDestino());
        viajeCompletado = true;
        notifyAll(); // Notifica al ascensor que ha bajado
    }

    /**
     * Método ejecutado por el ascensor para procesar el siguiente usuario de la cola.
     */
    public synchronized void atenderSiguienteUsuario() throws InterruptedException {
        // Espera si no hay usuarios en cola y todavía no ha terminado el registro
        while (colaUsuarios.isEmpty() && !todosAtendidos) {
            wait();
        }

        if (colaUsuarios.isEmpty() && todosAtendidos) {
            return; // No quedan más usuarios por atender
        }

        // Obtener el siguiente usuario a atender
        usuarioActual = colaUsuarios.poll();
        usuarioDentro = false;
        viajeCompletado = false;

        // 1. Mover el ascensor piso a piso hasta el origen del usuario
        while (pisoActual != usuarioActual.getPisoOrigen()) {
            moverHacia(usuarioActual.getPisoOrigen());
            Thread.sleep(300); // Pequeña pausa para simular el desplazamiento
        }

        // Notificar al usuario que el ascensor ha llegado a su piso
        notifyAll();

        // 2. Esperar a que el usuario confirme que ha subido
        while (!usuarioDentro) {
            wait();
        }

        // 3. Mover el ascensor piso a piso hasta el destino del usuario
        while (pisoActual != usuarioActual.getPisoDestino()) {
            moverHacia(usuarioActual.getPisoDestino());
            Thread.sleep(300);
        }

        // Notificar al usuario que se ha alcanzado su piso destino
        notifyAll();

        // 4. Esperar a que el usuario baje del ascensor
        while (!viajeCompletado) {
            wait();
        }

        // Dejar el ascensor libre para la siguiente solicitud
        usuarioActual = null;
    }

    public synchronized boolean quedanUsuariosPendientes() {
        return !colaUsuarios.isEmpty() || !todosAtendidos;
    }
}