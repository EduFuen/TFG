package com.comproOro.gestion.model.dao;

import com.comproOro.gestion.model.modelos.Cliente;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ClienteDAOTest {

    private ClienteDAO clienteDAO;

    @Before
    public void setUp() {
        clienteDAO = new ClienteDAO();
        clienteDAO.crearTablaClientes();
    }

    @Test
    public void testCrearTablaClientes() {
        try {
            clienteDAO.crearTablaClientes();
            System.out.println("Prueba de creación de tabla completada con éxito.");
        } catch (Exception e) {
            fail("Excepción en testCrearTablaClientes: " + e.getMessage());
        }
    }

    @Test
    public void testGuardarCliente() {
        Cliente cliente = new Cliente("12345678A", "Juan", "Pérez", "Madrid", "123456789", "Calle Falsa 123");
        boolean resultado = clienteDAO.guardarCliente(cliente);
        assertTrue("El cliente debería guardarse correctamente", resultado);
    }

    @Test
    public void testObtenerTodosLosClientes() {
        List<Cliente> clientes = clienteDAO.obtenerTodosLosClientes();
        assertNotNull("La lista de clientes no debería ser nula", clientes);
        assertTrue("Debería haber al menos un cliente en la base de datos", clientes.size() > 0);
    }

    @Test
    public void testObtenerClientePorDni() {
        Cliente cliente = new Cliente("87654321B", "Ana", "García", "Barcelona", "987654321", "Avenida Siempreviva 742");
        clienteDAO.guardarCliente(cliente);

        Cliente clienteRecuperado = clienteDAO.obtenerClientePorDni("87654321B");
        assertNotNull("El cliente recuperado no debería ser nulo", clienteRecuperado);
        assertEquals("El DNI del cliente recuperado debería coincidir", "87654321B", clienteRecuperado.getDni());
    }

    @Test
    public void testActualizarCliente() {
        Cliente cliente = new Cliente("11223344C", "Carlos", "Lopez", "Valencia", "555555555", "Calle Sol 101");
        clienteDAO.guardarCliente(cliente);

        cliente.setNombre("Carlos Modificado");
        boolean actualizado = clienteDAO.actualizarCliente(cliente, "11223344C");
        assertTrue("El cliente debería actualizarse correctamente", actualizado);

        Cliente clienteActualizado = clienteDAO.obtenerClientePorDni("11223344C");
        assertEquals("El nombre del cliente debería actualizarse", "Carlos Modificado", clienteActualizado.getNombre());
    }

    @Test
    public void testBorrarCliente() {
        Cliente cliente = new Cliente("22334455D", "María", "Jiménez", "Sevilla", "444444444", "Plaza Mayor 5");
        clienteDAO.guardarCliente(cliente);

        boolean borrado = clienteDAO.borrarCliente("22334455D");
        assertTrue("El cliente debería eliminarse correctamente", borrado);

        Cliente clienteBorrado = clienteDAO.obtenerClientePorDni("22334455D");
        assertNull("El cliente borrado no debería existir en la base de datos", clienteBorrado);
    }
}
