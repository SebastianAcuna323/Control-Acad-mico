package controllers;


import dao.AsistenciaDAO;
import dao.CursoDAO;
import dao.EstudianteDAO;
import model.Asistencia;
import model.Curso;
import model.Estudiante;
import views.GestionAsistencias;
import javax.swing.*;
import java.sql.Date;
import java.util.List;

public class AsistenciaController {
    private AsistenciaDAO asistenciaDAO;
    private CursoDAO cursoDAO;
    private EstudianteDAO estudianteDAO;
    private GestionAsistencias vista;

    public AsistenciaController(GestionAsistencias vista) {
        this.vista = vista;
        this.asistenciaDAO = new AsistenciaDAO();
        this.cursoDAO = new CursoDAO();
        this.estudianteDAO = new EstudianteDAO();
    }


    public void registrarAsistencia(int estudianteId, int cursoId, Date fechaClase,
                                    String estadoAsistencia, String novedades) {

        // Validar datos
        String mensajeValidacion = validarDatosAsistencia(
                estudianteId, cursoId, fechaClase, estadoAsistencia
        );

        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar si ya existe registro
        if (asistenciaDAO.existeAsistencia(estudianteId, cursoId, fechaClase)) {
            vista.mostrarMensaje(
                    "Ya existe un registro de asistencia para este estudiante en esta fecha.\n" +
                            "Use la opción 'Actualizar' para modificarlo.",
                    "Registro Duplicado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Crear objeto asistencia
        Asistencia asistencia = new Asistencia(
                estudianteId, cursoId, fechaClase, estadoAsistencia, novedades
        );

        // Intentar guardar
        if (asistenciaDAO.crear(asistencia)) {
            vista.mostrarMensaje(
                    "Asistencia registrada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarAsistencias();
        } else {
            vista.mostrarMensaje(
                    "Error al registrar la asistencia",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void actualizarAsistencia(int asistenciaId, String estadoAsistencia,
                                     String novedades) {

        if (asistenciaId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar una asistencia de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar estado
        if (estadoAsistencia == null || estadoAsistencia.trim().isEmpty()) {
            vista.mostrarMensaje(
                    "Debe seleccionar un estado de asistencia",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Crear objeto asistencia
        Asistencia asistencia = new Asistencia();
        asistencia.setAsistenciaId(asistenciaId);
        asistencia.setEstadoAsistencia(estadoAsistencia);
        asistencia.setNovedades(novedades);

        // Intentar actualizar
        if (asistenciaDAO.actualizar(asistencia)) {
            vista.mostrarMensaje(
                    "Asistencia actualizada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarAsistencias();
        } else {
            vista.mostrarMensaje(
                    "Error al actualizar la asistencia",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void eliminarAsistencia(int asistenciaId) {
        if (asistenciaId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar una asistencia de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Confirmar eliminación
        int confirmacion = vista.confirmarAccion(
                "¿Está seguro de eliminar este registro de asistencia?",
                "Confirmar Eliminación"
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Intentar eliminar
        if (asistenciaDAO.eliminar(asistenciaId)) {
            vista.mostrarMensaje(
                    "Asistencia eliminada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarAsistencias();
        } else {
            vista.mostrarMensaje(
                    "Error al eliminar la asistencia",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void cargarAsistencias() {
        List<Asistencia> asistencias = asistenciaDAO.listarTodas();
        vista.actualizarTabla(asistencias);
    }


    public void cargarAsistenciasPorCurso(int cursoId) {
        if (cursoId <= 0) {
            cargarAsistencias();
            return;
        }
        List<Asistencia> asistencias = asistenciaDAO.listarPorCurso(cursoId);
        vista.actualizarTabla(asistencias);
    }


    public void cargarAsistenciasPorFecha(int cursoId, Date fecha) {
        if (cursoId <= 0 || fecha == null) {
            vista.mostrarMensaje(
                    "Debe seleccionar un curso y una fecha",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        List<Asistencia> asistencias = asistenciaDAO.listarPorFecha(cursoId, fecha);
        vista.actualizarTabla(asistencias);
    }


    public List<Curso> cargarCursos() {
        return cursoDAO.listarVista();
    }

    public List<Estudiante> cargarEstudiantes() {
        return estudianteDAO.listarTodos();
    }

    public void mostrarEstadisticasEstudiante(int estudianteId, int cursoId) {
        if (estudianteId <= 0 || cursoId <= 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un estudiante y un curso",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        AsistenciaDAO.AsistenciaEstadistica stats =
                asistenciaDAO.calcularAsistencia(estudianteId, cursoId);

        if (stats != null) {
            String mensaje = String.format(
                    "📊 ESTADÍSTICAS DE ASISTENCIA\n\n" +
                            "Estudiante: %s\n" +
                            "Curso: %s\n\n" +
                            "Total de clases: %d\n" +
                            "Presentes: %d\n" +
                            "Tardanzas: %d\n" +
                            "Ausencias: %d\n\n" +
                            "Porcentaje de asistencia: %.2f%%\n\n" +
                            "%s",
                    stats.nombreEstudiante,
                    stats.nombreCurso,
                    stats.totalClases,
                    stats.clasesPresentes,
                    stats.tardanzas,
                    stats.ausencias,
                    stats.porcentajeAsistencia,
                    stats.porcentajeAsistencia >= 80 ?
                            "✓ Cumple con el mínimo de asistencia (80%)" :
                            "✗ NO cumple con el mínimo de asistencia (80%)"
            );

            vista.mostrarMensaje(mensaje, "Estadísticas de Asistencia",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            vista.mostrarMensaje(
                    "No hay registros de asistencia para este estudiante en este curso",
                    "Sin Datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public void mostrarReporteAsistenciasCurso(int cursoId) {
        if (cursoId <= 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un curso",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        List<AsistenciaDAO.AsistenciaEstadistica> estadisticas =
                asistenciaDAO.obtenerPorcentajesPorCurso(cursoId);

        if (estadisticas.isEmpty()) {
            vista.mostrarMensaje(
                    "No hay registros de asistencia para este curso",
                    "Sin Datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        vista.mostrarReporteAsistencias(estadisticas);
    }

    private String validarDatosAsistencia(int estudianteId, int cursoId,
                                          Date fechaClase, String estadoAsistencia) {

        // Validar estudiante
        if (estudianteId <= 0) {
            return "Debe seleccionar un estudiante válido";
        }

        Estudiante estudiante = estudianteDAO.obtenerPorId(estudianteId);
        if (estudiante == null) {
            return "El estudiante seleccionado no existe";
        }

        // Validar curso
        if (cursoId <= 0) {
            return "Debe seleccionar un curso válido";
        }

        Curso curso = cursoDAO.obtenerPorId(cursoId);
        if (curso == null) {
            return "El curso seleccionado no existe";
        }

        // Validar fecha
        if (fechaClase == null) {
            return "La fecha de clase es obligatoria";
        }

        // Validar que la fecha no sea futura
        Date hoy = new Date(System.currentTimeMillis());
        if (fechaClase.after(hoy)) {
            return "No se puede registrar asistencia para una fecha futura";
        }

        // Validar estado de asistencia
        if (estadoAsistencia == null || estadoAsistencia.trim().isEmpty()) {
            return "Debe seleccionar un estado de asistencia";
        }

        if (!estadoAsistencia.equals("presente") &&
                !estadoAsistencia.equals("ausente") &&
                !estadoAsistencia.equals("tardanza")) {
            return "Estado de asistencia inválido";
        }

        return null; // Validación exitosa
    }


    public String obtenerEstadisticas() {
        List<Asistencia> todas = asistenciaDAO.listarTodas();

        if (todas.isEmpty()) {
            return "No hay registros de asistencia en el sistema";
        }

        long presentes = todas.stream()
                .filter(a -> "presente".equalsIgnoreCase(a.getEstadoAsistencia()))
                .count();

        long ausentes = todas.stream()
                .filter(a -> "ausente".equalsIgnoreCase(a.getEstadoAsistencia()))
                .count();

        long tardanzas = todas.stream()
                .filter(a -> "tardanza".equalsIgnoreCase(a.getEstadoAsistencia()))
                .count();

        double porcentajePresentes = (presentes * 100.0) / todas.size();

        return String.format(
                "📊 ESTADÍSTICAS GENERALES DE ASISTENCIA\n\n" +
                        "Total de registros: %d\n\n" +
                        "Presentes: %d (%.1f%%)\n" +
                        "Ausentes: %d (%.1f%%)\n" +
                        "Tardanzas: %d (%.1f%%)",
                todas.size(),
                presentes, porcentajePresentes,
                ausentes, (ausentes * 100.0) / todas.size(),
                tardanzas, (tardanzas * 100.0) / todas.size()
        );
    }
}
