package ar.edu.uca.ingenieria.notificaciones.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import ar.edu.uca.ingenieria.notificaciones.config.SettingsActivity;
import ar.edu.uca.ingenieria.notificaciones.model.Student;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Implementación del servicio que se comunica con el backend.
 */
public class StudentService {

    private static final String TAG = StudentService.class.getSimpleName();

    public static void createStudent(Student student, final Context context) {
        StudentWebService webService = WebServiceFactory.getWebService(StudentWebService.class);
        final SharedPreferences prefs = context.getSharedPreferences(
                SettingsActivity.SettingsFragment.SETTINGS_PREFS_FILE,
                Context.MODE_PRIVATE);
        // TODO refactor!!! https://trello.com/c/OvbyOlWC
        webService.createStudent(student, new Callback<Student>() {
            @Override
            public void success(Student createdStudent, Response response) {
                Log.d(TAG, "INSERT Student id: " + createdStudent.getId());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("student_id", createdStudent.getId());
                editor.apply();
                Toast.makeText(context, "El alumno se creó exitosamente", LENGTH_LONG).show();
                ((Activity) context).finish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Error: " + retrofitError);
                Toast.makeText(context, "Error creando el alumno: " + retrofitError.getMessage(),
                        LENGTH_LONG).show();
            }
        });
    }

    public static void updateStudent(Student student, final Context context) {
        StudentWebService webService = WebServiceFactory.getWebService(StudentWebService.class);
        final SharedPreferences prefs = context.getSharedPreferences(
                SettingsActivity.SettingsFragment.SETTINGS_PREFS_FILE,
                Context.MODE_PRIVATE);
        // TODO refactor!!! https://trello.com/c/OvbyOlWC
        webService.updateStudent(student, student.getId(), new Callback<Student>() {
            @Override
            public void success(Student student, Response response) {
                Log.d(TAG, "UPDATE Student id: " + student.getId());
                Toast.makeText(context, "El alumno se actualizó correctamente", LENGTH_LONG).show();
                ((Activity) context).finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(context, "Error actualizando el alumno: " + error.getMessage(),
                        LENGTH_LONG).show();
            }
        });
    }

}
