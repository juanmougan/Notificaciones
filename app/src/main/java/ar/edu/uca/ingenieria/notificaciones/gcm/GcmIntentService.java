package ar.edu.uca.ingenieria.notificaciones.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Date;

import ar.edu.uca.ingenieria.notificaciones.MainActivity;
import ar.edu.uca.ingenieria.notificaciones.R;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Un {@link IntentService} específico, que maneja el mensaje GCM. Cuando el servicio termina, llama
 * a GcmBroadcastReceiver.completeWakefulIntent() para soltar el wake lock.
 */
public class GcmIntentService extends IntentService {
    public static int NOTIFICATION_ID = 1;
    private static final String TAG = GcmIntentService.class.getSimpleName();
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public static final String GCM_TITULO = "gcmTitulo";
    public static final String GCM_MENSAJE = "gcmMensaje";
    public static final String GCM_FECHA = "gcmFechaMensaje";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            String tituloNotificacion = getPrefijoTituloNotificacion() + " - " +
                    extras.getString(GCM_TITULO);       // TODO y si viene null? Puede pasar!
            String mensajeNotificacion = extras.getString(GCM_MENSAJE);
            Date fechaNotificacion = new Date(extras.getLong(GCM_FECHA));
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(tituloNotificacion, getGcmErrorEnviar() + mensajeNotificacion, fechaNotificacion);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(tituloNotificacion, getGcmMensajeBorrado() +
                        mensajeNotificacion, fechaNotificacion);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(tituloNotificacion, mensajeNotificacion, fechaNotificacion);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private Intent getIntentQueContieneLaNotificacion(String tituloNotificacion, String mensajeNotificacion, Date fechaNotificacion) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(GCM_TITULO, tituloNotificacion);
        intent.putExtra(GCM_MENSAJE, mensajeNotificacion);
        intent.putExtra(GCM_FECHA, fechaNotificacion.getTime());
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    /**
     * Pone el título y el mensaje en una notificación, y la postea (la muestra en status bar)
     *
     * @param titulo título de la notificación
     * @param msg    texto de la notificación
     */
    private void sendNotification(String titulo, String msg, Date fecha) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Acá invocamos a nuestra aplicación...
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                getIntentQueContieneLaNotificacion(titulo, msg, fecha), PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO este Builder debe tener algo que ver con las notificaciones - logo de la UCA?
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(titulo)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(++NOTIFICATION_ID, mBuilder.build());
    }

    // TODO: esto no es responsabilidad de esta clase...
    // Tal vez un "setting provider" o similar
    private String getPrefijoTituloNotificacion() {
        Resources res = getResources();
        return res.getString(R.string.titulo_notificacion);
    }

    // TODO: esto no es responsabilidad de esta clase...
    // Tal vez un "setting provider" o similar
    private String getGcmErrorEnviar() {
        Resources res = getResources();
        return res.getString(R.string.gcm_error_enviar);
    }

    // TODO: esto no es responsabilidad de esta clase...
    // Tal vez un "setting provider" o similar
    private String getGcmMensajeBorrado() {
        Resources res = getResources();
        return res.getString(R.string.gcm_mensaje_borrado);
    }

}
