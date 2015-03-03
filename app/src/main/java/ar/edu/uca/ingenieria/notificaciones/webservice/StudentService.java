package ar.edu.uca.ingenieria.notificaciones.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import ar.edu.uca.ingenieria.notificaciones.model.Student;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Implementación del servicio que se comunica con el backend.
 */
public class StudentService {

    public static void createStudent(Student student, final Context context) {
        StudentWebService webService = WebServiceFactory.getWebService(StudentWebService.class);
        webService.createStudent(student, new Callback<Student>() {
            @Override
            public void success(Student createdStudent, Response response) {
                Toast.makeText(context, "Datos guardados. ID = " + createdStudent.getId(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(context, "Error: " + retrofitError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
