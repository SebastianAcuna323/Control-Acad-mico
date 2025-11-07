package controllers;

import dao.CalificacionDAO;
import dao.CursoDAO;
import dao.EstudianteDAO;
import model.*;
import views.GestionCalificaciones;

import javax.swing.*;
import java.util.List;

public class CalificacionController {
    public CalificacionDAO calificacionDAO;
    private CursoDAO cursoDAO;
    private EstudianteDAO estudianteDAO;
    private GestionCalificaciones vista;

    public CalificacionController(GestionCalificaciones vista) {
        this.vista = vista;
        this.calificacionDAO = new CalificacionDAO();
        this.cursoDAO = new CursoDAO();
        this.estudianteDAO = new EstudianteDAO();
    }

    // ========== GESTIÓN DE ESTRUCTURA DE EVALUACIÓN ==========

    /**
     * Crear estructura de evaluación automática (3 cortes)
     */
    public void crearEstructuraEvaluacion(int cursoId, int periodoId) {
        if (cursoId <= 0 || periodoId <= 0) {
            vista.mostrarMensaje(
                    "Datos inválidos para crear estructura",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Verificar si ya existe estructura
        List<CorteEvaluacion> cortesExistentes = calificacionDAO.listarCortesPorCurso(cursoId);
        if (!cortesExistentes.isEmpty()) {
            int respuesta = vista.confirmarAccion(
                    "Ya existe una estructura de evaluación para este curso.\n" +
                            "¿Desea continuar de todos modos?",
                    "Estructura Existente"
            );
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        if (calificacionDAO.crearEstructuraEvaluacion(cursoId, periodoId)) {
            vista.mostrarMensaje(
                    "Estructura de evaluación creada exitosamente:\n\n" +
                            "• Primer Corte: 30%\n" +
                            "• Segundo Corte: 30%\n" +
                            "• Tercer Corte: 40%",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            cargarCortesDelCurso(cursoId);
        } else {
            vista.mostrarMensaje(
                    "Error al crear la estructura de evaluación",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Agregar componente a un corte
     */
    public void agregarComponente(int corteId, String nombreComponente, double porcentaje) {
        String mensajeValidacion = validarComponente(nombreComponente, porcentaje);
        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que la suma de porcentajes no exceda 100%
        List<ComponenteEvaluacion> componentes = calificacionDAO.listarComponentesPorCorte(corteId);
        double sumaActual = componentes.stream().mapToDouble(ComponenteEvaluacion::getPorcentaje).sum();

        if (sumaActual + porcentaje > 100) {
            vista.mostrarMensaje(
                    String.format("Error: La suma de porcentajes excedería el 100%%\n" +
                                    "Suma actual: %.1f%%\n" +
                                    "Intentando agregar: %.1f%%\n" +
                                    "Total resultante: %.1f%%",
                            sumaActual, porcentaje, sumaActual + porcentaje),
                    "Porcentaje Inválido",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        ComponenteEvaluacion componente = new ComponenteEvaluacion(
                corteId, nombreComponente, porcentaje
        );

        if (calificacionDAO.crearComponente(componente)) {
            vista.mostrarMensaje(
                    "Componente agregado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            cargarComponentesDelCorte(corteId);
        } else {
            vista.mostrarMensaje(
                    "Error al agregar el componente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========== GESTIÓN DE CALIFICACIONES ==========

    /**
     * Registrar calificación
     */
    public void registrarCalificacion(int estudianteId, int componenteId,
                                      double nota, String comentarios) {

        String mensajeValidacion = validarCalificacion(estudianteId, componenteId, nota);
        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar si ya existe
        if (calificacionDAO.existeCalificacion(estudianteId, componenteId)) {
            vista.mostrarMensaje(
                    "Ya existe una calificación para este estudiante en este componente.\n" +
                            "Use la opción 'Actualizar' para modificarla.",
                    "Calificación Existente",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Calificacion calificacion = new Calificacion(
                estudianteId, componenteId, nota, comentarios
        );

        if (calificacionDAO.registrarCalificacion(calificacion)) {
            vista.mostrarMensaje(
                    String.format("Calificación registrada: %.2f\nConcepto: %s",
                            nota, calificacion.getConceptoNota()),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormularioCalificacion();
        } else {
            vista.mostrarMensaje(
                    "Error al registrar la calificación",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Actualizar calificación
     */
    public void actualizarCalificacion(int calificacionId, double nota, String comentarios) {
        if (calificacionId <= 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar una calificación de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (nota < 0 || nota > 5.0) {
            vista.mostrarMensaje(
                    "La nota debe estar entre 0.0 y 5.0",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Calificacion calificacion = new Calificacion();
        calificacion.setCalificacionId(calificacionId);
        calificacion.setNota(nota);
        calificacion.setComentariosCalificacion(comentarios);

        if (calificacionDAO.actualizarCalificacion(calificacion)) {
            vista.mostrarMensaje(
                    String.format("Calificación actualizada: %.2f",nota),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormularioCalificacion();
        } else {
            vista.mostrarMensaje(
                    "Error al actualizar la calificación",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Eliminar calificación
     */
    public void eliminarCalificacion(int calificacionId) {
        if (calificacionId <= 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar una calificación de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirmacion = vista.confirmarAccion(
                "¿Está seguro de eliminar esta calificación?",
                "Confirmar Eliminación"
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        if (calificacionDAO.eliminarCalificacion(calificacionId)) {
            vista.mostrarMensaje(
                    "Calificación eliminada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormularioCalificacion();
        } else {
            vista.mostrarMensaje(
                    "Error al eliminar la calificación",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========== CARGA DE DATOS ==========

    public List<Curso> cargarCursos() {
        return cursoDAO.listarVista();
    }

    public List<Estudiante> cargarEstudiantes() {
        return estudianteDAO.listarTodos();
    }

    public void cargarCortesDelCurso(int cursoId) {
        List<CorteEvaluacion> cortes = calificacionDAO.listarCortesPorCurso(cursoId);
        vista.actualizarCortes(cortes);
    }

    public void cargarComponentesDelCorte(int corteId) {
        List<ComponenteEvaluacion> componentes = calificacionDAO.listarComponentesPorCorte(corteId);
        vista.actualizarComponentes(componentes);
    }

    public void cargarCalificacionesCurso(int cursoId) {
        List<Calificacion> calificaciones = calificacionDAO.listarCalificacionesDetalladas(cursoId);
        vista.actualizarTablaCalificaciones(calificaciones);
    }

    public void cargarCalificacionesEstudiante(int estudianteId, int cursoId) {
        List<Calificacion> calificaciones = calificacionDAO.obtenerCalificacionesEstudiante(
                estudianteId, cursoId
        );
        vista.actualizarTablaCalificaciones(calificaciones);
    }

    // ========== REPORTES Y CONSULTAS ==========

    /**
     * Mostrar nota final de un estudiante
     */
    public void mostrarNotaFinal(int estudianteId, int cursoId) {
        if (estudianteId <= 0 || cursoId <= 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un estudiante y un curso",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        CalificacionDAO.NotaFinal nf = calificacionDAO.calcularNotaFinal(estudianteId, cursoId);

        if (nf != null) {
            String concepto = nf.notaFinal >= 4.5 ? "Excelente" :
                    nf.notaFinal >= 4.0 ? "Sobresaliente" :
                            nf.notaFinal >= 3.5 ? "Bueno" :
                                    nf.notaFinal >= 3.0 ? "Aceptable" :
                                            nf.notaFinal >= 2.0 ? "Insuficiente" : "Deficiente";

            String estado = nf.notaFinal >= 3.0 ? "APROBADO" : "REPROBADO";

            String mensaje = String.format(
                    "📊 NOTA FINAL\n\n" +
                            "Estudiante: %s\n" +
                            "Curso: %s\n\n" +
                            "═══════════════════\n" +
                            "Nota Final: %.2f\n" +
                            "═══════════════════\n\n" +
                            "Concepto: %s\n" +
                            "Estado: %s",
                    nf.nombreEstudiante,
                    nf.nombreCurso,
                    nf.notaFinal,
                    concepto,
                    estado
            );

            vista.mostrarMensaje(mensaje, "Nota Final",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            vista.mostrarMensaje(
                    "No se encontraron calificaciones para calcular la nota final",
                    "Sin Datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Mostrar reporte completo del curso
     */
    public void mostrarReporteCurso(int cursoId) {
        if (cursoId <= 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un curso",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        List<CalificacionDAO.NotaFinal> notas = calificacionDAO.obtenerNotasFinalesCurso(cursoId);

        if (notas.isEmpty()) {
            vista.mostrarMensaje(
                    "No hay calificaciones registradas para este curso",
                    "Sin Datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        vista.mostrarReporteNotas(notas);
    }

    /**
     * Mostrar ranking del curso
     */
    public void mostrarRanking(int cursoId) {
        if (cursoId <= 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un curso",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        List<CalificacionDAO.NotaFinal> ranking = calificacionDAO.obtenerRankingCurso(cursoId);

        if (ranking.isEmpty()) {
            vista.mostrarMensaje(
                    "No hay calificaciones para generar el ranking",
                    "Sin Datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        vista.mostrarRanking(ranking);
    }

    // ========== VALIDACIONES ==========

    private String validarComponente(String nombreComponente, double porcentaje) {
        if (nombreComponente == null || nombreComponente.trim().isEmpty()) {
            return "El nombre del componente es obligatorio";
        }

        if (nombreComponente.trim().length() < 3) {
            return "El nombre debe tener al menos 3 caracteres";
        }

        if (porcentaje <= 0 || porcentaje > 100) {
            return "El porcentaje debe estar entre 0 y 100";
        }

        return null;
    }

    private String validarCalificacion(int estudianteId, int componenteId, double nota) {
        if (estudianteId <= 0) {
            return "Debe seleccionar un estudiante válido";
        }

        if (componenteId <= 0) {
            return "Debe seleccionar un componente de evaluación válido";
        }

        if (nota < 0.0 || nota > 5.0) {
            return "La nota debe estar entre 0.0 y 5.0";
        }

        return null;
    }

    /**
     * Obtener estadísticas generales
     */
    public String obtenerEstadisticas(int cursoId) {
        List<CalificacionDAO.NotaFinal> notas = calificacionDAO.obtenerNotasFinalesCurso(cursoId);

        if (notas.isEmpty()) {
            return "No hay calificaciones registradas";
        }

        double promedio = notas.stream()
                .mapToDouble(n -> n.notaFinal)
                .average()
                .orElse(0.0);

        long aprobados = notas.stream()
                .filter(n -> n.notaFinal >= 3.0)
                .count();

        long reprobados = notas.size() - aprobados;

        double notaMasAlta = notas.stream()
                .mapToDouble(n -> n.notaFinal)
                .max()
                .orElse(0.0);

        double notaMasBaja = notas.stream()
                .mapToDouble(n -> n.notaFinal)
                .min()
                .orElse(0.0);

        return String.format(
                "📊 ESTADÍSTICAS DEL CURSO\n\n" +
                        "Total de estudiantes: %d\n\n" +
                        "Promedio del curso: %.2f\n" +
                        "Nota más alta: %.2f\n" +
                        "Nota más baja: %.2f\n\n" +
                        "Aprobados: %d (%.1f%%)\n" +
                        "Reprobados: %d (%.1f%%)",
                notas.size(),
                promedio,
                notaMasAlta,
                notaMasBaja,
                aprobados, (aprobados * 100.0 / notas.size()),
                reprobados, (reprobados * 100.0 / notas.size())
        );
    }
}

