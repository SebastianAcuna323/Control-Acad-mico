package dao;

import model.ConexionBD;
import model.Docente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDAO {

    private ConexionBD conexionDB;

    public DocenteDAO() {
        this.conexionDB = ConexionBD.getInstancia();
    }

    /**
     * Crear un nuevo docente usando SP
     * CALL sp_crear_docente(...)
     */
    public boolean crear(Docente docente) {
        String sql = "CALL sp_crear_docente(?, ?, ?, ?, ?, ?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setString(1, docente.getNombreDocente());
            stmt.setString(2, docente.getIdentificacion());
            stmt.setString(3, docente.getTipoIdentificacion());
            stmt.setString(4, docente.getGenero());
            stmt.setString(5, docente.getCorreo());
            stmt.setString(6, docente.getTituloEstudios());
            stmt.setString(7, docente.getIdiomas());
            stmt.setString(8, docente.getCertificaciones());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear docente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener docente por ID usando SP
     * CALL sp_obtener_docente(?)
     */
    public Docente obtenerPorId(int docenteId) {
        String sql = "CALL sp_obtener_docente(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, docenteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearDocente(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener docente: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Listar todos los docentes usando SP
     * CALL sp_listar_docentes()
     */
    public List<Docente> listarTodos() {
        List<Docente> docentes = new ArrayList<>();
        String sql = "CALL sp_listar_docentes()";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                docentes.add(mapearDocente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar docentes: " + e.getMessage());
            e.printStackTrace();
        }
        return docentes;
    }

    /**
     * Actualizar docente usando SP
     * CALL sp_actualizar_docente(...)
     */
    public boolean actualizar(Docente docente) {
        String sql = "CALL sp_actualizar_docente(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, docente.getDocenteId());
            stmt.setString(2, docente.getNombreDocente());
            stmt.setString(3, docente.getIdentificacion());
            stmt.setString(4, docente.getTipoIdentificacion());
            stmt.setString(5, docente.getGenero());
            stmt.setString(6, docente.getCorreo());
            stmt.setString(7, docente.getTituloEstudios());
            stmt.setString(8, docente.getIdiomas());
            stmt.setString(9, docente.getCertificaciones());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al actualizar docente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar docente usando SP
     * CALL sp_eliminar_docente(?)
     */
    public boolean eliminar(int docenteId) {
        String sql = "CALL sp_eliminar_docente(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, docenteId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar docente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Buscar docentes por nombre
     */
    public List<Docente> buscarPorNombre(String nombre) {
        List<Docente> docentes = new ArrayList<>();
        String sql = "SELECT * FROM docentes WHERE nombre_docente LIKE ? ORDER BY nombre_docente";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                docentes.add(mapearDocente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar docentes: " + e.getMessage());
            e.printStackTrace();
        }
        return docentes;
    }

    /**
     * Obtener docentes con sus cursos (usando vista)
     */
    public List<Docente> listarConCursos() {
        List<Docente> docentes = new ArrayList<>();
        String sql = "SELECT DISTINCT d.* FROM vista_docentes_cursos d ORDER BY d.nombre_docente";

        try (Statement stmt = conexionDB.getConexion().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                docentes.add(mapearDocente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar docentes con cursos: " + e.getMessage());
            e.printStackTrace();
        }
        return docentes;
    }

    /**
     * Mapear ResultSet a objeto Docente
     */
    private Docente mapearDocente(ResultSet rs) throws SQLException {
        Docente docente = new Docente();
        docente.setDocenteId(rs.getInt("docente_id"));
        docente.setNombreDocente(rs.getString("nombre_docente"));
        docente.setIdentificacion(rs.getString("identificacion"));
        docente.setTipoIdentificacion(rs.getString("tipo_identificacion"));
        docente.setGenero(rs.getString("genero"));
        docente.setCorreo(rs.getString("correo"));
        docente.setTituloEstudios(rs.getString("titulo_estudios"));
        docente.setIdiomas(rs.getString("idiomas"));
        docente.setCertificaciones(rs.getString("certificaciones"));
        return docente;
    }
}



