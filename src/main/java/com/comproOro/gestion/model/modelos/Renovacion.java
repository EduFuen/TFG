package com.comproOro.gestion.model.modelos;

import java.time.LocalDate;

/**
 * Representa una renovación de un contrato, con fechas, versión e importe asociados.
 */
public class Renovacion {
    /** Identificador único de la renovación. */
    private int idRenovacion;

    /** Identificador del contrato al que pertenece la renovación. */
    private String idContrato;

    /** Fecha en la que se realizó la renovación. */
    private LocalDate fechaRenovacion;

    /** Fecha en la que finaliza la renovación. */
    private LocalDate fechaFinRenovacion;

    /** Versión de la renovación. */
    private int version;

    /** Importe asociado a la renovación. */
    private double importe;

    /**
     * Constructor con todos los atributos de la renovación.
     *
     * @param idRenovacion      identificador de la renovación
     * @param idContrato        identificador del contrato asociado
     * @param fechaRenovacion   fecha de la renovación
     * @param fechaFinRenovacion fecha de fin de la renovación
     * @param version           versión de la renovación
     * @param importe           importe asociado
     */
    public Renovacion(int idRenovacion, String idContrato, LocalDate fechaRenovacion, LocalDate fechaFinRenovacion, int version, double importe) {
        this.idRenovacion = idRenovacion;
        this.idContrato = idContrato;
        this.fechaRenovacion = fechaRenovacion;
        this.fechaFinRenovacion = fechaFinRenovacion;
        this.version = version;
        this.importe = importe;
    }

    /**
     * Constructor vacío de Renovacion.
     */
    public Renovacion() {
    }

    /**
     * Obtiene el identificador de la renovación.
     * @return el id de la renovación
     */
    public int getIdRenovacion() {
        return idRenovacion;
    }

    /**
     * Establece el identificador de la renovación.
     * @param idRenovacion el id a establecer
     */
    public void setIdRenovacion(int idRenovacion) {
        this.idRenovacion = idRenovacion;
    }

    /**
     * Obtiene el identificador del contrato asociado.
     * @return el id del contrato
     */
    public String getIdContrato() {
        return idContrato;
    }

    /**
     * Establece el identificador del contrato asociado.
     * @param idContrato el id del contrato a establecer
     */
    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    /**
     * Obtiene la fecha de la renovación.
     * @return la fecha de renovación
     */
    public LocalDate getFechaRenovacion() {
        return fechaRenovacion;
    }

    /**
     * Establece la fecha de la renovación.
     * @param fechaRenovacion la fecha a establecer
     */
    public void setFechaRenovacion(LocalDate fechaRenovacion) {
        this.fechaRenovacion = fechaRenovacion;
    }

    /**
     * Obtiene la fecha de fin de la renovación.
     * @return la fecha de fin de renovación
     */
    public LocalDate getFechaFinRenovacion() {
        return fechaFinRenovacion;
    }

    /**
     * Establece la fecha de fin de la renovación.
     * @param fechaFinRenovacion la fecha a establecer
     */
    public void setFechaFinRenovacion(LocalDate fechaFinRenovacion) {
        this.fechaFinRenovacion = fechaFinRenovacion;
    }

    /**
     * Obtiene la versión de la renovación.
     * @return la versión
     */
    public int getVersion() {
        return version;
    }

    /**
     * Establece la versión de la renovación.
     * @param version la versión a establecer
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Obtiene el importe de la renovación.
     * @return el importe
     */
    public double getImporte() {
        return importe;
    }

    /**
     * Establece el importe de la renovación.
     * @param importe el importe a establecer
     */
    public void setImporte(double importe) {
        this.importe = importe;
    }

    @Override
    public String toString() {
        return "Renovacion{" +
                "idRenovacion=" + idRenovacion +
                ", idContrato='" + idContrato + '\'' +
                ", fechaRenovacion=" + fechaRenovacion +
                ", fechaFinRenovacion=" + fechaFinRenovacion +
                ", version=" + version +
                ", importe=" + importe +
                '}';
    }
}
