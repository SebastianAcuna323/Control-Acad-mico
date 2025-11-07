package controllers;

import dao.EstudianteDAO;
import model.Estudiante;
import views.GestionEstudiantes;
import javax.swing.*;
import java.util.List;
import java.util.regex.Pattern;

public class EstudianteController {

    private EstudianteDAO estudianteDao;
    private GestionEstudiantes vista;

    // Patrones de validación
    private static final Pattern PATTERN_EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PATTERN_TELEFONO =
            Pattern.compile("^[0-9]{7,10}$");


    public EstudianteController(GestionEstudiantes vista) {
        this.vista = vista;
        this.estudianteDao = new EstudianteDAO();
    }


    public void crearEstudiante(String identificacion, String nombre,
                                String correoInstitucional, String correoPersonal,
                                String telefono, boolean esVocero,
                                String tipoDocumento, String genero,
                                String comentarios) {

        // Validar datos
        String mensajeValidacion = validarDatosEstudiante(
                identificacion, nombre, correoInstitucional, correoPersonal, telefono
        );

        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar si ya existe
        if (estudianteDao.buscarPorIdentificacion(identificacion) != null) {
            vista.mostrarMensaje(
                    "Ya existe un estudiante con esta identificación",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Crear estudiante
        Estudiante estudiante = new Estudiante(
                identificacion, nombre, correoInstitucional, correoPersonal,
                telefono, esVocero, tipoDocumento, genero, comentarios
        );

        // Intentar guardar
        if (estudianteDao.crear(estudiante)) {
            vista.mostrarMensaje(
                    "Estudiante creado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarEstudiantes();
        } else {
            vista.mostrarMensaje(
                    "Error al crear el estudiante. Verifique los datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void actualizarEstudiante(int estudianteId, String identificacion,
                                     String nombre, String correoInstitucional,
                                     String correoPersonal, String telefono,
                                     boolean esVocero, String tipoDocumento,
                                     String genero, String comentarios) {

        if (estudianteId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un estudiante de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar datos
        String mensajeValidacion = validarDatosEstudiante(
                identificacion, nombre, correoInstitucional, correoPersonal, telefono
        );

        if (mensajeValidacion != null) {
            vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear objeto estudiante
        Estudiante estudiante = new Estudiante(
                estudianteId, identificacion, nombre, correoInstitucional,
                correoPersonal, telefono, esVocero, tipoDocumento,
                genero, comentarios
        );

        // Intentar actualizar
        if (estudianteDao.actualizar(estudiante)) {
            vista.mostrarMensaje(
                    "Estudiante actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarEstudiantes();
        } else {
            vista.mostrarMensaje(
                    "Error al actualizar el estudiante",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void eliminarEstudiante(int estudianteId) {
        if (estudianteId < 0) {
            vista.mostrarMensaje(
                    "Debe seleccionar un estudiante de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Confirmar eliminación
        int confirmacion = vista.confirmarAccion(
                "¿Está seguro de eliminar este estudiante?\n" +
                        "Esta acción puede fallar si el estudiante tiene registros asociados.",
                "Confirmar Eliminación"
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Intentar eliminar
        if (estudianteDao.eliminar(estudianteId)) {
            vista.mostrarMensaje(
                    "Estudiante eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            vista.limpiarFormulario();
            cargarEstudiantes();
        } else {
            vista.mostrarMensaje(
                    "No se puede eliminar el estudiante.\n" +
                            "Puede tener calificaciones o asistencias registradas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public void cargarEstudiantes() {
        List<Estudiante> estudiantes = estudianteDao.listarTodos();
        vista.actualizarTabla(estudiantes);
    }


    public void buscarEstudiantes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarEstudiantes();
            return;
        }

        List<Estudiante> estudiantes = estudianteDao.buscarPorNombre(criterio);
        vista.actualizarTabla(estudiantes);

        if (estudiantes.isEmpty()) {
            vista.mostrarMensaje(
                    "No se encontraron estudiantes con ese criterio",
                    "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }


    public Estudiante obtenerEstudiante(int estudianteId) {
        return estudianteDao.obtenerPorId(estudianteId);
    }


    public void cargarVoceros() {
        List<Estudiante> voceros = estudianteDao.listarVoceros();
        vista.actualizarTabla(voceros);
    }


    private String validarDatosEstudiante(String identificacion, String nombre,
                                          String correoInstitucional,
                                          String correoPersonal, String telefono) {

        // Validar identificación
        if (identificacion == null || identificacion.trim().isEmpty()) {
            return "La identificación es obligatoria";
        }

        if (identificacion.length() < 6 || identificacion.length() > 20) {
            return "La identificación debe tener entre 6 y 20 caracteres";
        }

        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if (nombre.length() < 3) {
            return "El nombre debe tener al menos 3 caracteres";
        }

        // Validar correo institucional
        if (correoInstitucional == null || correoInstitucional.trim().isEmpty()) {
            return "El correo institucional es obligatorio";
        }

        if (!PATTERN_EMAIL.matcher(correoInstitucional).matches()) {
            return "El correo institucional no tiene un formato válido";
        }

        if (!correoInstitucional.toLowerCase().contains("@uniajc.edu.co")) {
            return "El correo institucional debe ser del dominio @uniajc.edu.co";
        }

        // Validar correo personal (opcional)
        if (correoPersonal != null && !correoPersonal.trim().isEmpty()) {
            if (!PATTERN_EMAIL.matcher(correoPersonal).matches()) {
                return "El correo personal no tiene un formato válido";
            }
        }

        // Validar teléfono (opcional)
        if (telefono != null && !telefono.trim().isEmpty()) {
            if (!PATTERN_TELEFONO.matcher(telefono).matches()) {
                return "El teléfono debe tener entre 7 y 10 dígitos";
            }
        }

        return null; // Validación exitosa
    }


    public boolean validarEmail(String email) {
        return PATTERN_EMAIL.matcher(email).matches();
    }


    public boolean validarTelefono(String telefono) {
        return PATTERN_TELEFONO.matcher(telefono).matches();
    }


    public String obtenerEstadisticas() {
        List<Estudiante> todos = estudianteDao.listarTodos();
        List<Estudiante> voceros = estudianteDao.listarVoceros();

        long hombres = todos.stream()
                .filter(e -> "M".equalsIgnoreCase(e.getGenero()))
                .count();

        long mujeres = todos.stream()
                .filter(e -> "F".equalsIgnoreCase(e.getGenero()))
                .count();

        return String.format(
                "Total de estudiantes: %d\n" +
                        "Voceros: %d\n" +
                        "Hombres: %d\n" +
                        "Mujeres: %d",
                todos.size(), voceros.size(), hombres, mujeres
        );
    }
}
