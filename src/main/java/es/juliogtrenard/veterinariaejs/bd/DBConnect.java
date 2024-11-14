package es.juliogtrenard.veterinariaejs.bd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexión a la base de datos
 */
public class DBConnect {
    /**
     * La conexión
     */
    private final Connection connection;

    /**
     * Lanza la conexión
     *
     * @throws java.sql.SQLException En caso de errores de SQL
     */
    public DBConnect() throws SQLException {
        Properties configuracion = getConfiguracion();
        Properties connConfig = new Properties();
        connConfig.setProperty("user", configuracion.getProperty("user"));
        connConfig.setProperty("password", configuracion.getProperty("password"));

        connection = DriverManager.getConnection("jdbc:mariadb://" + configuracion.getProperty("address") + ":" + configuracion.getProperty("port") + "/" + configuracion.getProperty("database") + "?serverTimezone=Europe/Madrid", connConfig);
        connection.setAutoCommit(true);
        DatabaseMetaData databaseMetaData = connection.getMetaData();

        /*System.out.println();
        System.out.println("--- Datos de conexión ------------------------------------------");
        System.out.printf("Base de datos: %s%n", databaseMetaData.getDatabaseProductName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDatabaseProductVersion());
        System.out.printf("Driver: %s%n", databaseMetaData.getDriverName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDriverVersion());
        System.out.println("----------------------------------------------------------------");
        System.out.println();*/
        connection.setAutoCommit(true);
    }

    /**
     * Configuración para la conexión a la base de datos
     *
     * @return Properties los datos de conexión a la base de datos
     */
    private Properties getConfiguracion() {
        File f = new File("configuracion.properties");
        Properties properties;
        try {
            FileInputStream configFileReader=new FileInputStream(f);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("configuracion.properties not found at config file path " + f.getPath());
        }
        return properties;
    }

    /**
     * Devuelve la conexión creada
     *
     * @return una conexión a la BBDD
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Cierra la conexion con la base de datos
     *
     * @return La conexión cerrada.
     * @throws java.sql.SQLException En caso de errores de SQL al cerrar la conexión.
     */
    public Connection closeConnection() throws SQLException{
        connection.close();
        return connection;
    }

}