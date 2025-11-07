package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class ConexionBD {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    private static ConexionBD instancia;
    private Connection conexion;

    // Carga las credenciales desde config.properties
    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));

            URL = props.getProperty("URL");
            USERNAME = props.getProperty("USERNAME");
            PASSWORD = props.getProperty("PASSWORD");

        } catch (IOException e) {
            System.err.println("❌ Error al cargar el archivo config.properties");
            e.printStackTrace();
        }
    }

    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: Driver MySQL no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos");
            e.printStackTrace();
        }
    }

    public static ConexionBD getInstancia() {
        if (instancia == null || !isConexionActiva()) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener la conexión");
            e.printStackTrace();
        }
        return conexion;
    }

    private static boolean isConexionActiva() {
        try {
            return instancia != null &&
                    instancia.conexion != null &&
                    !instancia.conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✅ Conexión cerrada exitosamente");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión");
            e.printStackTrace();
        }
    }

    public ResultSet ejecutarProcedimiento(String procedimiento, Object... parametros) {
        try {
            StringBuilder sql = new StringBuilder("CALL ").append(procedimiento).append("(");
            for (int i = 0; i < parametros.length; i++) {
                sql.append("?");
                if (i < parametros.length - 1) sql.append(",");
            }
            sql.append(")");

            CallableStatement stmt = conexion.prepareCall(sql.toString());

            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }

            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar procedimiento: " + procedimiento);
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet ejecutarConsulta(String sql) {
        try {
            Statement stmt = conexion.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar consulta");
            e.printStackTrace();
            return null;
        }
    }

    public int ejecutarActualizacion(String sql) {
        try {
            Statement stmt = conexion.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar actualización");
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        ConexionBD db = ConexionBD.getInstancia();

        if (db.getConexion() != null) {
            System.out.println("✅ Test de conexión exitoso");

            try {
                ResultSet rs = db.ejecutarConsulta("SELECT DATABASE() as db_name");
                if (rs.next()) {
                    System.out.println("📊 Base de datos actual: " + rs.getString("db_name"));
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            db.cerrarConexion();
        } else {
            System.out.println("❌ Test de conexión fallido");
        }
    }
}
