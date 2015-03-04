package ar.edu.uca.ingenieria.notificaciones.webservice;

import com.google.gson.Gson;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Factory estática de servicios web usando Generics.
 */
public class WebServiceFactory {

    private static final String ENDPOINT = "http://192.168.56.1:1337";  // GENYMOTION

    public static <T> T getWebService(Class<T> webServiceClass) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(new Gson()))
                .build();
        return restAdapter.create(webServiceClass);
    }

}
