package ar.edu.uca.ingenieria.notificaciones.gcm;

/**
 * Callback para sincronizar el POSTeo de un nuevo regid
 * Created by juanmougan@gmail.com on 03/04/15.
 */
public interface GcmRegistrationCallback {

    /**
     * Hay un regid, se puede proseguir con la Activity.
     */
    void onAlreadyRegistered();

    /**
     * Se registró exitosamente
     */
    void onRegistrationSuccess();

    /**
     * Hay que re POSTear al Alumno, porque el regid cambió.
     */
    void onRePostStudent();

    /**
     * No se puede registrar a GCM.
     */
    void onRegistrationFailure();

}
