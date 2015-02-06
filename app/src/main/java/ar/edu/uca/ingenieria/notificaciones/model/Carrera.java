package ar.edu.uca.ingenieria.notificaciones.model;

/**
 * Modela una Carrera (incluyendo Ciclo Común).
 * Created by juanmougan@gmail.com on 06/02/2015.
 */
public enum Carrera {

    CICLO_COMUN("Ciclo Común"),
    AMBIENTAL("Ambiental"),
    CIVIL("Civil"),
    ELECTRONICA("Electrónica"),
    INDUSTRIAL("Industrial"),
    INFORMATICA("Informática");

    private String nombre;

    Carrera(String name) {
        this.nombre = name;
    }

    @Override
    public String toString() {
        return this.nombre;
    }

}
