package ar.edu.uca.ingenieria.notificaciones.model;

/**
 * Modela una Notificación que será enviada por la Secretaría de la Facultad.
 * Esta clase es necesaria para poder tipar el {@link android.widget.ArrayAdapter}
 * Creado por juanmougan@gmail.com on 08/01/15.
 */
public class Notificacion {

    private String titulo;
    private String mensaje;

    public Notificacion(String titulo, String mensaje) {
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
