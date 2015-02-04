package ar.edu.uca.ingenieria.notificaciones;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.uca.ingenieria.notificaciones.adapter.NotificacionesAdapter;
import ar.edu.uca.ingenieria.notificaciones.gcm.GcmIntentService;
import ar.edu.uca.ingenieria.notificaciones.model.Notificacion;

/**
 * Main UI for the demo app.
 */
public class MainActivity extends Activity {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "GCM Demo";

    /**
     * Es el project number obtenido en la API Console, como se explica en "Getting Started."
     */
    String senderId;

    TextView mDisplay;
    List<Notificacion> notificaciones;
    private ListView notificacionesListView;

    GoogleCloudMessaging gcm;
    Context context;
    String regid;
    private NotificacionesAdapter notificacionesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mDisplay = (TextView) findViewById(R.id.display);

        context = getApplicationContext();
        senderId = getSenderId();
        intentarRegistrarGooglePlayServices();

        Notificacion notificacion = getNotificacionFromIntent();
        this.notificaciones = new ArrayList<Notificacion>();
        inicializarListView(notificacion);
    }

    private void inicializarListView(Notificacion notificacion) {
        this.notificaciones.add(notificacion);
        // Create the adapter to convert the array to views
        notificacionesAdapter = new NotificacionesAdapter(this, this.notificaciones);
        // Attach the adapter to a ListView
        this.notificacionesListView = (ListView) findViewById(R.id.lista_notificaciones);
        this.notificacionesListView.setAdapter(notificacionesAdapter);
    }

    private void agregarNotificacion(Notificacion notificacion) {
        this.notificaciones.add(notificacion);
        // this.notificacionesAdapter.add(notificacion);
        this.notificacionesAdapter.notifyDataSetChanged();
    }

    /**
     * Verifica si Google Play Services está instalado en el dispositivo. De ser así, intenta
     * registrarse y guarda el regid obtenido.
     */
    private void intentarRegistrarGooglePlayServices() {
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        Log.i(TAG, "regid: " + this.regid);
    }

    private Notificacion getNotificacionFromIntent() {
        String tituloNotificacion = (String) getIntent().getCharSequenceExtra(GcmIntentService.GCM_TITULO);
        Log.d(TAG, "Título: " + tituloNotificacion);
        String mensajeNotificacion = (String) getIntent().getCharSequenceExtra(GcmIntentService.GCM_MENSAJE);
        Log.d(TAG, "Mensaje: " + mensajeNotificacion);
        // TODO esto muestra "1/1/1970" si no viene la fecha...
        long fechaMilisegundos = getIntent().getLongExtra(GcmIntentService.GCM_FECHA, new Date(0).getTime());
        Date fechaNotificacion = new Date(fechaMilisegundos);
        return new Notificacion(tituloNotificacion, mensajeNotificacion, fechaNotificacion);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
        Log.i(TAG, "regid: " + this.regid);
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        this.setIntent(intent);
        this.agregarNotificacion(this.getNotificacionFromIntent());
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
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
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(senderId);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
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
                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    // TODO implementar esto del lado del server
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    // TODO: esto no es responsabilidad de esta clase...
    // Tal vez un "setting provider" o similar
    private String getSenderId() {
        Resources res = getResources();
        return res.getString(R.string.senderId);
    }

}