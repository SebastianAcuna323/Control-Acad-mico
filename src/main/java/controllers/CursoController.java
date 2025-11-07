package controllers;

import dao.CursoDAO;
import dao.DocenteDAO;
import dao.PeriodoAcademicoDAO;
import model.Curso;
import model.Docente;
import model.PeriodoAcademico;
import views.GestionCursos;
import javax.swing.*;
import java.util.List;

public class CursoController {
    private CursoDAO cursoDAO;
    private DocenteDAO docenteDAO;
    private PeriodoAcademicoDAO periodoDAO;
    private GestionCursos vista;

    public CursoController(GestionCursos vista) {
        this.vista = vista;
        this.cursoDAO = new CursoDAO();
        this.docenteDAO = new DocenteDAO();
        this.periodoDAO = new PeriodoAcademicoDAO();
    }


    public void crearCurso(String nombreCurso, int periodoId, int docenteId,
                           String descripcion) {

        // Validar datos
        String mensajeValidacion = validarDatosCurso(nombreCurso, periodoId, docenteId);

        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear objeto curso
        Curso curso = new Curso(
                nombreCurso, periodoId, docenteId, descripcion
        );

        // Intentar guardar
        if (cursoDAO.crear(curso)) {
            vista.mostrarMensaje(
                    "Curso creado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarCursos();
        } else {
            vista.mostrarMensaje(
                    "Error al crear el curso",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void actualizarCurso(int cursoId, String nombreCurso, int periodoId,
                                int docenteId, String descripcion) {

        if (cursoId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un curso de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar datos
        String mensajeValidacion = validarDatosCurso(nombreCurso, periodoId, docenteId);

        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear objeto curso
        Curso curso = new Curso(
                cursoId, nombreCurso, periodoId, docenteId, descripcion
        );

        // Intentar actualizar
        if (cursoDAO.actualizar(curso)) {
            vista.mostrarMensaje(
                    "Curso actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarCursos();
        } else {
            vista.mostrarMensaje(
                    "Error al actualizar el curso",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void eliminarCurso(int cursoId) {
        if (cursoId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un curso de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Confirmar eliminación
        int confirmacion = vista.confirmarAccion(
                "¿Está seguro de eliminar este curso?\n" +
                        "Esta acción eliminará también las clases, evaluaciones y calificaciones asociadas.",
                "Confirmar Eliminación"
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Intentar eliminar
        if (cursoDAO.eliminar(cursoId)) {
            vista.mostrarMensaje(
                    "Curso eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarCursos();
        } else {
            vista.mostrarMensaje(
                    "No se puede eliminar el curso.\n" +
                            "Puede tener clases o evaluaciones asociadas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void cargarCursos() {
        List<Curso> cursos = cursoDAO.listarVista();
        vista.actualizarTabla(cursos);
    }


    public void cargarCursosPorPeriodo(int periodoId) {
        List<Curso> cursos = cursoDAO.listarPorPeriodo(periodoId);
        vista.actualizarTabla(cursos);
    }


    public void cargarCursosPorDocente(int docenteId) {
        List<Curso> cursos = cursoDAO.listarPorDocente(docenteId);
        vista.actualizarTabla(cursos);
    }


    public void buscarCursos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarCursos();
            return;
        }

        List<Curso> cursos = cursoDAO.buscarPorNombre(criterio);
        vista.actualizarTabla(cursos);

        if (cursos.isEmpty()) {
            vista.mostrarMensaje(
                    "No se encontraron cursos con ese criterio",
                    "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }


    public Curso obtenerCurso(int cursoId) {
        return cursoDAO.obtenerPorId(cursoId);
    }


    public List<Docente> cargarDocentes() {
        return docenteDAO.listarTodos();
    }


    public List<PeriodoAcademico> cargarPeriodos() {
        return periodoDAO.listarTodos();
    }


    public List<PeriodoAcademico> cargarPeriodosActivos() {
        return periodoDAO.listarActivos();
    }


    private String validarDatosCurso(String nombreCurso, int periodoId, int docenteId) {

        // Validar nombre del curso
        if (nombreCurso == null || nombreCurso.trim().isEmpty()) {
            return "El nombre del curso es obligatorio";
        }

        if (nombreCurso.trim().length() < 3) {
            return "El nombre del curso debe tener al menos 3 caracteres";
        }

        if (nombreCurso.trim().length() > 255) {
            return "El nombre del curso no puede exceder 255 caracteres";
        }

        // Validar periodo académico
        if (periodoId <= 0) {
            return "Debe seleccionar un periodo académico válido";
        }

        // Validar que el periodo exista
        PeriodoAcademico periodo = periodoDAO.obtenerPorId(periodoId);
        if (periodo == null) {
            return "El periodo académico seleccionado no existe";
        }

        // Validar docente
        if (docenteId <= 0) {
            return "Debe seleccionar un docente válido";
        }

        // Validar que el docente exista
        Docente docente = docenteDAO.obtenerPorId(docenteId);
        if (docente == null) {
            return "El docente seleccionado no existe";
        }

        return null; // Validación exitosa
    }


    public String obtenerEstadisticas() {
        List<Curso> todos = cursoDAO.listarTodos();
        List<PeriodoAcademico> periodos = periodoDAO.listarTodos();
        List<Docente> docentes = docenteDAO.listarTodos();

        if (todos.isEmpty()) {
            return "No hay cursos registrados en el sistema";
        }

        // Contar cursos por periodo actual
        PeriodoAcademico periodoActual = periodoDAO.obtenerPeriodoActual();
        long cursosActuales = 0;
        if (periodoActual != null) {
            cursosActuales = todos.stream()
                    .filter(c -> c.getPeriodoAcademicoId() == periodoActual.getPeriodoAcademicoId())
                    .count();
        }

        // Docente con más cursos
        String docenteMasCursos = "N/A";
        long maxCursos = 0;
        for (Docente d : docentes) {
            long count = todos.stream()
                    .filter(c -> c.getDocenteId() == d.getDocenteId())
                    .count();
            if (count > maxCursos) {
                maxCursos = count;
                docenteMasCursos = d.getNombreDocente() + " (" + count + " cursos)";
            }
        }

        return String.format(
                "📊 ESTADÍSTICAS DE CURSOS\n\n" +
                        "Total de cursos: %d\n" +
                        "Periodos académicos: %d\n" +
                        "Cursos en periodo actual: %d\n" +
                        "Total de docentes: %d\n\n" +
                        "Docente con más cursos:\n%s",
                todos.size(),
                periodos.size(),
                cursosActuales,
                docentes.size(),
                docenteMasCursos
        );
    }
}


