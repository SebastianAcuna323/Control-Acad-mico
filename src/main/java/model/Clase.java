package model;

import java.sql.Date;

public class Clase {
    private int claseId;
    private int cursoId;
    private int numeroClase;
    private Date fechaClase;
    private String temaClase;
    private String descripcionClase;
    private String comentariosClase;

    private String nombreCurso;

    public Clase() {}

    public Clase(int claseId, int cursoId, int numeroClase, Date fechaClase,
                 String temaClase, String descripcionClase, String comentariosClase) {
        this.claseId = claseId;
        this.cursoId = cursoId;
        this.numeroClase = numeroClase;
        this.fechaClase = fechaClase;
        this.temaClase = temaClase;
        this.descripcionClase = descripcionClase;
        this.comentariosClase = comentariosClase;
    }

    public Clase(int cursoId, int numeroClase, Date fechaClase,
                 String temaClase, String descripcionClase, String comentariosClase) {
        this.cursoId = cursoId;
        this.numeroClase = numeroClase;
        this.fechaClase = fechaClase;
        this.temaClase = temaClase;
        this.descripcionClase = descripcionClase;
        this.comentariosClase = comentariosClase;
    }

    // Getters y Setters
    public int getClaseId() { return claseId; }
    public void setClaseId(int claseId) { this.claseId = claseId; }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public int getNumeroClase() { return numeroClase; }
    public void setNumeroClase(int numeroClase) {
        this.numeroClase = numeroClase;
    }

    public Date getFechaClase() { return fechaClase; }
    public void setFechaClase(Date fechaClase) {
        this.fechaClase = fechaClase;
    }

    public String getTemaClase() { return temaClase; }
    public void setTemaClase(String temaClase) {
        this.temaClase = temaClase;
    }

    public String getDescripcionClase() { return descripcionClase; }
    public void setDescripcionClase(String descripcionClase) {
        this.descripcionClase = descripcionClase;
    }

    public String getComentariosClase() { return comentariosClase; }
    public void setComentariosClase(String comentariosClase) {
        this.comentariosClase = comentariosClase;
    }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    @Override
    public String toString() {
        return "Clase #" + numeroClase + " - " + temaClase;
    }
}