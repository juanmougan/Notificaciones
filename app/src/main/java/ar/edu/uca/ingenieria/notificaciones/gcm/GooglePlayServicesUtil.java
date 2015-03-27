package ar.edu.uca.ingenieria.notificaciones.gcm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

/**
 * Clase utilitaria que se encarga de verificar si Google Play Services está instalado correctamente.
 * Created by juanmougan@gmail.com on 26/03/15.
 */
public class GooglePlayServicesUtil {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // TODO externalizar este String
    public static final String PLAY_SERVICES_ERROR_MSG = "Este dispositivo no soporta Google Play Services. No se podrá utilizar esta aplicación";

    /**
     * Verifica que el teléfono tenga instalado Google Play Services. En caso contrario, solicita su
     * instalación.
     *
     */
    public static boolean checkPlayServices(Activity activity) {
        int resultCode = com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (com.google.android.gms.common.GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("GooglePlayServicesUtil", PLAY_SERVICES_ERROR_MSG);
                Toast.makeText(activity, PLAY_SERVICES_ERROR_MSG, Toast.LENGTH_LONG).show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

}
