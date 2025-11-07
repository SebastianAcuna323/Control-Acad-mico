package dao;

import model.ConexionBD;
import model.Curso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    private ConexionBD conexionDB;

    public CursoDAO() {
        this.conexionDB = ConexionBD.getInstancia();
    }

    /**
     * Crear un nuevo curso usando SP
     * CALL sp_crear_curso(...)
     */
    public boolean crear(Curso curso) {
        String sql = "CALL sp_crear_curso(?, ?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setString(1, curso.getNombreCurso());
            stmt.setInt(2, curso.getPeriodoAcademicoId());
            stmt.setInt(3, curso.getDocenteId());
            stmt.setString(4, curso.getDescripcionCurso());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener curso por ID usando SP
     * CALL sp_obtener_curso(?)
     */
    public Curso obtenerPorId(int cursoId) {
        String sql = "CALL sp_obtener_curso(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCurso(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener curso: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Listar todos los cursos usando SP
     * CALL sp_listar_cursos()
     */
    public List<Curso> listarTodos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "CALL sp_listar_cursos()";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cursos.add(mapearCurso(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar cursos: " + e.getMessage());
            e.printStackTrace();
        }
        return cursos;
    }

    /**
     * Listar cursos por periodo académico usando SP
     * CALL sp_listar_cursos_por_periodo(?)
     */
    public List<Curso> listarPorPeriodo(int periodoId) {
        List<Curso> cursos = new ArrayList<>();
        String sql = "CALL sp_listar_cursos_por_periodo(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, periodoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cursos.add(mapearCurso(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar cursos por periodo: " + e.getMessage());
            e.printStackTrace();
        }
        return cursos;
    }

    /**
     * Actualizar curso usando SP
     * CALL sp_actualizar_curso(...)
     */
    public boolean actualizar(Curso curso) {
        String sql = "CALL sp_actualizar_curso(?, ?, ?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, curso.getCursoId());
            stmt.setString(2, curso.getNombreCurso());
            stmt.setInt(3, curso.getPeriodoAcademicoId());
            stmt.setInt(4, curso.getDocenteId());
            stmt.setString(5, curso.getDescripcionCurso());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al actualizar curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar curso usando SP
     * CALL sp_eliminar_curso(?)
     */
    public boolean eliminar(int cursoId) {
        String sql = "CALL sp_eliminar_curso(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, cursoId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Buscar cursos por nombre
     */
    public List<Curso> buscarPorNombre(String nombre) {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT c.*, d.nombre_docente, p.nombre_periodo " +
                "FROM cursos c " +
                "LEFT JOIN docentes d ON c.docente_id = d.docente_id " +
                "LEFT JOIN periodos_academicos p ON c.periodo_academico_id = p.periodo_academico_id " +
                "WHERE c.nombre_curso LIKE ? " +
                "ORDER BY c.nombre_curso";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cursos.add(mapearCursoCompleto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cursos: " + e.getMessage());
            e.printStackTrace();
        }
        return cursos;
    }

    /**
     * Listar cursos de un docente
     */
    public List<Curso> listarPorDocente(int docenteId) {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT c.*, d.nombre_docente, p.nombre_periodo " +
                "FROM cursos c " +
                "LEFT JOIN docentes d ON c.docente_id = d.docente_id " +
                "LEFT JOIN periodos_academicos p ON c.periodo_academico_id = p.periodo_academico_id " +
                "WHERE c.docente_id = ? " +
                "ORDER BY p.fecha_inicio DESC, c.nombre_curso";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setInt(1, docenteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cursos.add(mapearCursoCompleto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar cursos por docente: " + e.getMessage());
            e.printStackTrace();
        }
        return cursos;
    }

    /**
     * Obtener cursos usando la vista completa
     */
    public List<Curso> listarVista() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM vista_cursos_completa ORDER BY nombre_curso";

        try (Statement stmt = conexionDB.getConexion().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Curso curso = new Curso();
                curso.setCursoId(rs.getInt("curso_id"));
                curso.setNombreCurso(rs.getString("nombre_curso"));
                curso.setDescripcionCurso(rs.getString("descripcion_curso"));
                curso.setDocenteId(rs.getInt("docente_id"));
                curso.setPeriodoAcademicoId(rs.getInt("periodo_academico_id"));
                curso.setNombreDocente(rs.getString("nombre_docente"));
                curso.setNombrePeriodo(rs.getString("nombre_periodo"));
                cursos.add(curso);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar vista de cursos: " + e.getMessage());
            e.printStackTrace();
        }
        return cursos;
    }

    /**
     * Mapear ResultSet a objeto Curso (básico)
     */
    private Curso mapearCurso(ResultSet rs) throws SQLException {
        Curso curso = new Curso();
        curso.setCursoId(rs.getInt("curso_id"));
        curso.setNombreCurso(rs.getString("nombre_curso"));
        curso.setPeriodoAcademicoId(rs.getInt("periodo_academico_id"));
        curso.setDocenteId(rs.getInt("docente_id"));
        curso.setDescripcionCurso(rs.getString("descripcion_curso"));

        // Si el SP devuelve información adicional
        try {
            curso.setNombreDocente(rs.getString("nombre_docente"));
            curso.setNombrePeriodo(rs.getString("nombre_periodo"));
        } catch (SQLException e) {
            // Estos campos opcionales pueden no estar presentes
        }

        return curso;
    }

    /**
     * Mapear ResultSet a objeto Curso (con información relacionada)
     */
    private Curso mapearCursoCompleto(ResultSet rs) throws SQLException {
        Curso curso = new Curso();
        curso.setCursoId(rs.getInt("curso_id"));
        curso.setNombreCurso(rs.getString("nombre_curso"));
        curso.setPeriodoAcademicoId(rs.getInt("periodo_academico_id"));
        curso.setDocenteId(rs.getInt("docente_id"));
        curso.setDescripcionCurso(rs.getString("descripcion_curso"));
        curso.setNombreDocente(rs.getString("nombre_docente"));
        curso.setNombrePeriodo(rs.getString("nombre_periodo"));
        return curso;
    }
}

