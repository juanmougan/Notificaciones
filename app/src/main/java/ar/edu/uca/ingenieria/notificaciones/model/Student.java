package ar.edu.uca.ingenieria.notificaciones.model;

/**
 * Modela un alumno. Copia de la contraparte del backend en:
 * https://github.com/juanmougan/backendNotificaciones/blob/master/api/models/Student.js
 * salvo por la lista de subscripción.
 * Created by juanmougan@gmail.com on 06/02/2015.
 */
public class Student {

    private int id;

    private String firstName;

    private String lastName;

    private String fileNumber;

    private Career career;

    private String regid;

    private String email;

    public Student() {
    }

    public Student(StudentBuilder studentBuilder) {
        this.id = studentBuilder.id;
        this.firstName = studentBuilder.firstName;
        this.lastName = studentBuilder.lastName;
        this.fileNumber = studentBuilder.fileNumber;
        this.career = studentBuilder.career;
        this.regid = studentBuilder.regid;
        this.email = studentBuilder.email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public static class StudentBuilder {

        private int id;

        private String firstName;

        private String lastName;

        private String fileNumber;

        private Career career;

        private String regid;

        private String email;

        public StudentBuilder() {
        }

        public StudentBuilder id(int id) {
            this.id = id;
            return this;
        }

        public StudentBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public StudentBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public StudentBuilder fileNumber(String fileNumber) {
            this.fileNumber = fileNumber;
            return this;
        }

        public StudentBuilder career(Career career) {
            this.career = career;
            return this;
        }

        public StudentBuilder regid(String regid) {
            this.regid = regid;
            return this;
        }

        public StudentBuilder email(String email) {
            this.email = email;
            return this;
        }

        public Student build() {
            return new Student(this);
        }

    }

}
