package dao;

import model.Estudiante;
import model.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar operaciones CRUD de Estudiantes
 * Utiliza los Stored Procedures de la base de datos
 */

public class EstudianteDao {
    private ConexionBD conexionBD;

    public EstudianteDao() {
        this.conexionBD = ConexionBD.getInstancia();
    }

    /**
     * Crear un nuevo estudiante usando SP
     * CALL sp_crear_estudiante(...)
     */
    public boolean crear(Estudiante estudiante) {
        String sql = "CALL sp_crear_estudiante(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (CallableStatement stmt = conexionBD.getConexion().prepareCall(sql)) {
            stmt.setString(1, estudiante.getIdentificacion());
            stmt.setString(2, estudiante.getNombre());
            stmt.setString(3, estudiante.getCorreoInstitucional());
            stmt.setString(4, estudiante.getCorreoPersonal());
            stmt.setString(5, estudiante.getTelefono());
            stmt.setInt(6, estudiante.isEsVocero() ? 1 : 0);
            stmt.setString(7, estudiante.getTipoDocumento());
            stmt.setString(8, estudiante.getGenero());
            stmt.setString(9, estudiante.getComentarios());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear estudiante: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener estudiante por ID usando SP
     * CALL sp_obtener_estudiante(?)
     */
    public Estudiante obtenerPorId(int estudianteId) {
        String sql = "CALL sp_obtener_estudiante(?)";

        try (CallableStatement stmt = conexionBD.getConexion().prepareCall(sql)) {
            stmt.setInt(1, estudianteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEstudiante(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estudiante: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Listar todos los estudiantes usando SP
     * CALL sp_listar_estudiantes()
     */
    public List<Estudiante> listarTodos() {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "CALL sp_listar_estudiantes()";

        try (CallableStatement stmt = conexionBD.getConexion().prepareCall(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                estudiantes.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar estudiantes: " + e.getMessage());
            e.printStackTrace();
        }
        return estudiantes;
    }

    /**
     * Buscar estudiante por identificación usando SP
     * CALL sp_buscar_estudiante_por_identificacion(?)
     */
    public Estudiante buscarPorIdentificacion(String identificacion) {
        String sql = "CALL sp_buscar_estudiante_por_identificacion(?)";

        try (CallableStatement stmt = conexionBD.getConexion().prepareCall(sql)) {
            stmt.setString(1, identificacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEstudiante(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar estudiante: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Buscar estudiantes por nombre usando SP
     * CALL sp_buscar_estudiantes_por_nombre(?)
     */
    public List<Estudiante> buscarPorNombre(String nombre) {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "CALL sp_buscar_estudiantes_por_nombre(?)";

        try (CallableStatement stmt = conexionBD.getConexion().prepareCall(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                estudiantes.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return estudiantes;
    }

    /**
     * Actualizar estudiante usando SP
     * CALL sp_actualizar_estudiante(...)
     */
    public boolean actualizar(Estudiante estudiante) {
        String sql = "CALL sp_actualizar_estudiante(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (CallableStatement stmt = conexionBD.getConexion().prepareCall(sql)) {
            stmt.setInt(1, estudiante.getEstudianteId());
            stmt.setString(2, estudiante.getIdentificacion());
            stmt.setString(3, estudiante.getNombre());
            stmt.setString(4, estudiante.getCorreoInstitucional());
            stmt.setString(5, estudiante.getCorreoPersonal());
            stmt.setString(6, estudiante.getTelefono());
            stmt.setInt(7, estudiante.isEsVocero() ? 1 : 0);
            stmt.setString(8, estudiante.getTipoDocumento());
            stmt.setString(9, estudiante.getGenero());
            stmt.setString(10, estudiante.getComentarios());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estudiante: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar estudiante usando SP
     * CALL sp_eliminar_estudiante(?)
     */
    public boolean eliminar(int estudianteId) {
        String sql = "CALL sp_eliminar_estudiante(?)";

        try (CallableStatement stmt = conexionBD.getConexion().prepareCall(sql)) {
            stmt.setInt(1, estudianteId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar estudiante: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener lista de voceros
     */
    public List<Estudiante> listarVoceros() {
        List<Estudiante> voceros = new ArrayList<>();
        String sql = "SELECT * FROM vista_estudiantes_contacto WHERE es_vocero = 1";

        try (Statement stmt = conexionBD.getConexion().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                voceros.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar voceros: " + e.getMessage());
            e.printStackTrace();
        }
        return voceros;
    }

    /**
     * Mapear ResultSet a objeto Estudiante
     */
    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante();
        estudiante.setEstudianteId(rs.getInt("estudiante_id"));
        estudiante.setIdentificacion(rs.getString("identificacion"));
        estudiante.setNombre(rs.getString("nombre"));
        estudiante.setCorreoInstitucional(rs.getString("correo_institucional"));
        estudiante.setCorreoPersonal(rs.getString("correo_personal"));
        estudiante.setTelefono(rs.getString("telefono"));
        estudiante.setEsVocero(rs.getBoolean("es_vocero"));
        estudiante.setTipoDocumento(rs.getString("tipo_documento"));
        estudiante.setGenero(rs.getString("genero"));
        estudiante.setComentarios(rs.getString("comentarios"));
        return estudiante;
    }

    /**
     * Método de prueba
     */
    public static void main(String[] args) {
        EstudianteDao dao = new EstudianteDao();

        // Listar todos los estudiantes
        System.out.println("=== LISTANDO ESTUDIANTES ===");
        List<Estudiante> estudiantes = dao.listarTodos();
        for (Estudiante e : estudiantes) {
            System.out.println(e);
        }

        // Buscar por ID
        System.out.println("\n=== BUSCANDO ESTUDIANTE ID 1 ===");
        Estudiante estudiante = dao.obtenerPorId(1);
        if (estudiante != null) {
            System.out.println(estudiante);
        }
    }
}
