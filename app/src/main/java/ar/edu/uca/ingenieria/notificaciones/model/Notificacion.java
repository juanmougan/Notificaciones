package ar.edu.uca.ingenieria.notificaciones.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;

/**
 * Modela una Notificación que será enviada por la Secretaría de la Facultad.
 * Esta clase es necesaria para poder tipar el {@link android.widget.ArrayAdapter}
 * Creado por juanmougan@gmail.com on 08/01/15.
 */
public class Notificacion implements Parcelable {

    private String titulo;
    private String mensaje;
    private Date fechaMensaje;
    private static final String TAG = Notificacion.class.getSimpleName();

    public Notificacion(String titulo, String mensaje, Date fechaMensaje) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaMensaje = fechaMensaje;
    }

    public Notificacion(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);
        Log.d(TAG, "Leyendo del Parcel...");
        Log.d(TAG, "titulo: " + data[0]);
        Log.d(TAG, "mensaje: " + data[1]);
        this.titulo = data[0];
        this.mensaje = data[1];
        this.fechaMensaje = new Date(in.readLong());
        Log.d(TAG, "fecha: " + this.fechaMensaje);
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

    public Date getFechaMensaje() {
        return fechaMensaje;
    }

    public void setFechaMensaje(Date fechaMensaje) {
        this.fechaMensaje = fechaMensaje;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "Escribiendo en el Parcel...");
        Log.d(TAG, "titulo: " + this.titulo);
        Log.d(TAG, "mensaje: " + this.mensaje);
        Log.d(TAG, "fecha: " + this.fechaMensaje);
        dest.writeStringArray(new String[]{this.titulo, this.mensaje});
        dest.writeLong(this.fechaMensaje.getTime());
    }

    public static final Creator CREATOR = new Creator<Notificacion>() {

        @Override
        public Notificacion createFromParcel(Parcel in) {
            return new Notificacion(in);
        }

        @Override
        public Notificacion[] newArray(int size) {
            return new Notificacion[size];
        }
    };
}
