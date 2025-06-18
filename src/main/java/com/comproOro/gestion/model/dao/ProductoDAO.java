package com.comproOro.gestion.model.dao;

import com.comproOro.gestion.model.modelos.Producto;
import java.sql.*;
import java.util.ArrayList;

/**
 * Clase DAO (Data Access Object) para realizar operaciones CRUD sobre productos
 * en la base de datos. Incluye métodos para crear la tabla, insertar, actualizar
 * y consultar productos asociados a contratos.
 */
public class ProductoDAO {

    /**
     * Constructor de la clase. Al instanciar el DAO, se asegura de que la tabla
     * de productos exista en la base de datos.
     */
    public ProductoDAO() {
        crearTablaProductos();
    }

    /**
     * Crea la tabla 'productos' en la base de datos si no existe.
     * La tabla contiene campos relacionados con los productos asociados
     * a un contrato.
     */
    private void crearTablaProductos() {
        String sql = "CREATE TABLE IF NOT EXISTS productos (" +
                " idProducto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " cantidad INTEGER NOT NULL, " +
                " descripcion TEXT NOT NULL, " +
                " observaciones TEXT, " +
                " peso REAL NOT NULL, " +
                " precioGramo REAL NOT NULL, " +
                " importe REAL NOT NULL, " +
                " idContrato TEXT NOT NULL, " +
                " FOREIGN KEY(idContrato) REFERENCES contratos(idContrato)" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'productos' creada o ya existe.");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla productos: " + e.getMessage());
        }
    }

    /**
     * Guarda un nuevo producto en la base de datos.
     *
     * @param producto el objeto {@link Producto} que se desea guardar.
     * @return <code>true</code> si el producto fue guardado correctamente,
     *         <code>false</code> en caso contrario.
     */
    public boolean guardarProducto(Producto producto) {
        String sql = "INSERT INTO productos (cantidad, descripcion, observaciones, peso, precioGramo, importe, idContrato) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, producto.getCantidad());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setString(3, producto.getObservaciones());
            pstmt.setDouble(4, producto.getPeso());
            pstmt.setDouble(5, producto.getPrecioGramo());
            pstmt.setDouble(6, producto.getImporte());
            pstmt.setString(7, producto.getIdContrato());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    producto.setIdProducto(generatedKeys.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar el producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un producto desde la base de datos usando su identificador.
     *
     * @param id el ID del producto.
     * @return el objeto {@link Producto} correspondiente al ID o <code>null</code> si no se encuentra.
     */
    public Producto obtenerProductoPorId(int id) {
        Producto producto = null;
        String sql = "SELECT * FROM productos WHERE idProducto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto();
                    producto.setIdProducto(rs.getInt("idProducto"));
                    producto.setCantidad(rs.getInt("cantidad"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setObservaciones(rs.getString("observaciones"));
                    producto.setPeso(rs.getDouble("peso"));
                    producto.setPrecioGramo(rs.getDouble("precioGramo"));
                    producto.setImporte(rs.getDouble("importe"));
                    producto.setIdContrato(rs.getString("idContrato"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el producto por ID: " + e.getMessage());
        }

        return producto;
    }

    /**
     * Obtiene una lista de productos asociados a un contrato específico.
     *
     * @param idContrato el ID del contrato.
     * @return una lista de objetos {@link Producto} asociados al contrato.
     */
    public ArrayList<Producto> obtenerProductosPorContrato(String idContrato) {
        ArrayList<Producto> productos = new ArrayList<>();

        String query = "SELECT idProducto, cantidad, descripcion, observaciones, peso, precioGramo, importe, idContrato "
                + "FROM productos WHERE idContrato = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, idContrato);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();

                producto.setIdProducto(rs.getInt("idProducto"));
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setObservaciones(rs.getString("observaciones"));
                producto.setPeso(rs.getDouble("peso"));
                producto.setPrecioGramo(rs.getDouble("precioGramo"));
                producto.setImporte(rs.getDouble("importe"));
                producto.setIdContrato(rs.getString("idContrato"));

                productos.add(producto);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener los productos para el contrato con ID: " + idContrato);
        }

        return productos;
    }

    /**
     * Actualiza un producto existente en la base de datos.
     *
     * @param producto el objeto {@link Producto} con los nuevos valores.
     * @return <code>true</code> si la actualización fue exitosa,
     *         <code>false</code> en caso contrario.
     */
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET cantidad = ?, descripcion = ?, observaciones = ?, peso = ?, precioGramo = ?, importe = ?, idContrato = ? WHERE idProducto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, producto.getCantidad());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setString(3, producto.getObservaciones());
            pstmt.setDouble(4, producto.getPeso());
            pstmt.setDouble(5, producto.getPrecioGramo());
            pstmt.setDouble(6, producto.getImporte());
            pstmt.setString(7, producto.getIdContrato());
            pstmt.setInt(8, producto.getIdProducto());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
            return false;
        }
    }
}
