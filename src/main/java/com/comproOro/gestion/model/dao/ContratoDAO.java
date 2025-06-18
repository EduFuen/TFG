package com.comproOro.gestion.model.dao;

import com.comproOro.gestion.model.modelos.Contrato;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para manejar las operaciones CRUD y consultas sobre la tabla contratos.
 */
public class ContratoDAO {

    /**
     * Constructor que crea la tabla contratos si no existe.
     */
    public ContratoDAO() {
        crearTablaContratos();
    }

    /**
     * Crea la tabla contratos en la base de datos si no existe.
     */
    private void crearTablaContratos() {
        String sql = "CREATE TABLE IF NOT EXISTS contratos ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " id_contrato TEXT NOT NULL UNIQUE, "
                + " id_pol TEXT, "
                + " dni_cliente TEXT NOT NULL, "
                + " detalles_contrato TEXT NOT NULL, "
                + " fecha_inicio DATE NOT NULL, "
                + " fecha_final DATE, "
                + " tipo TEXT NOT NULL, "
                + " rescatado TEXT DEFAULT 'N', "
                + " fecha_rescate DATE, "
                + " importe REAL NOT NULL DEFAULT 0.0"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'contratos' creada o ya existe.");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla contratos: " + e.getMessage());
        }
    }

    /**
     * Obtiene el próximo número incremental para el id_contrato basado en el tipo y el año actual.
     *
     * @param tipo Tipo de contrato para filtrar.
     * @return El siguiente número de contrato disponible para ese tipo.
     */
    private int obtenerProximoNumeroContrato(String tipo) {
        int maxNumero = 0;
        String sql = "SELECT MAX(CAST(SUBSTR(id_contrato, 8) AS INTEGER)) AS max_num "
                + "FROM contratos WHERE tipo = ? AND SUBSTR(id_contrato, 3, 4) = ?";

        String anoActual = new SimpleDateFormat("yyyy").format(new java.util.Date());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            pstmt.setString(2, anoActual);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                maxNumero = rs.getInt("max_num");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el próximo número de contrato: " + e.getMessage());
        }
        return maxNumero + 1;
    }

    /**
     * Obtiene el próximo número incremental para la póliza (id_pol), basándose en los últimos 4 dígitos y el año actual.
     *
     * @return El siguiente número disponible para id_pol.
     */
    public int obtenerProximoNumeroPoliza() {
        int maxNumero = 0;

        String sql = "SELECT MAX(CAST(SUBSTR(id_pol, 7) AS INTEGER)) AS max_num "
                + "FROM contratos WHERE id_pol IS NOT NULL AND SUBSTR(id_pol, 3, 4) = ?";

        String anoActual = new SimpleDateFormat("yyyy").format(new java.util.Date());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anoActual);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                maxNumero = rs.getInt("max_num");
                if (rs.wasNull()) {
                    maxNumero = 0;
                }
            }

            if (maxNumero == 0) {
                maxNumero = 1;
            } else {
                maxNumero += 1;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el próximo número de póliza: " + e.getMessage());
        }

        return maxNumero;
    }

    /**
     * Guarda un nuevo contrato en la base de datos.
     *
     * @param contrato Objeto Contrato con los datos a guardar.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    public boolean guardarContrato(Contrato contrato) {

        int numeroContrato = obtenerProximoNumeroContrato(contrato.getTipo());
        contrato.generarIdContrato(numeroContrato);

        String sql = "INSERT INTO contratos (id_contrato, id_pol, dni_cliente, detalles_contrato, fecha_inicio, fecha_final, tipo, rescatado, fecha_rescate, importe) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contrato.getIdContrato());
            pstmt.setString(2, contrato.getIdPol());
            pstmt.setString(3, contrato.getDniCliente());
            pstmt.setString(4, contrato.getDetallesContrato());
            pstmt.setDate(5, new Date(contrato.getFechaInicio().getTime()));

            if (contrato.getFechaFinal() != null) {
                pstmt.setDate(6, new Date(contrato.getFechaFinal().getTime()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }

            pstmt.setString(7, contrato.getTipo());
            pstmt.setString(8, contrato.getRescatado());

            if (contrato.getFechaRescate() != null) {
                pstmt.setDate(9, new Date(contrato.getFechaRescate().getTime()));
            } else {
                pstmt.setNull(9, Types.DATE);
            }

            pstmt.setDouble(10, contrato.getImporte());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar el contrato: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un contrato según su id_contrato.
     *
     * @param idContrato Identificador único del contrato.
     * @return Objeto Contrato con la información o null si no se encuentra.
     */
    public Contrato obtenerContratoPorId(String idContrato) {
        String sql = "SELECT * FROM contratos WHERE id_contrato = ?";
        Contrato contrato = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idContrato);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    contrato = new Contrato();
                    contrato.setIdContrato(rs.getString("id_contrato"));
                    contrato.setIdPol(rs.getString("id_pol"));
                    contrato.setDniCliente(rs.getString("dni_cliente"));
                    contrato.setDetallesContrato(rs.getString("detalles_contrato"));
                    contrato.setFechaInicio(rs.getDate("fecha_inicio"));
                    contrato.setFechaFinal(rs.getDate("fecha_final"));
                    contrato.setTipo(rs.getString("tipo"));
                    contrato.setRescatado(rs.getString("rescatado"));
                    contrato.setFechaRescate(rs.getDate("fecha_rescate"));
                    contrato.setImporte(rs.getDouble("importe"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el contrato por ID: " + e.getMessage());
        }

        return contrato;
    }

    /**
     * Actualiza los datos de un contrato existente en la base de datos.
     *
     * @param contrato Objeto Contrato con los datos actualizados.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    public boolean actualizarContrato(Contrato contrato) {
        String sql = "UPDATE contratos SET id_pol = ?, dni_cliente = ?, detalles_contrato = ?, fecha_inicio = ?, fecha_final = ?, tipo = ?, rescatado = ?, fecha_rescate = ?, importe = ? WHERE id_contrato = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contrato.getIdPol());
            pstmt.setString(2, contrato.getDniCliente());
            pstmt.setString(3, contrato.getDetallesContrato());
            pstmt.setDate(4, new Date(contrato.getFechaInicio().getTime()));

            if (contrato.getFechaFinal() != null) {
                pstmt.setDate(5, new Date(contrato.getFechaFinal().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setString(6, contrato.getTipo());
            pstmt.setString(7, contrato.getRescatado());

            if (contrato.getFechaRescate() != null) {
                pstmt.setDate(8, new Date(contrato.getFechaRescate().getTime()));
            } else {
                pstmt.setNull(8, Types.DATE);
            }

            pstmt.setDouble(9, contrato.getImporte());
            pstmt.setString(10, contrato.getIdContrato());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el contrato: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene una lista con todos los contratos almacenados.
     *
     * @return Lista de objetos Contrato.
     */
    public List<Contrato> obtenerTodosLosContratos() {
        List<Contrato> contratos = new ArrayList<>();
        String sql = "SELECT * FROM contratos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Contrato contrato = new Contrato();
                contrato.setIdContrato(rs.getString("id_contrato"));
                contrato.setIdPol(rs.getString("id_pol"));
                contrato.setDniCliente(rs.getString("dni_cliente"));
                contrato.setDetallesContrato(rs.getString("detalles_contrato"));
                contrato.setFechaInicio(rs.getDate("fecha_inicio"));
                contrato.setFechaFinal(rs.getDate("fecha_final"));
                contrato.setTipo(rs.getString("tipo"));
                contrato.setRescatado(rs.getString("rescatado"));
                contrato.setFechaRescate(rs.getDate("fecha_rescate"));
                contrato.setImporte(rs.getDouble("importe"));

                contratos.add(contrato);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los contratos: " + e.getMessage());
        }

        return contratos;
    }

    /**
     * Obtiene una lista de contratos de tipo "EMPEÑO" para un cliente específico identificado por DNI.
     *
     * @param dni DNI del cliente.
     * @return Lista de contratos de empeño o vacía si no se encuentran.
     */
    public List<Contrato> obtenerContratosEmpenoPorDni(String dni) {
        List<Contrato> contratos = new ArrayList<>();
        String sql = "SELECT * FROM contratos WHERE TRIM(dni_cliente) = ? AND UPPER(tipo) = 'EMPENO'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Contrato contrato = new Contrato();
                    contrato.setIdContrato(rs.getString("id_contrato"));
                    contrato.setIdPol(rs.getString("id_pol"));
                    contrato.setDniCliente(rs.getString("dni_cliente"));
                    contrato.setDetallesContrato(rs.getString("detalles_contrato"));
                    contrato.setFechaInicio(rs.getDate("fecha_inicio"));
                    contrato.setFechaFinal(rs.getDate("fecha_final"));
                    contrato.setTipo(rs.getString("tipo"));
                    contrato.setRescatado(rs.getString("rescatado"));
                    contrato.setFechaRescate(rs.getDate("fecha_rescate"));
                    contrato.setImporte(rs.getDouble("importe"));

                    contratos.add(contrato);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener contratos de empeño por DNI: " + e.getMessage());
        }

        if (contratos.isEmpty()) {
            System.out.println("No se encontraron contratos de tipo empeño para el DNI: " + dni);
        }

        return contratos;
    }

    /**
     * Actualiza el DNI en todos los contratos que coincidan con el DNI antiguo.
     *
     * @param dniAntiguo DNI anterior.
     * @param dniNuevo   DNI nuevo para actualizar.
     * @return true si se actualizaron filas, false si no o en caso de error.
     */
    public boolean actualizarDniEnContratos(String dniAntiguo, String dniNuevo) {
        String sql = "UPDATE contratos SET dni_cliente = ? WHERE dni_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dniNuevo);
            stmt.setString(2, dniAntiguo);

            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un cliente tiene contratos asociados.
     *
     * @param dniCliente El DNI del cliente a verificar.
     * @return {@code true} si el cliente tiene al menos un contrato, {@code false} en caso contrario.
     */
    public boolean tieneContratos(String dniCliente) {
        String sql = "SELECT COUNT(*) FROM contratos WHERE dni_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dniCliente);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar contratos: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca contratos que coincidan con el texto proporcionado y tipo especificado.
     *
     * @param searchText Texto a buscar en el ID del contrato o en el DNI del cliente.
     * @param tipo       Tipo de contrato a filtrar. Si es "Cualquiera", no se aplica filtro por tipo.
     * @return Lista de contratos que cumplen con los criterios de búsqueda.
     */
    public List<Contrato> buscarContratos(String searchText, String tipo) {
        List<Contrato> contratos = new ArrayList<>();
        String sql = "SELECT * FROM contratos WHERE (id_contrato LIKE ? OR dni_cliente LIKE ?)";

        if (tipo != null && !tipo.equals("Cualquiera")) {
            sql += " AND tipo = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchText + "%");
            pstmt.setString(2, "%" + searchText + "%");

            if (tipo != null && !tipo.equals("Cualquiera")) {
                pstmt.setString(3, tipo);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Contrato contrato = new Contrato();
                contrato.setIdContrato(rs.getString("id_contrato"));
                contrato.setIdPol(rs.getString("id_pol"));
                contrato.setDniCliente(rs.getString("dni_cliente"));
                contrato.setFechaInicio(rs.getDate("fecha_inicio"));
                contrato.setFechaFinal(rs.getDate("fecha_final"));
                contrato.setTipo(rs.getString("tipo"));
                contrato.setRescatado(rs.getString("rescatado"));
                contrato.setImporte(rs.getDouble("importe"));
                contratos.add(contrato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contratos;
    }

    /**
     * Busca contratos por texto, tipo y un rango de fechas de inicio.
     *
     * @param searchText  Texto a buscar en el ID del contrato o DNI del cliente.
     * @param tipo        Tipo de contrato a filtrar.
     * @param fechaInicio Fecha mínima de inicio del contrato.
     * @param fechaFinal  Fecha máxima de inicio del contrato.
     * @return Lista de contratos que coinciden con los filtros.
     */
    public List<Contrato> buscarContratosConFechas(String searchText, String tipo, java.sql.Date fechaInicio, java.sql.Date fechaFinal) {
        List<Contrato> contratos = new ArrayList<>();
        String sql = "SELECT * FROM contratos WHERE (id_contrato LIKE ? OR dni_cliente LIKE ?)";

        if (tipo != null && !tipo.equals("Cualquiera")) {
            sql += " AND tipo = ?";
        }

        if (fechaInicio != null && fechaFinal != null) {
            sql += " AND fecha_inicio > ? AND fecha_inicio < ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchText + "%");
            pstmt.setString(2, "%" + searchText + "%");

            int paramIndex = 3;
            if (tipo != null && !tipo.equals("Cualquiera")) {
                pstmt.setString(paramIndex++, tipo);
            }

            if (fechaInicio != null && fechaFinal != null) {
                pstmt.setDate(paramIndex++, fechaInicio);
                pstmt.setDate(paramIndex, fechaFinal);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Contrato contrato = new Contrato();
                contrato.setIdContrato(rs.getString("id_contrato"));
                contrato.setIdPol(rs.getString("id_pol"));
                contrato.setDniCliente(rs.getString("dni_cliente"));
                contrato.setFechaInicio(rs.getDate("fecha_inicio"));
                contrato.setFechaFinal(rs.getDate("fecha_final"));
                contrato.setTipo(rs.getString("tipo"));
                contrato.setRescatado(rs.getString("rescatado"));
                contrato.setImporte(rs.getDouble("importe"));
                contratos.add(contrato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contratos;
    }

    /**
     * Busca contratos a partir de una fecha de inicio , con filtros opcionales.
     *
     * @param searchText  Texto a buscar en el ID del contrato o DNI del cliente.
     * @param tipo        Tipo de contrato a filtrar.
     * @param fechaInicio Fecha mínima de inicio del contrato.
     * @return Lista de contratos filtrados por fecha de inicio y otros criterios.
     */
    public List<Contrato> buscarContratosConFechaInicio(String searchText, String tipo, Date fechaInicio) {
        List<Contrato> contratos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM contratos WHERE fecha_inicio >= ?");
        List<String> conditions = new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            conditions.add("(id_contrato LIKE ? OR dni_cliente LIKE ?)");
        }

        if (tipo != null && !tipo.isEmpty()) {
            conditions.add("tipo = ?");
        }

        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            pstmt.setDate(1, fechaInicio);
            int index = 2;

            if (searchText != null && !searchText.isEmpty()) {
                pstmt.setString(index++, "%" + searchText + "%");
                pstmt.setString(index++, "%" + searchText + "%");
            }

            if (tipo != null && !tipo.isEmpty()) {
                pstmt.setString(index++, tipo);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Contrato contrato = new Contrato();
                    contrato.setIdContrato(rs.getString("id_contrato"));
                    contrato.setIdPol(rs.getString("id_pol"));
                    contrato.setDniCliente(rs.getString("dni_cliente"));
                    contrato.setDetallesContrato(rs.getString("detalles_contrato"));
                    contrato.setFechaInicio(rs.getDate("fecha_inicio"));
                    contrato.setFechaFinal(rs.getDate("fecha_final"));
                    contrato.setTipo(rs.getString("tipo"));
                    contrato.setRescatado(rs.getString("rescatado"));
                    contrato.setFechaRescate(rs.getDate("fecha_rescate"));
                    contrato.setImporte(rs.getDouble("importe"));
                    contratos.add(contrato);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar contratos por fecha de inicio: " + e.getMessage());
        }

        return contratos;
    }

    /**
     * Busca contratos cuya fecha de inicio sea menor o igual a una fecha dada, con filtros opcionales.
     *
     * @param searchText Texto a buscar en el ID del contrato o DNI del cliente.
     * @param tipo       Tipo de contrato a filtrar.
     * @param fechaFinal Fecha máxima de inicio del contrato.
     * @return Lista de contratos que cumplen con los filtros aplicados.
     */
    public List<Contrato> buscarContratosConFechaFinal(String searchText, String tipo, Date fechaFinal) {
        List<Contrato> contratos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM contratos WHERE fecha_inicio <= ?");
        List<String> conditions = new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            conditions.add("(id_contrato LIKE ? OR dni_cliente LIKE ?)");
        }

        if (tipo != null && !tipo.isEmpty()) {
            conditions.add("tipo = ?");
        }

        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            pstmt.setDate(1, fechaFinal);
            int index = 2;

            if (searchText != null && !searchText.isEmpty()) {
                pstmt.setString(index++, "%" + searchText + "%");
                pstmt.setString(index++, "%" + searchText + "%");
            }

            if (tipo != null && !tipo.isEmpty()) {
                pstmt.setString(index++, tipo);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Contrato contrato = new Contrato();
                    contrato.setIdContrato(rs.getString("id_contrato"));
                    contrato.setIdPol(rs.getString("id_pol"));
                    contrato.setDniCliente(rs.getString("dni_cliente"));
                    contrato.setDetallesContrato(rs.getString("detalles_contrato"));
                    contrato.setFechaInicio(rs.getDate("fecha_inicio"));
                    contrato.setFechaFinal(rs.getDate("fecha_final"));
                    contrato.setTipo(rs.getString("tipo"));
                    contrato.setRescatado(rs.getString("rescatado"));
                    contrato.setFechaRescate(rs.getDate("fecha_rescate"));
                    contrato.setImporte(rs.getDouble("importe"));
                    contratos.add(contrato);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar contratos por fecha de finalización: " + e.getMessage());
        }

        return contratos;
    }
}