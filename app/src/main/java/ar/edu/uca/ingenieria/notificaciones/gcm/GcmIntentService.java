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

import ar.edu.uca.ingenieria.notificaciones.MainActivity;
import ar.edu.uca.ingenieria.notificaciones.R;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Un {@link IntentService} específico, que maneja el mensaje GCM. Cuando el servicio termina, llama
 * a GcmBroadcastReceiver.completeWakefulIntent() para soltar el wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = GcmIntentService.class.getSimpleName();
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public static final String GCM_TITULO = "gcmTitulo";
    public static final String GCM_MENSAJE = "gcmMensaje";

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
            String tituloNotificacion = extras.getString(GCM_TITULO);
            String mensajeNotificacion = extras.getString(GCM_MENSAJE);
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(tituloNotificacion, getGcmErrorEnviar() + mensajeNotificacion);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(tituloNotificacion, getGcmMensajeBorrado() +
                        mensajeNotificacion);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//                doFakeWork(extras);
                sendNotification(tituloNotificacion, mensajeNotificacion);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private Intent getIntentQueContieneLaNotificacion(String tituloNotificacion, String mensajeNotificacion) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(GCM_TITULO, tituloNotificacion);
        intent.putExtra(GCM_MENSAJE, mensajeNotificacion);
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    // TODO borrar este metodo
    private void doFakeWork(Bundle extras) {
        // This loop represents the service doing some work.
        for (int i=0; i<5; i++) {
            Log.i(TAG, "Working... " + (i + 1)
                    + "/5 @ " + SystemClock.elapsedRealtime());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
        // Post notification of received message.
        sendNotification("GCM", "Received: " + extras.toString());
        Log.i(TAG, "Received: " + extras.toString());
    }

    /**
     * Pone el título y el mensaje en una notificación, y la postea (la muestra en status bar)
     * @param titulo título de la notificación
     * @param msg texto de la notificación
     */
    private void sendNotification(String titulo, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // En teoría Marge, acá invocamos a nuestra aplicación...
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                getIntentQueContieneLaNotificacion(titulo, msg), PendingIntent.FLAG_UPDATE_CURRENT);

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
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    // TODO: esto no es responsabilidad de esta clase...
    // Tal vez un "setting provider" o similar
    private String getTituloNotificacion() {
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
