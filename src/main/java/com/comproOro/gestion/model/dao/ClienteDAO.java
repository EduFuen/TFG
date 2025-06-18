package com.comproOro.gestion.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.comproOro.gestion.model.modelos.Cliente;

/**
 * Clase DAO  para manejar operaciones CRUD
 * relacionadas con la tabla 'clientes' en la base de datos.
 * Proporciona métodos para crear la tabla, guardar, obtener,
 * actualizar y borrar clientes.
 *
 * @author Edu
 * @version 1.0
 */
public class ClienteDAO {

    private Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos
     * y crea la tabla 'clientes' si no existe.
     */
    public ClienteDAO() {
        this.connection = DatabaseConnection.getConnection();
        crearTablaClientes();
    }

    /**
     * Crea la tabla 'clientes' en la base de datos si no existe.
     * La tabla tiene campos para id, dni, nombre, apellido, poblacion,
     * telefono y direccion.
     */
    public void crearTablaClientes() {
        String sql = "CREATE TABLE IF NOT EXISTS clientes ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " dni TEXT NOT NULL UNIQUE, "
                + " nombre TEXT NOT NULL, "
                + " apellido TEXT NOT NULL, "
                + " poblacion TEXT NOT NULL, "
                + " telefono TEXT NOT NULL, "
                + " direccion TEXT NOT NULL"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'clientes' creada o ya existe.");
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla 'clientes': " + e.getMessage());
        }
    }

    /**
     * Guarda un nuevo cliente en la base de datos.
     *
     * @param cliente Objeto Cliente que se desea guardar.
     * @return true si el cliente se guardó correctamente, false si hubo un error
     *         (por ejemplo, si el DNI ya existe).
     */
    public boolean guardarCliente(Cliente cliente) {
        String query = "INSERT INTO clientes (dni, nombre, apellido, poblacion, telefono, direccion) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cliente.getDni());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getApellido());
            stmt.setString(4, cliente.getPoblacion());
            stmt.setString(5, cliente.getTelefono());
            stmt.setString(6, cliente.getDireccion());

            int filasInsertadas = stmt.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: clientes.dni")) {
                System.out.println("Error: El DNI ya existe en la base de datos.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Obtiene una lista con todos los clientes almacenados en la base de datos.
     *
     * @return Lista de objetos Cliente.
     */
    public List<Cliente> obtenerTodosLosClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String poblacion = rs.getString("poblacion");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");

                clientes.add(new Cliente(dni, nombre, apellido, poblacion, telefono, direccion));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los clientes: " + e.getMessage());
        }

        return clientes;
    }

    /**
     * Obtiene un cliente específico buscando por su DNI.
     *
     * @param dni DNI del cliente a buscar.
     * @return Objeto Cliente si se encuentra, o null si no existe.
     */
    public Cliente obtenerClientePorDni(String dni) {
        String sql = "SELECT id, dni, nombre, apellido, poblacion, telefono, direccion FROM clientes WHERE dni = ?";
        Cliente cliente = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setDni(rs.getString("dni"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellido(rs.getString("apellido"));
                    cliente.setPoblacion(rs.getString("poblacion"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setDireccion(rs.getString("direccion"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cliente;
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente    Objeto Cliente con los nuevos datos a actualizar.
     * @param dniAntiguo DNI actual del cliente que se desea actualizar.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarCliente(Cliente cliente, String dniAntiguo) {
        String sqlActualizarCampos = "UPDATE clientes SET nombre = ?, apellido = ?, poblacion = ?, telefono = ?, direccion = ? WHERE dni = ?";
        String sqlActualizarDni = "UPDATE clientes SET dni = ? WHERE dni = ?";

        try (PreparedStatement stmtActualizarCampos = connection.prepareStatement(sqlActualizarCampos);
             PreparedStatement stmtActualizarDni = connection.prepareStatement(sqlActualizarDni)) {

            stmtActualizarCampos.setString(1, cliente.getNombre());
            stmtActualizarCampos.setString(2, cliente.getApellido());
            stmtActualizarCampos.setString(3, cliente.getPoblacion());
            stmtActualizarCampos.setString(4, cliente.getTelefono());
            stmtActualizarCampos.setString(5, cliente.getDireccion());
            stmtActualizarCampos.setString(6, dniAntiguo);

            int filasActualizadasCampos = stmtActualizarCampos.executeUpdate();

            // Si se actualizó al menos un campo y el DNI ha cambiado, actualiza el DNI
            if (filasActualizadasCampos > 0 && !dniAntiguo.equals(cliente.getDni())) {
                stmtActualizarDni.setString(1, cliente.getDni());
                stmtActualizarDni.setString(2, dniAntiguo);

                int filasActualizadasDni = stmtActualizarDni.executeUpdate();
                return filasActualizadasDni > 0;
            }

            return filasActualizadasCampos > 0;

        } catch (SQLException e) {
            System.err.println("Error en actualizarCliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un cliente de la base de datos buscando por su DNI.
     *
     * @param dniCliente DNI del cliente que se desea borrar.
     * @return true si el cliente fue eliminado, false si hubo error o no existía.
     */
    public boolean borrarCliente(String dniCliente) {
        String sql = "DELETE FROM clientes WHERE dni = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dniCliente);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al borrar el cliente: " + e.getMessage());
            return false;
        }
    }
}
