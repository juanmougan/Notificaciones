package ar.edu.uca.ingenieria.notificaciones;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;

import ar.edu.uca.ingenieria.notificaciones.adapter.NotificacionesAdapter;
import ar.edu.uca.ingenieria.notificaciones.config.SettingsActivity;
import ar.edu.uca.ingenieria.notificaciones.gcm.GcmIntentService;
import ar.edu.uca.ingenieria.notificaciones.gcm.GcmRegistrationCallback;
import ar.edu.uca.ingenieria.notificaciones.gcm.GooglePlayServicesUtil;
import ar.edu.uca.ingenieria.notificaciones.model.Notification;

/**
 * Main UI for the demo app.
 */
public class MainActivity extends ListActivity implements GcmRegistrationCallback {

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PREFERENCE_FIRST_RUN = "preferenceFirstRun";
    private static final String TAG = MainActivity.class.getSimpleName();

    Context context;    // TODO tal vez lo pueda volar...
    private GooglePlayServicesUtil googlePlayServicesUtil;
    private NotificacionesAdapter notificacionesAdapter;

    private void setupEmptyView() {
        View empty = findViewById(R.id.emptyElement);
        if (empty == null) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.activity_main, null);
            empty = (TextView) view.findViewById(R.id.emptyElement);
        }
        ListView list = this.getListView();
        list.setEmptyView(empty);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        this.googlePlayServicesUtil = new GooglePlayServicesUtil(this);
        this.googlePlayServicesUtil.setCallback(this);
        // TODO esto es asincronico
        intentarRegistrarGooglePlayServices();
    }

    @Override
    public void onAlreadyRegistered() {
        this.checkFirstRun();
        this.setupListView();
    }

    private void setupListView() {
        Notification notificacion;
        // TODO refactor - usar el Parcelable aca y consultar por notificacion != null
        // ver https://trello.com/c/N6KsGEjN
        if (this.getIntent().getExtras() != null) {
            notificacion = getNotificacionFromIntent();
            inicializarListView();
            this.notificacionesAdapter.add(notificacion);
        }
        this.setupEmptyView();
    }

    private void checkFirstRun() {
        if (isFirstRun()) {
            Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(openSettingsIntent);
        }
    }

    @Override
    public void onRePostStudent() {
        Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
        openSettingsIntent.putExtra(SettingsActivity.REPOST_NEEDED, true);
        this.startActivity(openSettingsIntent);
    }

    @Override
    public void onRegistrationSuccess() {
        this.checkFirstRun();
        this.setupListView();
    }

    @Override
    public void onRegistrationFailure() {
        MainActivity.this.finish();
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

    // TODO ver este no usa el getSharedPreferences del context, ojo - este usa un ApplicationContext
    // TODO comparar con el que esta en GPSU
    //private SharedPreferences getGcmPreferences(Context context) {
    // This sample app persists the registration ID in shared preferences, but
    // how you store the regID in your app is up to you.
    //    return getSharedPreferences(GooglePlayServicesUtil.class.getSimpleName(),
    //            Context.MODE_PRIVATE);
    //}

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

}