package ar.edu.uca.ingenieria.notificaciones.webservice;

import java.util.List;

import ar.edu.uca.ingenieria.notificaciones.model.Student;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Webservice usado para comunicarse con el backend
 * Created by juanmougan@gmail.com on 18/02/15.
 */
public interface StudentWebService {

    @POST("/student")
    void createStudent(@Body Student student, Callback<Student> callback);

    @GET("/students")
    void getStudents(@Query("first_name") String firstName,
                     @Query("last_name") String lastName,
                     @Query("file_number") String fileNumber,
                     Callback<List<Student>> callback);

    @PUT("/students/{id}")
    void updateStudent(@Body Student student, @Path("id") int id, Callback<Student> callback);

}
