package dao;

import model.ConexionBD;
import model.Calificacion;
import model.CorteEvaluacion;
import model.ComponenteEvaluacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CalificacionDAO {
    private ConexionBD conexionDB;

    public CalificacionDAO() {
        this.conexionDB = ConexionBD.getInstancia();
    }

    // ========== CORTES DE EVALUACIÓN ==========

    /**
     * Crear estructura de evaluación para un curso (3 cortes automáticos)
     */
    public boolean crearEstructuraEvaluacion(int cursoId, int periodoId) {
        try {
            // Corte 1: 30%
            crearCorte(new CorteEvaluacion(cursoId, periodoId, "Primer Corte", 30.0,
                    "Primer corte evaluativo del periodo"));

            // Corte 2: 30%
            crearCorte(new CorteEvaluacion(cursoId, periodoId, "Segundo Corte", 30.0,
                    "Segundo corte evaluativo del periodo"));

            // Corte 3: 40%
            crearCorte(new CorteEvaluacion(cursoId, periodoId, "Tercer Corte", 40.0,
                    "Tercer corte evaluativo del periodo"));

            return true;
        } catch (Exception e) {
            System.err.println("Error al crear estructura de evaluación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crear un corte de evaluación
     */
    public boolean crearCorte(CorteEvaluacion corte) {
        String sql = "INSERT INTO cortes_evaluacion (curso_id, periodo_academico_id, " +
                "nombre_corte, porcentaje, comentarios_corte) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, corte.getCursoId());
            stmt.setInt(2, corte.getPeriodoAcademicoId());
            stmt.setString(3, corte.getNombreCorte());
            stmt.setDouble(4, corte.getPorcentaje());
            stmt.setString(5, corte.getComentariosCorte());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear corte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Listar cortes de un curso
     */
    public List<CorteEvaluacion> listarCortesPorCurso(int cursoId) {
        List<CorteEvaluacion> cortes = new ArrayList<>();
        String sql = "SELECT ce.*, c.nombre_curso, p.nombre_periodo " +
                "FROM cortes_evaluacion ce " +
                "LEFT JOIN cursos c ON ce.curso_id = c.curso_id " +
                "LEFT JOIN periodos_academicos p ON ce.periodo_academico_id = p.periodo_academico_id " +
                "WHERE ce.curso_id = ? ORDER BY ce.corte_evaluacion_id";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CorteEvaluacion corte = new CorteEvaluacion();
                corte.setCorteEvaluacionId(rs.getInt("corte_evaluacion_id"));
                corte.setCursoId(rs.getInt("curso_id"));
                corte.setPeriodoAcademicoId(rs.getInt("periodo_academico_id"));
                corte.setNombreCorte(rs.getString("nombre_corte"));
                corte.setPorcentaje(rs.getDouble("porcentaje"));
                corte.setComentariosCorte(rs.getString("comentarios_corte"));
                corte.setNombreCurso(rs.getString("nombre_curso"));
                corte.setNombrePeriodo(rs.getString("nombre_periodo"));
                cortes.add(corte);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar cortes: " + e.getMessage());
            e.printStackTrace();
        }
        return cortes;
    }

    // ========== COMPONENTES DE EVALUACIÓN ==========

    /**
     * Crear un componente de evaluación
     */
    public boolean crearComponente(ComponenteEvaluacion componente) {
        String sql = "INSERT INTO componentes_evaluacion (corte_evaluacion_id, " +
                "nombre_componente, porcentaje) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, componente.getCorteEvaluacionId());
            stmt.setString(2, componente.getNombreComponente());
            stmt.setDouble(3, componente.getPorcentaje());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear componente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Listar componentes de un corte
     */
    public List<ComponenteEvaluacion> listarComponentesPorCorte(int corteId) {
        List<ComponenteEvaluacion> componentes = new ArrayList<>();
        String sql = "SELECT comp.*, corte.nombre_corte, corte.porcentaje as porcentaje_corte " +
                "FROM componentes_evaluacion comp " +
                "LEFT JOIN cortes_evaluacion corte ON comp.corte_evaluacion_id = corte.corte_evaluacion_id " +
                "WHERE comp.corte_evaluacion_id = ? " +
                "ORDER BY comp.componente_evaluacion_id";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, corteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ComponenteEvaluacion comp = new ComponenteEvaluacion();
                comp.setComponenteEvaluacionId(rs.getInt("componente_evaluacion_id"));
                comp.setCorteEvaluacionId(rs.getInt("corte_evaluacion_id"));
                comp.setNombreComponente(rs.getString("nombre_componente"));
                comp.setPorcentaje(rs.getDouble("porcentaje"));
                comp.setNombreCorte(rs.getString("nombre_corte"));
                comp.setPorcentajeCorte(rs.getDouble("porcentaje_corte"));
                componentes.add(comp);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar componentes: " + e.getMessage());
            e.printStackTrace();
        }
        return componentes;
    }

    /**
     * Obtener todos los componentes de un curso
     */
    public List<ComponenteEvaluacion> listarComponentesPorCurso(int cursoId) {
        List<ComponenteEvaluacion> componentes = new ArrayList<>();
        String sql = "SELECT * FROM vista_estructura_evaluacion " +
                "WHERE curso_id = ? ORDER BY corte_evaluacion_id, componente_evaluacion_id";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ComponenteEvaluacion comp = new ComponenteEvaluacion();
                comp.setComponenteEvaluacionId(rs.getInt("componente_evaluacion_id"));
                comp.setCorteEvaluacionId(rs.getInt("corte_evaluacion_id"));
                comp.setNombreComponente(rs.getString("nombre_componente"));
                comp.setPorcentaje(rs.getDouble("porcentaje_componente"));
                comp.setNombreCorte(rs.getString("nombre_corte"));
                comp.setPorcentajeCorte(rs.getDouble("porcentaje_corte"));
                componentes.add(comp);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar componentes por curso: " + e.getMessage());
            e.printStackTrace();
        }
        return componentes;
    }

    // ========== CALIFICACIONES ==========

    /**
     * Registrar calificación usando SP
     */
    public boolean registrarCalificacion(Calificacion calificacion) {
        String sql = "CALL sp_registrar_calificacion(?, ?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, calificacion.getEstudianteId());
            stmt.setInt(2, calificacion.getComponenteEvaluacionId());
            stmt.setDouble(3, calificacion.getNota());
            stmt.setString(4, calificacion.getComentariosCalificacion());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al registrar calificación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar calificación usando SP
     */
    public boolean actualizarCalificacion(Calificacion calificacion) {
        String sql = "CALL sp_actualizar_calificacion(?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, calificacion.getCalificacionId());
            stmt.setDouble(2, calificacion.getNota());
            stmt.setString(3, calificacion.getComentariosCalificacion());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al actualizar calificación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar calificación usando SP
     */
    public boolean eliminarCalificacion(int calificacionId) {
        String sql = "CALL sp_eliminar_calificacion(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, calificacionId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar calificación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener calificaciones de un estudiante en un curso usando SP
     */
    public List<Calificacion> obtenerCalificacionesEstudiante(int estudianteId, int cursoId) {
        List<Calificacion> calificaciones = new ArrayList<>();
        String sql = "CALL sp_obtener_calificaciones_estudiante(?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, estudianteId);
            stmt.setInt(2, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                calificaciones.add(mapearCalificacion(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener calificaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return calificaciones;
    }

    /**
     * Listar calificaciones detalladas usando vista
     */
    public List<Calificacion> listarCalificacionesDetalladas(int cursoId) {
        List<Calificacion> calificaciones = new ArrayList<>();
        String sql = "SELECT * FROM vista_calificaciones_detalladas " +
                "WHERE curso_id = ? " +
                "ORDER BY estudiante_nombre, corte_evaluacion_id, componente_evaluacion_id";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                calificaciones.add(mapearCalificacionVista(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar calificaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return calificaciones;
    }

    /**
     * Calcular nota final usando SP
     */
    public NotaFinal calcularNotaFinal(int estudianteId, int cursoId) {
        String sql = "CALL sp_calcular_nota_final(?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, estudianteId);
            stmt.setInt(2, cursoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                NotaFinal nf = new NotaFinal();
                nf.nombreEstudiante = rs.getString("estudiante");
                nf.nombreCurso = rs.getString("nombre_curso");
                nf.notaFinal = rs.getDouble("nota_final");
                return nf;
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular nota final: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtener notas finales de todos los estudiantes de un curso
     */
    public List<NotaFinal> obtenerNotasFinalesCurso(int cursoId) {
        List<NotaFinal> notas = new ArrayList<>();
        String sql = "SELECT * FROM vista_notas_finales WHERE curso_id = ? " +
                "ORDER BY nota_final DESC";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NotaFinal nf = new NotaFinal();
                nf.estudianteId = rs.getInt("estudiante_id");
                nf.identificacion = rs.getString("identificacion");
                nf.nombreEstudiante = rs.getString("estudiante");
                nf.nombreCurso = rs.getString("nombre_curso");
                nf.notaFinal = rs.getDouble("nota_final");
                nf.estado = rs.getString("estado");
                nf.conceptoFinal = rs.getString("concepto_final");
                notas.add(nf);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener notas finales: " + e.getMessage());
            e.printStackTrace();
        }
        return notas;
    }

    /**
     * Obtener ranking de estudiantes
     */
    public List<NotaFinal> obtenerRankingCurso(int cursoId) {
        List<NotaFinal> ranking = new ArrayList<>();
        String sql = "SELECT * FROM vista_ranking_estudiantes WHERE curso_id = ? " +
                "ORDER BY ranking";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NotaFinal nf = new NotaFinal();
                nf.ranking = rs.getInt("ranking");
                nf.estudianteId = rs.getInt("estudiante_id");
                nf.nombreEstudiante = rs.getString("estudiante");
                nf.nombreCurso = rs.getString("nombre_curso");
                nf.notaFinal = rs.getDouble("nota_final");
                nf.estado = rs.getString("estado");
                ranking.add(nf);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ranking: " + e.getMessage());
            e.printStackTrace();
        }
        return ranking;
    }

    /**
     * Verificar si ya existe calificación
     */
    public boolean existeCalificacion(int estudianteId, int componenteId) {
        String sql = "SELECT COUNT(*) as total FROM calificaciones " +
                "WHERE estudiante_id = ? AND componente_evaluacion_id = ?";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, estudianteId);
            stmt.setInt(2, componenteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar calificación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ========== MÉTODOS DE MAPEO ==========

    private Calificacion mapearCalificacion(ResultSet rs) throws SQLException {
        Calificacion cal = new Calificacion();
        cal.setCalificacionId(rs.getInt("calificacion_id"));
        cal.setNombreEstudiante(rs.getString("estudiante"));
        cal.setNombreCorte(rs.getString("nombre_corte"));
        cal.setNombreComponente(rs.getString("nombre_componente"));
        cal.setPorcentajeComponente(rs.getDouble("porcentaje_componente"));
        cal.setNota(rs.getDouble("nota"));
        cal.setComentariosCalificacion(rs.getString("comentarios_calificacion"));
        return cal;
    }

    private Calificacion mapearCalificacionVista(ResultSet rs) throws SQLException {
        Calificacion cal = new Calificacion();
        cal.setCalificacionId(rs.getInt("calificacion_id"));
        cal.setEstudianteId(rs.getInt("estudiante_id"));
        cal.setIdentificacionEstudiante(rs.getString("estudiante_identificacion"));
        cal.setNombreEstudiante(rs.getString("estudiante_nombre"));
        cal.setNombreCurso(rs.getString("nombre_curso"));
        cal.setNombreCorte(rs.getString("nombre_corte"));
        cal.setNombreComponente(rs.getString("nombre_componente"));
        cal.setPorcentajeComponente(rs.getDouble("porcentaje_componente"));
        cal.setPorcentajeCorte(rs.getDouble("porcentaje_corte"));
        cal.setNota(rs.getDouble("nota"));
        cal.setComentariosCalificacion(rs.getString("comentarios_calificacion"));
        return cal;
    }

    // ========== CLASE INTERNA PARA NOTAS FINALES ==========

    public static class NotaFinal {
        public int ranking;
        public int estudianteId;
        public String identificacion;
        public String nombreEstudiante;
        public String nombreCurso;
        public double notaFinal;
        public String estado;
        public String conceptoFinal;
    }
}
