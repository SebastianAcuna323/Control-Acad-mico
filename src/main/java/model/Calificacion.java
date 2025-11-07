package model;

public class Calificacion {

    private int calificacionId;
    private int estudianteId;
    private int componenteEvaluacionId;
    private double nota;
    private String comentariosCalificacion;

    // Información relacionada
    private String nombreEstudiante;
    private String identificacionEstudiante;
    private String nombreComponente;
    private String nombreCorte;
    private String nombreCurso;
    private double porcentajeComponente;
    private double porcentajeCorte;

    // Constantes para escalas de calificación
    public static final double NOTA_MINIMA = 0.0;
    public static final double NOTA_MAXIMA = 5.0;
    public static final double NOTA_APROBATORIA = 3.0;

    // Constructor vacío
    public Calificacion() {
    }

    // Constructor completo
    public Calificacion(int calificacionId, int estudianteId,
                        int componenteEvaluacionId, double nota,
                        String comentariosCalificacion) {
        this.calificacionId = calificacionId;
        this.estudianteId = estudianteId;
        this.componenteEvaluacionId = componenteEvaluacionId;
        this.nota = nota;
        this.comentariosCalificacion = comentariosCalificacion;
    }

    // Constructor sin ID
    public Calificacion(int estudianteId, int componenteEvaluacionId,
                        double nota, String comentariosCalificacion) {
        this.estudianteId = estudianteId;
        this.componenteEvaluacionId = componenteEvaluacionId;
        this.nota = nota;
        this.comentariosCalificacion = comentariosCalificacion;
    }

    // Getters y Setters
    public int getCalificacionId() {
        return calificacionId;
    }

    public void setCalificacionId(int calificacionId) {
        this.calificacionId = calificacionId;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public int getComponenteEvaluacionId() {
        return componenteEvaluacionId;
    }

    public void setComponenteEvaluacionId(int componenteEvaluacionId) {
        this.componenteEvaluacionId = componenteEvaluacionId;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getComentariosCalificacion() {
        return comentariosCalificacion;
    }

    public void setComentariosCalificacion(String comentariosCalificacion) {
        this.comentariosCalificacion = comentariosCalificacion;
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

    public String getNombreComponente() {
        return nombreComponente;
    }

    public void setNombreComponente(String nombreComponente) {
        this.nombreComponente = nombreComponente;
    }

    public String getNombreCorte() {
        return nombreCorte;
    }

    public void setNombreCorte(String nombreCorte) {
        this.nombreCorte = nombreCorte;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public double getPorcentajeComponente() {
        return porcentajeComponente;
    }

    public void setPorcentajeComponente(double porcentajeComponente) {
        this.porcentajeComponente = porcentajeComponente;
    }

    public double getPorcentajeCorte() {
        return porcentajeCorte;
    }

    public void setPorcentajeCorte(double porcentajeCorte) {
        this.porcentajeCorte = porcentajeCorte;
    }

    /**
     * Calcular nota ponderada del componente
     */
    public double calcularNotaPonderadaComponente() {
        return (nota * porcentajeComponente) / 100.0;
    }

    /**
     * Calcular aporte a la nota final del curso
     */
    public double calcularAporteNotaFinal() {
        return (nota * porcentajeComponente / 100.0) * (porcentajeCorte / 100.0);
    }

    /**
     * Obtener concepto cualitativo de la nota
     */
    public String getConceptoNota() {
        if (nota >= 4.5) return "Excelente";
        if (nota >= 4.0) return "Sobresaliente";
        if (nota >= 3.5) return "Bueno";
        if (nota >= 3.0) return "Aceptable";
        if (nota >= 2.0) return "Insuficiente";
        return "Deficiente";
    }

    /**
     * Obtener color según la nota
     */
    public java.awt.Color getColorNota() {
        if (nota >= 4.5) return new java.awt.Color(39, 174, 96);   // Verde oscuro
        if (nota >= 4.0) return new java.awt.Color(46, 204, 113);  // Verde
        if (nota >= 3.5) return new java.awt.Color(52, 152, 219);  // Azul
        if (nota >= 3.0) return new java.awt.Color(241, 196, 15);  // Amarillo
        if (nota >= 2.0) return new java.awt.Color(230, 126, 34);  // Naranja
        return new java.awt.Color(231, 76, 60);                     // Rojo
    }

    /**
     * Validar que la nota esté en el rango permitido
     */
    public boolean validarNota() {
        return nota >= NOTA_MINIMA && nota <= NOTA_MAXIMA;
    }

    /**
     * Verificar si la nota es aprobatoria
     */
    public boolean esAprobatoria() {
        return nota >= NOTA_APROBATORIA;
    }

    @Override
    public String toString() {
        return String.format("Calificacion{estudiante='%s', componente='%s', nota=%.2f (%s)}",
                nombreEstudiante, nombreComponente, nota, getConceptoNota());
    }
}

