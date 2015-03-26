package ar.edu.uca.ingenieria.notificaciones.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ar.edu.uca.ingenieria.notificaciones.R;
import ar.edu.uca.ingenieria.notificaciones.model.Student;
import ar.edu.uca.ingenieria.notificaciones.webservice.StudentService;

public class SettingsActivity extends Activity {

    // TODO ademas del fragment, agregar botones?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
        createListenerForButton();
    }

    private void createListenerForButton() {
        Button sendButton = (Button) this.findViewById(R.id.sendStudentButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student s = obtenerDatosAlumno();
//                Student s = new Student.StudentBuilder().firstName("first name").
//                        lastName("last name").fileNumber("file number")./*career(Career.INFORMATICA).*/
//                        regid("regid").email("email@email.com").build();
                if (yaExisteAlumno()) {
                    StudentService.updateStudent(s, SettingsActivity.this);
                } else {
                    StudentService.createStudent(s, SettingsActivity.this);
                }
            }
        });
    }

    // TODO al ID lo estoy guardando en las SP por ahora
    private boolean yaExisteAlumno() {
        SharedPreferences prefs = this.getSharedPreferences(
                SettingsFragment.SETTINGS_PREFS_FILE,
                Context.MODE_PRIVATE);
        int id = prefs.getInt("student_id", 0);
        return id > 0;
    }

    // TODO mover a otra clase?
    private Student obtenerDatosAlumno() {
        SharedPreferences prefs = this.getSharedPreferences(
                SettingsFragment.SETTINGS_PREFS_FILE,
                Context.MODE_PRIVATE);
        // TODO Arreglar Carrera y completar regid
        Student student = new Student.StudentBuilder()
                .id(prefs.getInt("student_id", 0))
                .firstName(prefs.getString("pref_key_nombre_alumno", ""))
                .lastName(prefs.getString("pref_key_apellido_alumno", ""))
                .fileNumber(prefs.getString("pref_key_numero_registro", ""))
                .regid(prefs.getString("regid", ""))
                .email(prefs.getString("pref_key_email", ""))
                .build();
        return student;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Este Fragment contiene la configuración.
     */
    public static class SettingsFragment extends PreferenceFragment {

        private final static String TAG = SettingsFragment.class.getSimpleName();
        public final static String SETTINGS_PREFS_FILE = SettingsFragment.class.getName() + ".PREF_SETTINGS";

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Define the settings file to use by this settings fragment
            this.getPreferenceManager().setSharedPreferencesName(SETTINGS_PREFS_FILE);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
