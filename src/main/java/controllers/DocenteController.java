package controllers;


import dao.DocenteDAO;
import model.Docente;
import views.GestionDocentes;
import javax.swing.*;
import java.util.List;
import java.util.regex.Pattern;


    public class DocenteController {

        private DocenteDAO docenteDAO;
        private GestionDocentes vista;

        // Patrón para validar emails
        private static final Pattern PATTERN_EMAIL =
                Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");


        public DocenteController(GestionDocentes vista) {
            this.vista = vista;
            this.docenteDAO = new DocenteDAO();
        }


        public void crearDocente(String nombreDocente, String identificacion,
                                 String tipoIdentificacion, String genero,
                                 String correo, String tituloEstudios,
                                 String idiomas, String certificaciones) {

            // Validar datos
            String mensajeValidacion = validarDatosDocente(
                    nombreDocente, identificacion, correo
            );

            if (mensajeValidacion != null) {
                vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crear objeto docente
            Docente docente = new Docente(
                    nombreDocente, identificacion, tipoIdentificacion, genero,
                    correo, tituloEstudios, idiomas, certificaciones
            );

            // Intentar guardar en la base de datos
            if (docenteDAO.crear(docente)) {
                vista.mostrarMensaje(
                        "Docente creado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                vista.limpiarFormulario();
                cargarDocentes();
            } else {
                vista.mostrarMensaje(
                        "Error al crear el docente.\n" +
                                "Verifique que la identificación y correo no estén duplicados.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        public void actualizarDocente(int docenteId, String nombreDocente,
                                      String identificacion, String tipoIdentificacion,
                                      String genero, String correo, String tituloEstudios,
                                      String idiomas, String certificaciones) {

            // Validar que se haya seleccionado un docente
            if (docenteId < 0) {
                vista.mostrarMensaje(
                        "Debe seleccionar un docente de la tabla",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Validar datos
            String mensajeValidacion = validarDatosDocente(
                    nombreDocente, identificacion, correo
            );

            if (mensajeValidacion != null) {
                vista.mostrarMensaje(mensajeValidacion, "Error de Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crear objeto docente con ID
            Docente docente = new Docente(
                    docenteId, nombreDocente, identificacion, tipoIdentificacion,
                    genero, correo, tituloEstudios, idiomas, certificaciones
            );

            // Intentar actualizar en la base de datos
            if (docenteDAO.actualizar(docente)) {
                vista.mostrarMensaje(
                        "Docente actualizado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                vista.limpiarFormulario();
                cargarDocentes();
            } else {
                vista.mostrarMensaje(
                        "Error al actualizar el docente",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        public void eliminarDocente(int docenteId) {
            // Validar que se haya seleccionado un docente
            if (docenteId < 0) {
                vista.mostrarMensaje(
                        "Debe seleccionar un docente de la tabla",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Confirmar eliminación
            int confirmacion = vista.confirmarAccion(
                    "¿Está seguro de eliminar este docente?\n" +
                            "Esta acción puede fallar si el docente tiene cursos asignados.",
                    "Confirmar Eliminación"
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            // Intentar eliminar de la base de datos
            if (docenteDAO.eliminar(docenteId)) {
                vista.mostrarMensaje(
                        "Docente eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                vista.limpiarFormulario();
                cargarDocentes();
            } else {
                vista.mostrarMensaje(
                        "No se puede eliminar el docente.\n" +
                                "Puede tener cursos asignados.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }


        public void cargarDocentes() {
            List<Docente> docentes = docenteDAO.listarTodos();
            vista.actualizarTabla(docentes);
        }

        public void buscarDocentes(String criterio) {
            if (criterio == null || criterio.trim().isEmpty()) {
                cargarDocentes();
                return;
            }

            List<Docente> docentes = docenteDAO.buscarPorNombre(criterio);
            vista.actualizarTabla(docentes);

            if (docentes.isEmpty()) {
                vista.mostrarMensaje(
                        "No se encontraron docentes con ese criterio",
                        "Búsqueda",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }


        public Docente obtenerDocente(int docenteId) {
            return docenteDAO.obtenerPorId(docenteId);
        }


        public void cargarDocentesConCursos() {
            List<Docente> docentes = docenteDAO.listarConCursos();
            vista.actualizarTabla(docentes);
        }

        private String validarDatosDocente(String nombreDocente,
                                           String identificacion, String correo) {

            // Validar nombre
            if (nombreDocente == null || nombreDocente.trim().isEmpty()) {
                return "El nombre del docente es obligatorio";
            }

            if (nombreDocente.trim().length() < 3) {
                return "El nombre debe tener al menos 3 caracteres";
            }

            if (nombreDocente.trim().length() > 255) {
                return "El nombre no puede exceder 255 caracteres";
            }

            // Validar identificación
            if (identificacion == null || identificacion.trim().isEmpty()) {
                return "La identificación es obligatoria";
            }

            if (identificacion.trim().length() < 6 || identificacion.trim().length() > 20) {
                return "La identificación debe tener entre 6 y 20 caracteres";
            }

            // Validar que solo contenga números
            if (!identificacion.trim().matches("^[0-9]+$")) {
                return "La identificación debe contener solo números";
            }

            // Validar correo
            if (correo == null || correo.trim().isEmpty()) {
                return "El correo es obligatorio";
            }

            if (!PATTERN_EMAIL.matcher(correo.trim()).matches()) {
                return "El correo no tiene un formato válido";
            }

            if (correo.trim().length() > 255) {
                return "El correo no puede exceder 255 caracteres";
            }

            return null; // Validación exitosa
        }


        public boolean validarEmail(String email) {
            return PATTERN_EMAIL.matcher(email).matches();
        }


        public String obtenerEstadisticas() {
            List<Docente> todos = docenteDAO.listarTodos();

            if (todos.isEmpty()) {
                return "No hay docentes registrados en el sistema";
            }

            long hombres = todos.stream()
                    .filter(d -> "M".equalsIgnoreCase(d.getGenero()))
                    .count();

            long mujeres = todos.stream()
                    .filter(d -> "F".equalsIgnoreCase(d.getGenero()))
                    .count();

            long otros = todos.size() - hombres - mujeres;

            // Contar docentes con título de maestría o doctorado
            long conMaestria = todos.stream()
                    .filter(d -> d.getTituloEstudios() != null &&
                            (d.getTituloEstudios().toLowerCase().contains("maestr") ||
                                    d.getTituloEstudios().toLowerCase().contains("magíster")))
                    .count();

            long conDoctorado = todos.stream()
                    .filter(d -> d.getTituloEstudios() != null &&
                            d.getTituloEstudios().toLowerCase().contains("doctor"))
                    .count();

            return String.format(
                    "📊 ESTADÍSTICAS DE DOCENTES\n\n" +
                            "Total de docentes: %d\n\n" +
                            "Por género:\n" +
                            "  • Hombres: %d (%.1f%%)\n" +
                            "  • Mujeres: %d (%.1f%%)\n" +
                            "  • Otros: %d\n\n" +
                            "Por nivel académico:\n" +
                            "  • Con Maestría: %d\n" +
                            "  • Con Doctorado: %d",
                    todos.size(),
                    hombres, (hombres * 100.0 / todos.size()),
                    mujeres, (mujeres * 100.0 / todos.size()),
                    otros,
                    conMaestria,
                    conDoctorado
            );
        }
    }

