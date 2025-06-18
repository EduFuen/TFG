package com.comproOro.gestion.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;

/**
 * Clase encargada de gestionar la conexión con la base de datos SQLite.
 * Utiliza una base de datos local ubicada en el directorio del proyecto.
 */
public class DatabaseConnection {

    /**
     * Establece y devuelve una conexión a la base de datos SQLite.
     * <p>
     * La base de datos se encuentra en el directorio de trabajo actual
     * y se llama <code>mi_base_de_datos.db</code>.
     *
     * @return una instancia {@link Connection} conectada a la base de datos,
     *         o <code>null</code> si ocurre un error al establecer la conexión.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Obtener el directorio actual del proyecto
            String currentDir = System.getProperty("user.dir");

            // Construir la ruta absoluta hacia el archivo de la base de datos
            String dbPath = Paths.get(currentDir, "mi_base_de_datos.db").toString();

            // Establecer la conexión a la base de datos SQLite
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            System.out.println("Conexión establecida con la base de datos SQLite en: " + dbPath);

        } catch (SQLException e) {
            System.err.println("Error al establecer la conexión: " + e.getMessage());
        }

        return connection;
    }
}
