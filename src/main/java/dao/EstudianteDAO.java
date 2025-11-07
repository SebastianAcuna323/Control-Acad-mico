package dao;

import model.Estudiante;
import model.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EstudianteDAO {
    private ConexionBD conexionBD;

    public EstudianteDAO() {
        this.conexionBD = ConexionBD.getInstancia();
    }


     //Crear un nuevo estudiante usando SP

    public boolean crear(Estudiante estudiante) {
        String sql = "CALL sp_crear_estudiante(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            System.out.println("=== DEBUG: Creando estudiante ===");
            System.out.println("Identificación: " + estudiante.getIdentificacion());
            System.out.println("Nombre: " + estudiante.getNombre());
            System.out.println("Correo Institucional: " + estudiante.getCorreoInstitucional());

            stmt = conexionBD.getConexion().prepareCall(sql);

            stmt.setString(1, estudiante.getIdentificacion());
            stmt.setString(2, estudiante.getNombre());
            stmt.setString(3, estudiante.getCorreoInstitucional());
            stmt.setString(4, estudiante.getCorreoPersonal());
            stmt.setString(5, estudiante.getTelefono());
            stmt.setInt(6, estudiante.isEsVocero() ? 1 : 0);
            stmt.setString(7, estudiante.getTipoDocumento());
            stmt.setString(8, estudiante.getGenero());
            stmt.setString(9, estudiante.getComentarios());

            System.out.println("Ejecutando procedimiento almacenado...");
            rs = stmt.executeQuery();

            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println("Resultado: " + resultado);

                if (resultado.contains("Éxito") || resultado.contains("Exito")) {
                    return true;
                } else {
                    System.err.println("Error del SP: " + resultado);
                    return false;
                }
            }

            System.err.println("El procedimiento no devolvió resultado");
            return false;

        } catch (SQLException e) {
            System.err.println("===== ERROR SQL DETALLADO =====");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public Estudiante obtenerPorId(int estudianteId) {
        String sql = "CALL sp_obtener_estudiante(?)";
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conexionBD.getConexion().prepareCall(sql);
            stmt.setInt(1, estudianteId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEstudiante(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estudiante: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public List<Estudiante> listarTodos() {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "CALL sp_listar_estudiantes()";
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conexionBD.getConexion().prepareCall(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                estudiantes.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar estudiantes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estudiantes;
    }


    public Estudiante buscarPorIdentificacion(String identificacion) {
        String sql = "CALL sp_buscar_estudiante_por_identificacion(?)";
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conexionBD.getConexion().prepareCall(sql);
            stmt.setString(1, identificacion);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEstudiante(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar estudiante: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Estudiante> buscarPorNombre(String nombre) {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "CALL sp_buscar_estudiantes_por_nombre(?)";
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conexionBD.getConexion().prepareCall(sql);
            stmt.setString(1, nombre);
            rs = stmt.executeQuery();

            while (rs.next()) {
                estudiantes.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar por nombre: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return estudiantes;
    }


    public boolean actualizar(Estudiante estudiante) {
        String sql = "CALL sp_actualizar_estudiante(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conexionBD.getConexion().prepareCall(sql);
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

            rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito") || resultado.contains("Exito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estudiante: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //Eliminar estudiante usando SP

    public boolean eliminar(int estudianteId) {
        String sql = "CALL sp_eliminar_estudiante(?)";
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conexionBD.getConexion().prepareCall(sql);
            stmt.setInt(1, estudianteId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito") || resultado.contains("Exito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar estudiante: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Estudiante> listarVoceros() {
        List<Estudiante> voceros = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes WHERE es_vocero = 1 ORDER BY nombre";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conexionBD.getConexion().createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                voceros.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar voceros: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return voceros;
    }


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
}