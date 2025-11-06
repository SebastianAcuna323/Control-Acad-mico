package model;

import java.sql.Date;

public class PeriodoAcademico {

    private int periodoAcademicoId;
    private String nombrePeriodo;
    private Date fechaInicio;
    private Date fechaFin;

    public PeriodoAcademico() {}

    public PeriodoAcademico(int periodoAcademicoId, String nombrePeriodo,
                            Date fechaInicio, Date fechaFin) {
        this.periodoAcademicoId = periodoAcademicoId;
        this.nombrePeriodo = nombrePeriodo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public PeriodoAcademico(String nombrePeriodo, Date fechaInicio, Date fechaFin) {
        this.nombrePeriodo = nombrePeriodo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters
    public int getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(int periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getNombrePeriodo() { return nombrePeriodo; }
    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return nombrePeriodo;
    }
}
