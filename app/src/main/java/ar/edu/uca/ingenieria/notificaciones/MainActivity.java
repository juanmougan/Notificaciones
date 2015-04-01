package ar.edu.uca.ingenieria.notificaciones;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import ar.edu.uca.ingenieria.notificaciones.gcm.GooglePlayServicesUtil;

import java.util.Date;

import ar.edu.uca.ingenieria.notificaciones.adapter.NotificacionesAdapter;
import ar.edu.uca.ingenieria.notificaciones.config.SettingsActivity;
import ar.edu.uca.ingenieria.notificaciones.gcm.GcmIntentService;
import ar.edu.uca.ingenieria.notificaciones.model.Notification;

/**
 * Main UI for the demo app.
 */
public class MainActivity extends ListActivity {

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PREFERENCE_FIRST_RUN = "preferenceFirstRun";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    /**
     * Es el project number obtenido en la API Console, como se explica en "Getting Started."
     */
    String senderId;

    GoogleCloudMessaging gcm;
    Context context;
    String regid;
    private GooglePlayServicesUtil googlePlayServicesUtil;
    private NotificacionesAdapter notificacionesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        this.googlePlayServicesUtil = new GooglePlayServicesUtil(this);
        if (isFirstRun()) {
            Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(openSettingsIntent);
        }
        intentarRegistrarGooglePlayServices();

        Notification notificacion;
        // TODO refactor - usar el Parcelable aca y consultar por notificacion != null
        // ver https://trello.com/c/N6KsGEjN
        if (this.getIntent().getExtras() != null) {
            notificacion = getNotificacionFromIntent();
            inicializarListView();
            this.notificacionesAdapter.add(notificacion);
        }
    }

    private void inicializarListView() {
        // Crear el Adapter y setearlo a la ListView que tiene esta ListActivity
        notificacionesAdapter = new NotificacionesAdapter(this);
        this.setListAdapter(notificacionesAdapter);
    }

    /**
     * Verifica si Google Play Services está instalado en el dispositivo. De ser así, intenta
     * registrarse y guarda el regid obtenido.
     */
    private void intentarRegistrarGooglePlayServices() {
        googlePlayServicesUtil.tryToRegisterGooglePlayServices();
    }

    private Notification getNotificacionFromIntent() {
        String tituloNotificacion = (String) getIntent().getCharSequenceExtra(GcmIntentService.GCM_TITULO);
        Log.d(TAG, "Título: " + tituloNotificacion);
        String mensajeNotificacion = (String) getIntent().getCharSequenceExtra(GcmIntentService.GCM_MENSAJE);
        Log.d(TAG, "Mensaje: " + mensajeNotificacion);
        // TODO esto muestra "1/1/1970" si no viene la fecha...
        long fechaMilisegundos = getIntent().getLongExtra(GcmIntentService.GCM_FECHA, new Date(0).getTime());
        Date fechaNotificacion = new Date(fechaMilisegundos);
        return new Notification(tituloNotificacion, mensajeNotificacion, fechaNotificacion);
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
        if (this.notificacionesAdapter == null) {
            this.inicializarListView();
        }
        this.notificacionesAdapter.add(this.getNotificacionFromIntent());
    }

    private boolean isFirstRun() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = p.getBoolean(PREFERENCE_FIRST_RUN, true);
        p.edit().putBoolean(PREFERENCE_FIRST_RUN, false).apply();
        return firstRun;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        return googlePlayServicesUtil.checkPlayServices();
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    // TODO ver si esto lo muevo a GPSU
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
     * @return Application's {@code SharedPreferences}.
     */
    // TODO ver este no usa el getSharedPreferences del context, ojo
    // TODO comparar con el que esta en GPSU
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(GooglePlayServicesUtil.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    // TODO mover a GPSU?
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

    // TODO implementar esto del lado del server
    // Ver https://trello.com/c/EDjnB9dm
    // La lógica correspondiente NO es responsabilidad de esta clase!

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

}