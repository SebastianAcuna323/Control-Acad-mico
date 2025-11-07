package controllers;



import dao.PeriodoAcademicoDAO;
import model.PeriodoAcademico;
import views.GestionPeriodos;
import javax.swing.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PeriodoAcademicoController {
    private PeriodoAcademicoDAO periodoDAO;
    private GestionPeriodos vista;

    public PeriodoAcademicoController(GestionPeriodos vista) {
        this.vista = vista;
        this.periodoDAO = new PeriodoAcademicoDAO();
    }


    public void crearPeriodo(String nombrePeriodo, Date fechaInicio, Date fechaFin) {
        // Validar datos
        String mensajeValidacion = validarDatosPeriodo(nombrePeriodo, fechaInicio, fechaFin);

        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear objeto periodo
        PeriodoAcademico periodo = new PeriodoAcademico(
                nombrePeriodo, fechaInicio, fechaFin
        );

        // Intentar guardar
        if (periodoDAO.crear(periodo)) {
            vista.mostrarMensaje(
                    "Periodo académico creado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarPeriodos();
        } else {
            vista.mostrarMensaje(
                    "Error al crear el periodo.\nPuede haber solapamiento de fechas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void actualizarPeriodo(int periodoId, String nombrePeriodo,
                                  Date fechaInicio, Date fechaFin) {
        if (periodoId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un periodo de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar datos
        String mensajeValidacion = validarDatosPeriodo(nombrePeriodo, fechaInicio, fechaFin);

        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear objeto periodo
        PeriodoAcademico periodo = new PeriodoAcademico(
                periodoId, nombrePeriodo, fechaInicio, fechaFin
        );

        // Intentar actualizar
        if (periodoDAO.actualizar(periodo)) {
            vista.mostrarMensaje(
                    "Periodo actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarPeriodos();
        } else {
            vista.mostrarMensaje(
                    "Error al actualizar el periodo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void eliminarPeriodo(int periodoId) {
        if (periodoId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un periodo de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Confirmar eliminación
        int confirmacion = vista.confirmarAccion(
                "¿Está seguro de eliminar este periodo?\n" +
                        "Esta acción puede fallar si el periodo tiene cursos asociados.",
                "Confirmar Eliminación"
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Intentar eliminar
        if (periodoDAO.eliminar(periodoId)) {
            vista.mostrarMensaje(
                    "Periodo eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarPeriodos();
        } else {
            vista.mostrarMensaje(
                    "No se puede eliminar el periodo.\n" +
                            "Puede tener cursos asociados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void cargarPeriodos() {
        List<PeriodoAcademico> periodos = periodoDAO.listarTodos();
        vista.actualizarTabla(periodos);
    }


    public void cargarPeriodosActivos() {
        List<PeriodoAcademico> periodos = periodoDAO.listarActivos();
        vista.actualizarTabla(periodos);
    }


    public void buscarPeriodos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarPeriodos();
            return;
        }

        List<PeriodoAcademico> periodos = periodoDAO.buscarPorNombre(criterio);
        vista.actualizarTabla(periodos);

        if (periodos.isEmpty()) {
            vista.mostrarMensaje(
                    "No se encontraron periodos con ese criterio",
                    "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }


    public PeriodoAcademico obtenerPeriodo(int periodoId) {
        return periodoDAO.obtenerPorId(periodoId);
    }


    public PeriodoAcademico obtenerPeriodoActual() {
        return periodoDAO.obtenerPeriodoActual();
    }


    private String validarDatosPeriodo(String nombrePeriodo, Date fechaInicio, Date fechaFin) {
        // Validar nombre
        if (nombrePeriodo == null || nombrePeriodo.trim().isEmpty()) {
            return "El nombre del periodo es obligatorio";
        }

        if (nombrePeriodo.trim().length() < 3) {
            return "El nombre debe tener al menos 3 caracteres";
        }

        if (nombrePeriodo.trim().length() > 255) {
            return "El nombre no puede exceder 255 caracteres";
        }

        // Validar fechas
        if (fechaInicio == null) {
            return "La fecha de inicio es obligatoria";
        }

        if (fechaFin == null) {
            return "La fecha de fin es obligatoria";
        }

        // Validar que fecha inicio sea menor a fecha fin
        if (!fechaInicio.before(fechaFin)) {
            return "La fecha de inicio debe ser anterior a la fecha de fin";
        }

        // Validar duración mínima (al menos 1 mes)
        LocalDate inicio = fechaInicio.toLocalDate();
        LocalDate fin = fechaFin.toLocalDate();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(inicio, fin);

        if (dias < 30) {
            return "El periodo debe tener una duración mínima de 30 días";
        }

        if (dias > 365) {
            return "El periodo no puede durar más de 1 año";
        }

        return null;
    }

    public String obtenerEstadisticas() {
        List<PeriodoAcademico> todos = periodoDAO.listarTodos();
        List<PeriodoAcademico> activos = periodoDAO.listarActivos();
        PeriodoAcademico actual = periodoDAO.obtenerPeriodoActual();

        if (todos.isEmpty()) {
            return "No hay periodos académicos registrados en el sistema";
        }

        return String.format(
                "📊 ESTADÍSTICAS DE PERIODOS ACADÉMICOS\n\n" +
                        "Total de periodos: %d\n" +
                        "Periodos activos: %d\n" +
                        "Periodo actual: %s\n",
                todos.size(),
                activos.size(),
                actual != null ? actual.getNombrePeriodo() : "Ninguno"
        );
    }
}
