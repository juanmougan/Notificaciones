package ar.edu.uca.ingenieria.notificaciones.config;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import ar.edu.uca.ingenieria.notificaciones.R;
import ar.edu.uca.ingenieria.notificaciones.model.Career;
import ar.edu.uca.ingenieria.notificaciones.model.Student;
import ar.edu.uca.ingenieria.notificaciones.webservice.StudentService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
                StudentService.createStudent(s, SettingsActivity.this);
            }
        });
    }

    // TODO mover a otra clase? Ojo, preciso un Context
    private Student obtenerDatosAlumno() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Student student = new Student.StudentBuilder()
                .firstName(prefs.getString("firstName", ""))
                .lastName(prefs.getString("lastName", ""))
                .fileNumber(prefs.getString("fileNumber", ""))
                .regid(prefs.getString("regid", ""))
                .email(prefs.getString("email", ""))
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

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
