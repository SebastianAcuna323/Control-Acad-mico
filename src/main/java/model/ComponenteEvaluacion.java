package model;

public class ComponenteEvaluacion {
    private int componenteEvaluacionId;
    private int corteEvaluacionId;
    private String nombreComponente;
    private double porcentaje;

    // Información relacionada
    private String nombreCorte;
    private double porcentajeCorte;

    // Constructor vacío
    public ComponenteEvaluacion() {
    }

    // Constructor completo
    public ComponenteEvaluacion(int componenteEvaluacionId, int corteEvaluacionId,
                                String nombreComponente, double porcentaje) {
        this.componenteEvaluacionId = componenteEvaluacionId;
        this.corteEvaluacionId = corteEvaluacionId;
        this.nombreComponente = nombreComponente;
        this.porcentaje = porcentaje;
    }

    // Constructor sin ID
    public ComponenteEvaluacion(int corteEvaluacionId, String nombreComponente,
                                double porcentaje) {
        this.corteEvaluacionId = corteEvaluacionId;
        this.nombreComponente = nombreComponente;
        this.porcentaje = porcentaje;
    }

    // Getters y Setters
    public int getComponenteEvaluacionId() {
        return componenteEvaluacionId;
    }

    public void setComponenteEvaluacionId(int componenteEvaluacionId) {
        this.componenteEvaluacionId = componenteEvaluacionId;
    }

    public int getCorteEvaluacionId() {
        return corteEvaluacionId;
    }

    public void setCorteEvaluacionId(int corteEvaluacionId) {
        this.corteEvaluacionId = corteEvaluacionId;
    }

    public String getNombreComponente() {
        return nombreComponente;
    }

    public void setNombreComponente(String nombreComponente) {
        this.nombreComponente = nombreComponente;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getNombreCorte() {
        return nombreCorte;
    }

    public void setNombreCorte(String nombreCorte) {
        this.nombreCorte = nombreCorte;
    }

    public double getPorcentajeCorte() {
        return porcentajeCorte;
    }

    public void setPorcentajeCorte(double porcentajeCorte) {
        this.porcentajeCorte = porcentajeCorte;
    }

    /**
     * Calcular porcentaje real sobre el total del curso
     */
    public double calcularPorcentajeSobreTotal() {
        if (porcentajeCorte > 0) {
            return (porcentaje * porcentajeCorte) / 100.0;
        }
        return 0;
    }

    @Override
    public String toString() {
        return nombreComponente + " (" + porcentaje + "%)";
    }

    /**
     * Validar que el porcentaje sea válido
     */
    public boolean validarPorcentaje() {
        return porcentaje > 0 && porcentaje <= 100;
    }
}
