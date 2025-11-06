package model;

import java.sql.*;

public class ConexionBD {

    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/control-academico";
    private static final String USUARIO = "root";
    private static final String PASSWORD = ""; // Cambiar según tu configuración

    private static ConexionBD instancia;
    private Connection conexion;

    /**
     * Constructor privado para patrón Singleton
     */
    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("✅ Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: Driver MySQL no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la instancia única de la conexión (Singleton)
     */
    public static ConexionBD getInstancia() {
        if (instancia == null || !isConexionActiva()) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Obtiene la conexión activa
     */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener la conexión");
            e.printStackTrace();
        }
        return conexion;
    }

    /**
     * Verifica si la conexión está activa
     */
    private static boolean isConexionActiva() {
        try {
            return instancia != null &&
                    instancia.conexion != null &&
                    !instancia.conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Cierra la conexión
     */
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

    /**
     * Ejecuta un procedimiento almacenado
     * @param procedimiento Nombre del procedimiento
     * @param parametros Parámetros del procedimiento
     * @return ResultSet con los resultados
     */
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

    /**
     * Ejecuta una consulta SQL
     * @param sql Consulta SQL
     * @return ResultSet con los resultados
     */
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

    /**
     * Ejecuta una actualización SQL (INSERT, UPDATE, DELETE)
     * @param sql Sentencia SQL
     * @return Número de filas afectadas
     */
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

    /**
     * Test de conexión
     */
    public static void main(String[] args) {
        ConexionBD db = ConexionBD.getInstancia();

        if (db.getConexion() != null) {
            System.out.println("✅ Test de conexión exitoso");

            // Probar una consulta simple
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
