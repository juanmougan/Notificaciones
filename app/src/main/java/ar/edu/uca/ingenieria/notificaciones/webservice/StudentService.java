package ar.edu.uca.ingenieria.notificaciones.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import ar.edu.uca.ingenieria.notificaciones.model.Student;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Implementación del servicio que se comunica con el backend.
 */
public class StudentService {

    private static final String TAG = StudentService.class.getSimpleName();

    public static void createStudent(Student student, final Context context) {
        StudentWebService webService = WebServiceFactory.getWebService(StudentWebService.class);
        webService.createStudent(student, new Callback<Student>() {
            @Override
            public void success(Student createdStudent, Response response) {
                Log.d(TAG, "Se creó el Alumno con id: " + createdStudent.getId());
                Toast.makeText(context, "Datos guardados. ID = " + createdStudent.getId(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Error: " + retrofitError);
                Toast.makeText(context, "Error: " + retrofitError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
