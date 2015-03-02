package ar.edu.uca.ingenieria.notificaciones.model;

/**
 * Modela un alumno. Copia de la contraparte del backend en:
 * https://github.com/juanmougan/backendNotificaciones/blob/master/api/models/Student.js
 * salvo por la lista de subscripción.
 * Created by juanmougan@gmail.com on 06/02/2015.
 */
public class Student {

    private String id;

    private String firstName;

    private String lastName;

    private String fileNumber;

    private Career career;

    private String regid;

    private String email;

    public Student() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
