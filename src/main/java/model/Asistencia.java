package model;

import java.sql.Date;

public class Asistencia {
    private int asistenciaId;
    private int estudianteId;
    private int cursoId;
    private Date fechaClase;
    private String estadoAsistencia; // 'presente', 'ausente', 'tardanza'
    private String novedades;

    // Campos adicionales para mostrar información relacionada
    private String nombreEstudiante;
    private String identificacionEstudiante;
    private String nombreCurso;
    private int numeroClase;

    // Constructor vacío
    public Asistencia() {
    }

    // Constructor completo
    public Asistencia(int asistenciaId, int estudianteId, int cursoId,
                      Date fechaClase, String estadoAsistencia, String novedades) {
        this.asistenciaId = asistenciaId;
        this.estudianteId = estudianteId;
        this.cursoId = cursoId;
        this.fechaClase = fechaClase;
        this.estadoAsistencia = estadoAsistencia;
        this.novedades = novedades;
    }

    // Constructor sin ID (para INSERT)
    public Asistencia(int estudianteId, int cursoId, Date fechaClase,
                      String estadoAsistencia, String novedades) {
        this.estudianteId = estudianteId;
        this.cursoId = cursoId;
        this.fechaClase = fechaClase;
        this.estadoAsistencia = estadoAsistencia;
        this.novedades = novedades;
    }

    // Getters y Setters
    public int getAsistenciaId() {
        return asistenciaId;
    }

    public void setAsistenciaId(int asistenciaId) {
        this.asistenciaId = asistenciaId;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    public Date getFechaClase() {
        return fechaClase;
    }

    public void setFechaClase(Date fechaClase) {
        this.fechaClase = fechaClase;
    }

    public String getEstadoAsistencia() {
        return estadoAsistencia;
    }

    public void setEstadoAsistencia(String estadoAsistencia) {
        this.estadoAsistencia = estadoAsistencia;
    }

    public String getNovedades() {
        return novedades;
    }

    public void setNovedades(String novedades) {
        this.novedades = novedades;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getIdentificacionEstudiante() {
        return identificacionEstudiante;
    }

    public void setIdentificacionEstudiante(String identificacionEstudiante) {
        this.identificacionEstudiante = identificacionEstudiante;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public int getNumeroClase() {
        return numeroClase;
    }

    public void setNumeroClase(int numeroClase) {
        this.numeroClase = numeroClase;
    }


    public String getEstadoSimbolo() {
        switch (estadoAsistencia.toLowerCase()) {
            case "presente":
                return "✓";
            case "ausente":
                return "✗";
            case "tardanza":
                return "⚠";
            default:
                return "?";
        }
    }


     //Obtener color del estado

    public java.awt.Color getEstadoColor() {
        switch (estadoAsistencia.toLowerCase()) {
            case "presente":
                return new java.awt.Color(46, 204, 113); // Verde
            case "ausente":
                return new java.awt.Color(231, 76, 60); // Rojo
            case "tardanza":
                return new java.awt.Color(241, 196, 15); // Amarillo
            default:
                return java.awt.Color.GRAY;
        }
    }

    @Override
    public String toString() {
        return "Asistencia{" +
                "asistenciaId=" + asistenciaId +
                ", estudiante='" + nombreEstudiante + '\'' +
                ", curso='" + nombreCurso + '\'' +
                ", fecha=" + fechaClase +
                ", estado='" + estadoAsistencia + '\'' +
                '}';
    }
}
