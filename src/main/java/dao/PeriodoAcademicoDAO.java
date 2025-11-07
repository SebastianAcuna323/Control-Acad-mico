package dao;

import model.ConexionBD;
import model.PeriodoAcademico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeriodoAcademicoDAO {

    private ConexionBD conexionDB;

    public PeriodoAcademicoDAO() {
        this.conexionDB = ConexionBD.getInstancia();
    }


     //Crear un nuevo periodo académico usando SP

    public boolean crear(PeriodoAcademico periodo) {
        String sql = "CALL sp_crear_periodo(?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setString(1, periodo.getNombrePeriodo());
            stmt.setDate(2, periodo.getFechaInicio());
            stmt.setDate(3, periodo.getFechaFin());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear periodo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



    public PeriodoAcademico obtenerPorId(int periodoId) {
        String sql = "CALL sp_obtener_periodo(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, periodoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPeriodo(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener periodo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


     //Listar todos los periodos

    public List<PeriodoAcademico> listarTodos() {
        List<PeriodoAcademico> periodos = new ArrayList<>();
        String sql = "CALL sp_listar_periodos()";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                periodos.add(mapearPeriodo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar periodos: " + e.getMessage());
            e.printStackTrace();
        }
        return periodos;
    }


    public List<PeriodoAcademico> listarActivos() {
        List<PeriodoAcademico> periodos = new ArrayList<>();
        String sql = "CALL sp_listar_periodos_activos()";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                periodos.add(mapearPeriodo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar periodos activos: " + e.getMessage());
            e.printStackTrace();
        }
        return periodos;
    }


    public boolean actualizar(PeriodoAcademico periodo) {
        String sql = "CALL sp_actualizar_periodo(?, ?, ?, ?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, periodo.getPeriodoAcademicoId());
            stmt.setString(2, periodo.getNombrePeriodo());
            stmt.setDate(3, periodo.getFechaInicio());
            stmt.setDate(4, periodo.getFechaFin());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al actualizar periodo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean eliminar(int periodoId) {
        String sql = "CALL sp_eliminar_periodo(?)";

        try (CallableStatement stmt = conexionDB.getConexion().prepareCall(sql)) {
            stmt.setInt(1, periodoId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String resultado = rs.getString("resultado");
                System.out.println(resultado);
                return resultado.contains("Éxito");
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar periodo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public List<PeriodoAcademico> buscarPorNombre(String nombre) {
        List<PeriodoAcademico> periodos = new ArrayList<>();
        String sql = "SELECT * FROM periodos_academicos WHERE nombre_periodo LIKE ? ORDER BY fecha_inicio DESC";

        try (PreparedStatement stmt = conexionDB.getConexion().prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                periodos.add(mapearPeriodo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar periodos: " + e.getMessage());
            e.printStackTrace();
        }
        return periodos;
    }

    public PeriodoAcademico obtenerPeriodoActual() {
        String sql = "SELECT * FROM periodos_academicos " +
                "WHERE CURDATE() BETWEEN fecha_inicio AND fecha_fin " +
                "LIMIT 1";

        try (Statement stmt = conexionDB.getConexion().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return mapearPeriodo(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener periodo actual: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    private PeriodoAcademico mapearPeriodo(ResultSet rs) throws SQLException {
        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setPeriodoAcademicoId(rs.getInt("periodo_academico_id"));
        periodo.setNombrePeriodo(rs.getString("nombre_periodo"));
        periodo.setFechaInicio(rs.getDate("fecha_inicio"));
        periodo.setFechaFin(rs.getDate("fecha_fin"));
        return periodo;
    }
}



