package model;

public class Estudiante {
    private int estudianteId;
    private String identificacion;
    private String nombre;
    private String correoInstitucional;
    private String correoPersonal;
    private String telefono;
    private boolean esVocero;
    private String tipoDocumento;
    private String genero;
    private String comentarios;

    // Constructor vacío
    public Estudiante() {
    }

    // Constructor completo
    public Estudiante(int estudianteId, String identificacion, String nombre,
                      String correoInstitucional, String correoPersonal,
                      String telefono, boolean esVocero, String tipoDocumento,
                      String genero, String comentarios) {
        this.estudianteId = estudianteId;
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correoInstitucional = correoInstitucional;
        this.correoPersonal = correoPersonal;
        this.telefono = telefono;
        this.esVocero = esVocero;
        this.tipoDocumento = tipoDocumento;
        this.genero = genero;
        this.comentarios = comentarios;
    }

    // Constructor sin ID (para INSERT)
    public Estudiante(String identificacion, String nombre,
                      String correoInstitucional, String correoPersonal,
                      String telefono, boolean esVocero, String tipoDocumento,
                      String genero, String comentarios) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correoInstitucional = correoInstitucional;
        this.correoPersonal = correoPersonal;
        this.telefono = telefono;
        this.esVocero = esVocero;
        this.tipoDocumento = tipoDocumento;
        this.genero = genero;
        this.comentarios = comentarios;
    }

    // Getters y Setters
    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getCorreoPersonal() {
        return correoPersonal;
    }

    public void setCorreoPersonal(String correoPersonal) {
        this.correoPersonal = correoPersonal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isEsVocero() {
        return esVocero;
    }

    public void setEsVocero(boolean esVocero) {
        this.esVocero = esVocero;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "estudianteId=" + estudianteId +
                ", identificacion='" + identificacion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correoInstitucional='" + correoInstitucional + '\'' +
                ", esVocero=" + esVocero +
                '}';
    }
}
