package com.comproOro.gestion.model.modelos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Representa un contrato de empeño o compra, incluyendo información sobre el cliente,
 * fechas, productos asociados y estado de rescate.
 */
public class Contrato {

    /** Identificador único del contrato (ej. {@code E-20250001}). */
    private String idContrato;

    /** Identificador de pól asociado al contrato (ej. {@code P-20250001}). */
    private String idPol;

    /** DNI del cliente asociado al contrato. */
    private String dniCliente;

    /** Detalles adicionales del contrato. */
    private String detallesContrato;

    /** Fecha de inicio del contrato. */
    private Date fechaInicio;

    /** Fecha final del contrato. */
    private Date fechaFinal;

    /** Tipo de contrato: {@code "Empeno"} o {@code "Compra"}. */
    private String tipo;

    /** Lista de productos asociados al contrato. */
    private List<Producto> productos;

    /** Indica si el contrato ha sido rescatado ({@code "S"} o {@code "N"}). */
    private String rescatado;

    /** Fecha en la que se rescató el contrato, si aplica. */
    private Date fechaRescate;

    /** Importe total del contrato. */
    private double importe;

    /**
     * Crea una nueva instancia de {@code Contrato} con valores por defecto.
     * La lista de productos está vacía, el estado de rescate es {@code "N"},
     * y el importe es {@code 0.0}.
     */
    public Contrato() {
        this.productos = new ArrayList<>();
        this.rescatado = "N";
        this.fechaRescate = null;
        this.importe = 0.0;
    }

    /**
     * Obtiene el identificador del contrato.
     * @return el ID del contrato
     */
    public String getIdContrato() {
        return idContrato;
    }

    /**
     * Establece el identificador del contrato.
     * @param idContrato el ID del contrato a establecer
     */
    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    /**
     * Obtiene el identificador de la póliza.
     * @return el ID de la póliza
     */
    public String getIdPol() {
        return idPol;
    }

    /**
     * Establece el identificador de la póliza.
     * @param idPol el ID de la póliza a establecer
     */
    public void setIdPol(String idPol) {
        this.idPol = idPol;
    }

    /**
     * Obtiene el DNI del cliente asociado.
     * @return el DNI del cliente
     */
    public String getDniCliente() {
        return dniCliente;
    }

    /**
     * Establece el DNI del cliente.
     * @param dniCliente el DNI a establecer
     */
    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    /**
     * Obtiene los detalles del contrato.
     * @return los detalles del contrato
     */
    public String getDetallesContrato() {
        return detallesContrato;
    }

    /**
     * Establece los detalles del contrato.
     * @param detallesContrato los detalles a establecer
     */
    public void setDetallesContrato(String detallesContrato) {
        this.detallesContrato = detallesContrato;
    }

    /**
     * Obtiene la fecha de inicio del contrato.
     * @return la fecha de inicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de inicio del contrato.
     * @param fechaInicio la fecha de inicio a establecer
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha final del contrato.
     * @return la fecha final
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Establece la fecha final del contrato.
     * @param fechaFinal la fecha final a establecer
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Obtiene el tipo de contrato.
     * @return el tipo de contrato (ej. {@code "Empeno"}, {@code "Compra"})
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de contrato.
     * @param tipo el tipo de contrato a establecer
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la lista de productos del contrato.
     * @return una lista no modificable de productos
     */
    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    /**
     * Establece la lista de productos del contrato.
     * @param productos lista de productos a asociar
     */
    public void setProductos(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
    }

    /**
     * Agrega un producto al contrato.
     * @param producto el producto a agregar
     */
    public void addProducto(Producto producto) {
        this.productos.add(producto);
    }

    /**
     * Elimina un producto del contrato.
     * @param producto el producto a eliminar
     */
    public void removeProducto(Producto producto) {
        this.productos.remove(producto);
    }

    /**
     * Obtiene el estado de rescate del contrato.
     * @return {@code "S"} si ha sido rescatado, {@code "N"} si no
     */
    public String getRescatado() {
        return rescatado;
    }

    /**
     * Establece el estado de rescate del contrato.
     * @param rescatado el valor a establecer ({@code "S"} o {@code "N"})
     */
    public void setRescatado(String rescatado) {
        this.rescatado = rescatado;
    }

    /**
     * Obtiene la fecha de rescate del contrato.
     * @return la fecha de rescate
     */
    public Date getFechaRescate() {
        return fechaRescate;
    }

    /**
     * Establece la fecha de rescate del contrato.
     * @param fechaRescate la fecha a establecer
     */
    public void setFechaRescate(Date fechaRescate) {
        this.fechaRescate = fechaRescate;
    }

    /**
     * Obtiene el importe total del contrato.
     * @return el importe
     */
    public double getImporte() {
        return importe;
    }

    /**
     * Establece el importe total del contrato.
     * @param importe el importe a establecer
     */
    public void setImporte(double importe) {
        this.importe = importe;
    }


    /**
     * Genera un identificador de contrato único con base en el año actual y el número proporcionado.
     * El formato es {@code E-yyyyNNNN} para empeños o {@code C-yyyyNNNN} para compras.
     *
     * @param numeroContrato el número consecutivo del contrato
     */
    public void generarIdContrato(int numeroContrato) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String ano = sdf.format(new Date());
        String formatoNumero = String.format("%04d", numeroContrato);
        this.idContrato = (tipo.equals("Empeno") ? "E-" : "C-") + ano + formatoNumero;
    }

    /**
     * Genera un identificador de póliza único con base en el año actual y el número proporcionado.
     * El formato es {@code P-yyyyNNNN}.
     *
     * @param numeroPol el número consecutivo de la póliza
     */
    public void generarIdPol(int numeroPol) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String ano = sdf.format(new Date());
        String formatoNumero = String.format("%04d", numeroPol);
        this.idPol = "P-" + ano + formatoNumero;
    }
}
