package com.comproOro.gestion.model.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.comproOro.gestion.model.modelos.Renovacion;

/**
 * DAO (Data Access Object) para manejar operaciones de base de datos relacionadas con renovaciones.
 * Incluye funciones para crear la tabla, insertar, consultar y cerrar la conexión.
 */
public class RenovacionDAO {

    private Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos y crea la tabla si no existe.
     */
    public RenovacionDAO() {
        this.connection = DatabaseConnection.getConnection();
        crearTablaRenovaciones();
    }

    /**
     * Crea la tabla 'renovaciones' en la base de datos si no existe.
     * La tabla almacena información sobre las renovaciones de contratos.
     */
    private void crearTablaRenovaciones() {
        String sql = "CREATE TABLE IF NOT EXISTS renovaciones (" +
                "idRenovacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idContrato INTEGER NOT NULL, " +
                "fechaRenovacion DATE NOT NULL, " +
                "fechaFinRenovacion DATE NOT NULL, " +
                "version INTEGER NOT NULL, " +
                "importe REAL NOT NULL" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'renovaciones' creada o ya existe.");
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla 'renovaciones': " + e.getMessage());
        }
    }

    /**
     * Guarda una renovación en la base de datos.
     *
     * @param renovacion El objeto {@link Renovacion} que se desea guardar.
     * @return <code>true</code> si la operación fue exitosa, <code>false</code> si hubo un error.
     */
    public boolean guardarRenovacion(Renovacion renovacion) {
        String query = "INSERT INTO renovaciones (idContrato, fechaRenovacion, fechaFinRenovacion, version, importe) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, renovacion.getIdContrato());
            stmt.setDate(2, Date.valueOf(renovacion.getFechaRenovacion()));
            stmt.setDate(3, Date.valueOf(renovacion.getFechaFinRenovacion()));
            stmt.setInt(4, renovacion.getVersion());
            stmt.setDouble(5, renovacion.getImporte());

            int filasInsertadas = stmt.executeUpdate();
            return filasInsertadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las renovaciones almacenadas en la base de datos.
     *
     * @return Lista de objetos .
     */
    public List<Renovacion> obtenerTodasLasRenovaciones() {
        List<Renovacion> renovaciones = new ArrayList<>();
        String sql = "SELECT * FROM renovaciones";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idRenovacion = rs.getInt("idRenovacion");
                String idContrato = rs.getString("idContrato");
                LocalDate fechaRenovacion = rs.getDate("fechaRenovacion").toLocalDate();
                LocalDate fechaFinRenovacion = rs.getDate("fechaFinRenovacion").toLocalDate();
                int version = rs.getInt("version");
                double importe = rs.getDouble("importe");

                renovaciones.add(new Renovacion(idRenovacion, idContrato, fechaRenovacion, fechaFinRenovacion, version, importe));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las renovaciones: " + e.getMessage());
        }

        return renovaciones;
    }

    /**
     * Cierra la conexión con la base de datos si está abierta.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la versión más alta (última) de renovación asociada a un contrato.
     *
     * @param idContrato ID del contrato.
     * @return Número de la última versión, o 0 si no hay registros.
     */
    public int obtenerUltimaVersionPorIdContrato(String idContrato) {
        String sql = "SELECT MAX(version) AS max_version FROM renovaciones WHERE idContrato = ?";
        int maxVersion = 0;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idContrato);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    maxVersion = rs.getInt("max_version");
                    if (maxVersion == 0) {
                        maxVersion = 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxVersion;
    }

    /**
     * Obtiene todas las renovaciones asociadas a un contrato específico.
     *
     * @param idContrato ID del contrato.
     * @return Lista de objetos  asociados al contrato.
     */
    public List<Renovacion> obtenerRenovacionesPorIdContrato(String idContrato) {
        List<Renovacion> renovaciones = new ArrayList<>();
        String sql = "SELECT * FROM renovaciones WHERE idContrato = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idContrato);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idRenovacion = rs.getInt("idRenovacion");
                    LocalDate fechaRenovacion = rs.getDate("fechaRenovacion").toLocalDate();
                    LocalDate fechaFinRenovacion = rs.getDate("fechaFinRenovacion").toLocalDate();
                    int version = rs.getInt("version");
                    double importe = rs.getDouble("importe");

                    renovaciones.add(new Renovacion(idRenovacion, idContrato, fechaRenovacion, fechaFinRenovacion, version, importe));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las renovaciones por idContrato: " + e.getMessage());
        }

        return renovaciones;
    }

    /**
     * Obtiene la última renovación registrada (mayor versión) de un contrato.
     *
     * @param idContrato ID del contrato.
     * @return Objeto  de la última renovación o <code>null</code> si no existe.
     */
    public Renovacion obtenerUltRenovPorIdContrato(String idContrato) {
        String sql = "SELECT * FROM renovaciones WHERE idContrato = ? ORDER BY version DESC LIMIT 1";
        Renovacion renovacion = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idContrato);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idRenovacion = rs.getInt("idRenovacion");
                    LocalDate fechaRenovacion = rs.getDate("fechaRenovacion").toLocalDate();
                    LocalDate fechaFinRenovacion = rs.getDate("fechaFinRenovacion").toLocalDate();
                    int version = rs.getInt("version");
                    double importe = rs.getDouble("importe");

                    renovacion = new Renovacion(idRenovacion, idContrato, fechaRenovacion, fechaFinRenovacion, version, importe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return renovacion;
    }
}
