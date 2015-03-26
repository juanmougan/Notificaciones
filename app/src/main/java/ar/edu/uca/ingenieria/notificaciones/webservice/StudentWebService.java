package ar.edu.uca.ingenieria.notificaciones.webservice;

import ar.edu.uca.ingenieria.notificaciones.model.Student;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * Webservice usado para comunicarse con el backend
 * Created by juanmougan@gmail.com on 18/02/15.
 */
public interface StudentWebService {

    @POST("/student")
    void createStudent(@Body Student student, Callback<Student> callback);

    @PUT("/student")
    void updateStudent(@Body Student student, @Query("id") int id, Callback<Student> callback);

}
