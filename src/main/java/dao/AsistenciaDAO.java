package dao;

import model.ConexionBD;
import model.Asistencia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {
    private ConexionBD conexionDB;

    public AsistenciaDAO() {
        this.conexionDB = ConexionBD.getInstancia();
    }

    /**
     * Registrar nueva asistencia usando SP
     * CALL sp_registrar_asistencia(...)
     */
    public boolean crear(Asistencia asistencia) {
        String sql = "CALL sp_registrar_asistencia(?, ?, ?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, asistencia.getEstudianteId());
            stmt.setInt(2, asistencia.getCursoId());
            stmt.setDate(3, asistencia.getFechaClase());
            stmt.setString(4, asistencia.getEstadoAsistencia());
            stmt.setString(5, asistencia.getNovedades());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al registrar asistencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener asistencias de un estudiante en un curso usando SP
     * CALL sp_obtener_asistencias_estudiante(?, ?)
     */
    public List<Asistencia> obtenerPorEstudianteCurso(int estudianteId, int cursoId) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "CALL sp_obtener_asistencias_estudiante(?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, estudianteId);
            stmt.setInt(2, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                asistencias.add(mapearAsistencia(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asistencias: " + e.getMessage());
            e.printStackTrace();
        }
        return asistencias;
    }

    /**
     * Actualizar asistencia usando SP
     * CALL sp_actualizar_asistencia(...)
     */
    public boolean actualizar(Asistencia asistencia) {
        String sql = "CALL sp_actualizar_asistencia(?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, asistencia.getAsistenciaId());
            stmt.setString(2, asistencia.getEstadoAsistencia());
            stmt.setString(3, asistencia.getNovedades());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al actualizar asistencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar asistencia usando SP
     * CALL sp_eliminar_asistencia(?)
     */
    public boolean eliminar(int asistenciaId) {
        String sql = "CALL sp_eliminar_asistencia(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, asistenciaId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar asistencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Listar asistencias usando la vista detallada
     */
    public List<Asistencia> listarTodas() {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM vista_asistencias_detalladas ORDER BY fecha_clase DESC, estudiante_nombre";

        try (Statement stmt = conexionDB.getConexion().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                asistencias.add(mapearAsistenciaVista(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar asistencias: " + e.getMessage());
            e.printStackTrace();
        }
        return asistencias;
    }

    /**
     * Listar asistencias por curso
     */
    public List<Asistencia> listarPorCurso(int cursoId) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM vista_asistencias_detalladas WHERE curso_id = ? " +
                "ORDER BY fecha_clase DESC, estudiante_nombre";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                asistencias.add(mapearAsistenciaVista(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar asistencias por curso: " + e.getMessage());
            e.printStackTrace();
        }
        return asistencias;
    }

    /**
     * Listar asistencias por fecha
     */
    public List<Asistencia> listarPorFecha(int cursoId, Date fecha) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM vista_asistencias_detalladas " +
                "WHERE curso_id = ? AND fecha_clase = ? " +
                "ORDER BY estudiante_nombre";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            stmt.setDate(2, fecha);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                asistencias.add(mapearAsistenciaVista(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar asistencias por fecha: " + e.getMessage());
            e.printStackTrace();
        }
        return asistencias;
    }

    /**
     * Calcular porcentaje de asistencia usando SP
     * CALL sp_calcular_asistencia(?, ?)
     */
    public AsistenciaEstadistica calcularAsistencia(int estudianteId, int cursoId) {
        String sql = "CALL sp_calcular_asistencia(?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, estudianteId);
            stmt.setInt(2, cursoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                AsistenciaEstadistica stats = new AsistenciaEstadistica();
                stats.nombreEstudiante = rs.getString("estudiante");
                stats.nombreCurso = rs.getString("nombre_curso");
                stats.totalClases = rs.getInt("total_clases");
                stats.clasesPresentes = rs.getInt("clases_presentes");
                stats.tardanzas = rs.getInt("tardanzas");
                stats.ausencias = rs.getInt("ausencias");
                stats.porcentajeAsistencia = rs.getDouble("porcentaje_asistencia");
                return stats;
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular asistencia: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtener porcentajes de asistencia por curso usando vista
     */
    public List<AsistenciaEstadistica> obtenerPorcentajesPorCurso(int cursoId) {
        List<AsistenciaEstadistica> estadisticas = new ArrayList<>();
        String sql = "SELECT * FROM vista_porcentaje_asistencia WHERE curso_id = ? " +
                "ORDER BY porcentaje_asistencia DESC";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AsistenciaEstadistica stats = new AsistenciaEstadistica();
                stats.nombreEstudiante = rs.getString("estudiante");
                stats.nombreCurso = rs.getString("nombre_curso");
                stats.totalClases = rs.getInt("total_clases");
                stats.clasesPresentes = rs.getInt("clases_presentes");
                stats.tardanzas = rs.getInt("tardanzas");
                stats.ausencias = rs.getInt("ausencias");
                stats.porcentajeAsistencia = rs.getDouble("porcentaje_asistencia");
                stats.cumpleMinimo = rs.getString("cumple_asistencia_minima");
                estadisticas.add(stats);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener porcentajes: " + e.getMessage());
            e.printStackTrace();
        }
        return estadisticas;
    }

    /**
     * Verificar si ya existe asistencia
     */
    public boolean existeAsistencia(int estudianteId, int cursoId, Date fecha) {
        String sql = "SELECT COUNT(*) as total FROM asistencias " +
                "WHERE estudiante_id = ? AND curso_id = ? AND fecha_clase = ?";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, estudianteId);
            stmt.setInt(2, cursoId);
            stmt.setDate(3, fecha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar asistencia: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mapear ResultSet a objeto Asistencia
     */
    private Asistencia mapearAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setAsistenciaId(rs.getInt("asistencia_id"));
        asistencia.setEstudianteId(rs.getInt("estudiante_id"));
        asistencia.setCursoId(rs.getInt("curso_id"));
        asistencia.setFechaClase(rs.getDate("fecha_clase"));
        asistencia.setEstadoAsistencia(rs.getString("estado_asistencia"));
        asistencia.setNovedades(rs.getString("novedades"));

        // Información adicional si está disponible
        try {
            asistencia.setNombreEstudiante(rs.getString("estudiante"));
            asistencia.setNombreCurso(rs.getString("nombre_curso"));
        } catch (SQLException e) {
            // Campos opcionales
        }

        return asistencia;
    }

    /**
     * Mapear ResultSet de la vista a objeto Asistencia
     */
    private Asistencia mapearAsistenciaVista(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setAsistenciaId(rs.getInt("asistencia_id"));
        asistencia.setEstudianteId(rs.getInt("estudiante_id"));
        asistencia.setIdentificacionEstudiante(rs.getString("estudiante_identificacion"));
        asistencia.setNombreEstudiante(rs.getString("estudiante_nombre"));
        asistencia.setCursoId(rs.getInt("curso_id"));
        asistencia.setNombreCurso(rs.getString("nombre_curso"));
        asistencia.setFechaClase(rs.getDate("fecha_clase"));
        asistencia.setEstadoAsistencia(rs.getString("estado_asistencia"));
        asistencia.setNovedades(rs.getString("novedades"));

        try {
            asistencia.setNumeroClase(rs.getInt("numero_clase"));
        } catch (SQLException e) {
            // Campo opcional
        }

        return asistencia;
    }

    /**
     * Clase interna para estadísticas de asistencia
     */
    public static class AsistenciaEstadistica {
        public String nombreEstudiante;
        public String nombreCurso;
        public int totalClases;
        public int clasesPresentes;
        public int tardanzas;
        public int ausencias;
        public double porcentajeAsistencia;
        public String cumpleMinimo;
    }
}
