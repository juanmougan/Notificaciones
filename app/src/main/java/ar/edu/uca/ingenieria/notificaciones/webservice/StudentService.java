package ar.edu.uca.ingenieria.notificaciones.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

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
                StudentService.showToastError(context, retrofitError.getMessage());
            }
        });

    }

    // TODO Mientras esta clase tenga métodos estáticos, este método va a ser un asco...
    public static void updateStudent(final Student student, final Context context) {
        StudentWebService webService = WebServiceFactory.getWebService(StudentWebService.class);
        // Buscar el Student en el backend
        webService.getStudents(student.getFirstName(), student.getLastName(), student.getFileNumber(), new Callback<List<Student>>() {
            @Override
            public void success(List<Student> students, Response response) {
                if (students.size() == 0) {
                    StudentService.showToastError(context, "No se encontró el alumno");
                } else {
                    Student studentToUpdate = students.get(0);
                    studentToUpdate.setRegid(student.getRegid());
                    Log.d(TAG, studentToUpdate.toString());
                    performUpdateStudent(studentToUpdate, context);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error confirmando los datos del alumno: " + error);
                StudentService.showToastError(context, error.getMessage());
            }
        });
    }

    private static void showToastError(Context context, String error) {
        Toast.makeText(context, "Error confirmando los datos del alumno: " + error,
                LENGTH_LONG).show();
    }

    private static void performUpdateStudent(Student student, final Context context) {
        StudentWebService webService = WebServiceFactory.getWebService(StudentWebService.class);
        final SharedPreferences prefs = context.getSharedPreferences(
                SettingsActivity.SettingsFragment.SETTINGS_PREFS_FILE,
                Context.MODE_PRIVATE);

        // TODO refactor!!! https://trello.com/c/OvbyOlWC
        webService.updateStudent(student, student.getId(), new Callback<Student>() {
            @Override
            public void success(Student student, Response response) {
                Toast.makeText(context, "El alumno se actualizó correctamente", LENGTH_LONG).show();
                ((Activity) context).finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error: " + error);
                StudentService.showToastError(context, error.getMessage());
            }
        });
    }

}
