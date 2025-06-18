package com.comproOro.gestion.model.dao;

import com.comproOro.gestion.model.modelos.Renovacion;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class RenovacionDAOTest {

    private RenovacionDAO renovacionDAO;
    private Renovacion renovacion;

    @Before
    public void setUp() {

        renovacionDAO = new RenovacionDAO();
        renovacion = new Renovacion();
        renovacion.setIdContrato("contrato_123");
        renovacion.setFechaRenovacion(LocalDate.of(2024, 1, 1));
        renovacion.setFechaFinRenovacion(LocalDate.of(2025, 1, 1));
        renovacion.setVersion(1);
        renovacion.setImporte(1000.0);
    }



    @Test
    public void obtenerTodasLasRenovaciones() {

        renovacionDAO.guardarRenovacion(renovacion);
        List<Renovacion> renovaciones = renovacionDAO.obtenerTodasLasRenovaciones();
        assertNotNull("La lista de renovaciones no debe ser nula", renovaciones);
        assertTrue("Debe haber al menos una renovación", renovaciones.size() > 0);
    }






    @Test
    public void close() {

        renovacionDAO.close();

    }

    @Test
    public void obtenerUltimaVersionPorIdContrato() {

        renovacionDAO.guardarRenovacion(renovacion);
        Renovacion renovacion2 = new Renovacion();
        renovacion2.setIdContrato("contrato_123");
        renovacion2.setFechaRenovacion(LocalDate.of(2024, 2, 1));
        renovacion2.setFechaFinRenovacion(LocalDate.of(2025, 2, 1));
        renovacion2.setVersion(2);
        renovacion2.setImporte(1100.0);
        renovacionDAO.guardarRenovacion(renovacion2);


        int ultimaVersion = renovacionDAO.obtenerUltimaVersionPorIdContrato("contrato_123");
        assertEquals("La última versión debería ser 2", 2, ultimaVersion);
    }



    @Test
    public void obtenerUltRenovPorIdContrato() {

        renovacionDAO.guardarRenovacion(renovacion);
        Renovacion renovacion2 = new Renovacion();
        renovacion2.setIdContrato("contrato_123");
        renovacion2.setFechaRenovacion(LocalDate.of(2024, 2, 1));
        renovacion2.setFechaFinRenovacion(LocalDate.of(2025, 2, 1));
        renovacion2.setVersion(2);
        renovacion2.setImporte(1100.0);
        renovacionDAO.guardarRenovacion(renovacion2);


        Renovacion ultimaRenovacion = renovacionDAO.obtenerUltRenovPorIdContrato("contrato_123");
        assertNotNull("La última renovación no debe ser nula", ultimaRenovacion);
        assertEquals("La versión de la última renovación no es correcta", 2, ultimaRenovacion.getVersion());
    }
}
