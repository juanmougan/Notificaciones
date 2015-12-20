package ar.edu.uca.ingenieria.notificaciones.webservice;

import com.google.gson.Gson;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Factory estática de servicios web usando Generics.
 */
public class WebServiceFactory {

//    private static final String BASE_URL = "http://192.168.56.1";    // GENYMOTION
//    private static final String BASE_URL = "http://10.0.3.2";        // URL alternativa
//    private static final String BASE_URL = "http://172.29.90.176";     // Depende de la red...
    //private static final String BASE_URL = "http://192.168.1.107";     // Depende de la red...
    private static final String BASE_URL = "http://192.168.0.104";     // Depende de la red...
    private static final String PORT = "3000";
    public static String SUFFIX = "/api/v1";
    private static final String ENDPOINT = BASE_URL + ":" + PORT + SUFFIX;

    public static <T> T getWebService(Class<T> webServiceClass) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(new Gson()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return restAdapter.create(webServiceClass);
    }

}
