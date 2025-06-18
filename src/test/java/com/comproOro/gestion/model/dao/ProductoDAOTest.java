package com.comproOro.gestion.model.dao;

import com.comproOro.gestion.model.modelos.Producto;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ProductoDAOTest {

    private ProductoDAO productoDAO;
    private Producto producto;

    @Before
    public void setUp() {
        productoDAO = new ProductoDAO();
        producto = new Producto();

        producto.setCantidad(10);
        producto.setDescripcion("Producto de prueba");
        producto.setObservaciones("Ninguna");
        producto.setPeso(5.0);
        producto.setPrecioGramo(2.0);
        producto.setImporte(10.0);
        producto.setIdContrato("contrato_123");
    }

    @Test
    public void testGuardarProducto() {

        boolean result = productoDAO.guardarProducto(producto);
        assertTrue("El producto debería guardarse correctamente", result);


        Producto productoGuardado = productoDAO.obtenerProductoPorId(producto.getIdProducto());
        assertNotNull("El producto guardado no debe ser nulo", productoGuardado);
        assertEquals("La descripción del producto guardado no es la misma", producto.getDescripcion(), productoGuardado.getDescripcion());
    }

    @Test
    public void testObtenerProductoPorId() {

        productoDAO.guardarProducto(producto);


        Producto productoObtenido = productoDAO.obtenerProductoPorId(producto.getIdProducto());
        assertNotNull("El producto debería ser encontrado por su ID", productoObtenido);
        assertEquals("La descripción del producto no es correcta", producto.getDescripcion(), productoObtenido.getDescripcion());
    }

    @Test
    public void testObtenerProductosPorContrato() {

        productoDAO.guardarProducto(producto);


        List<Producto> productos = productoDAO.obtenerProductosPorContrato(producto.getIdContrato());
        assertNotNull("La lista de productos no debe ser nula", productos);
        assertTrue("Debería haber al menos un producto asociado al contrato", productos.size() > 0);
    }

    @Test
    public void testActualizarProducto() {

        productoDAO.guardarProducto(producto);


        producto.setDescripcion("Producto actualizado");
        producto.setCantidad(20);
        boolean result = productoDAO.actualizarProducto(producto);
        assertTrue("El producto debería actualizarse correctamente", result);


        Producto productoActualizado = productoDAO.obtenerProductoPorId(producto.getIdProducto());
        assertNotNull("El producto actualizado no debe ser nulo", productoActualizado);
        assertEquals("La descripción del producto actualizado no es la misma", "Producto actualizado", productoActualizado.getDescripcion());
        assertEquals("La cantidad del producto actualizado no es la misma", 20, productoActualizado.getCantidad());
    }
}
