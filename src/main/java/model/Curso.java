package model;

public class Curso {
    private int cursoId;
    private String nombreCurso;
    private int periodoAcademicoId;
    private int docenteId;
    private String descripcionCurso;
    private String nombrePeriodo;
    private String nombreDocente;

    public Curso() {}

    public Curso(int cursoId, String nombreCurso, int periodoAcademicoId,
                 int docenteId, String descripcionCurso) {
        this.cursoId = cursoId;
        this.nombreCurso = nombreCurso;
        this.periodoAcademicoId = periodoAcademicoId;
        this.docenteId = docenteId;
        this.descripcionCurso = descripcionCurso;
    }

    public Curso(String nombreCurso, int periodoAcademicoId,
                 int docenteId, String descripcionCurso) {
        this.nombreCurso = nombreCurso;
        this.periodoAcademicoId = periodoAcademicoId;
        this.docenteId = docenteId;
        this.descripcionCurso = descripcionCurso;
    }

    // Getters y Setters
    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public int getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(int periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public int getDocenteId() { return docenteId; }
    public void setDocenteId(int docenteId) {
        this.docenteId = docenteId;
    }

    public String getDescripcionCurso() { return descripcionCurso; }
    public void setDescripcionCurso(String descripcionCurso) {
        this.descripcionCurso = descripcionCurso;
    }

    public String getNombrePeriodo() { return nombrePeriodo; }
    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    public String getNombreDocente() { return nombreDocente; }
    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    @Override
    public String toString() {
        return nombreCurso;
    }
}
