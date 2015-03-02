package ar.edu.uca.ingenieria.notificaciones.webservice;

import android.os.AsyncTask;

import ar.edu.uca.ingenieria.notificaciones.model.Student;

/**
 * Implementación del servicio que se comunica con el backend.
 */
public class StudentService {

    public static Student createStudent(final Student student) {
        final StudentWebService webService = WebServiceFactory.getWebService(StudentWebService.class);
        new AsyncTask<Void, Void, Student>() {
            @Override
            protected Student doInBackground(Void... params) {
                return webService.createStudent(student);
            }
        };
        return null;
    }

}
