package ar.edu.uca.ingenieria.notificaciones.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import ar.edu.uca.ingenieria.notificaciones.MainActivity;
import ar.edu.uca.ingenieria.notificaciones.R;

/**
 * Clase utilitaria que se encarga de verificar si Google Play Services está instalado correctamente.
 * Created by juanmougan@gmail.com on 26/03/15.
 */
public class GooglePlayServicesUtil {

    private static final String TAG = GooglePlayServicesUtil.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // TODO externalizar este String
    public static final String PLAY_SERVICES_ERROR_MSG = "Este dispositivo no soporta Google Play Services. No se podrá utilizar esta aplicación";
    private final Activity activity;
    private GcmRegistrationCallback callback;

    public String getRegid() {
        return regid;
    }

    private String regid;
    private GoogleCloudMessaging gcm;

    public GooglePlayServicesUtil(Activity activity) {
        this.activity = activity;
    }

    /**
     * Verifica que el teléfono tenga instalado Google Play Services. En caso contrario, solicita su
     * instalación.
     */
    public boolean checkPlayServices() {
        int resultCode = com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (com.google.android.gms.common.GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog(resultCode, this.activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, PLAY_SERVICES_ERROR_MSG);
                Toast.makeText(this.activity, PLAY_SERVICES_ERROR_MSG, Toast.LENGTH_LONG).show();
                this.activity.finish();
            }
            return false;
        }
        return true;
    }

    public void tryToRegisterGooglePlayServices() {
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (this.checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this.activity);
            regid = getRegistrationId(this.activity);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                this.callback.onRegistrationSuccess();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        Log.i(TAG, "regid: " + regid);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(MainActivity.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(MainActivity.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GooglePlayServicesUtil.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity.getApplicationContext());
                    }
                    regid = gcm.register(getSenderId());
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    // TODO esto solo lo deberia hacer SettingsActivity
                    // sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(activity.getApplicationContext(), regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "Response from Google: " + msg);
                GooglePlayServicesUtil.this.callback.onRePostStudent();
            }
        }.execute(null, null, null);
    }

    // TODO: esto no es responsabilidad de esta clase...
    // Tal vez un "setting provider" o similar

    /**
     * Es el project number obtenido en la API Console, como se explica en "Getting Started."
     *
     * @return el senderId que está persistido en las SharedPreferences
     */
    private String getSenderId() {
        Resources res = activity.getResources();
        return res.getString(R.string.senderId);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(MainActivity.PROPERTY_REG_ID, regId);
        editor.putInt(MainActivity.PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    public void setCallback(GcmRegistrationCallback callback) {
        this.callback = callback;
    }
}
