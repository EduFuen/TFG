package com.comproOro.gestion.model.modelos;

/**
 * Representa un producto asociado a un contrato, con detalles como cantidad, peso, precio y observaciones.
 */
public class Producto {

    /** Identificador único del producto. */
    private int idProducto;

    /** Cantidad de unidades del producto. */
    private int cantidad;

    /** Descripción del producto. */
    private String descripcion;

    /** Observaciones adicionales sobre el producto. */
    private String observaciones;

    /** Peso del producto en gramos. */
    private double peso;

    /** Precio por gramo del producto. */
    private double precioGramo;

    /** Importe total calculado del producto. */
    private double importe;

    /** Identificador del contrato al que pertenece el producto. */
    private String idContrato;

    /**
     * Constructor vacío de Producto.
     */
    public Producto() {
    }

    /**
     * Constructor con todos los atributos del producto.
     *
     * @param idProducto    identificador único del producto
     * @param cantidad      cantidad de unidades
     * @param descripcion   descripción del producto
     * @param observaciones observaciones adicionales
     * @param peso          peso del producto en gramos
     * @param precioGramo   precio por gramo
     * @param importe       importe total
     * @param idContrato    identificador del contrato asociado
     */
    public Producto(int idProducto, int cantidad, String descripcion, String observaciones, double peso, double precioGramo, double importe, String idContrato) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.observaciones = observaciones;
        this.peso = peso;
        this.precioGramo = precioGramo;
        this.importe = importe;
        this.idContrato = idContrato;
    }

    /**
     * Obtiene el identificador del producto.
     * @return el id del producto
     */
    public int getIdProducto() {
        return idProducto;
    }

    /**
     * Establece el identificador del producto.
     * @param idProducto el id a establecer
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Obtiene la cantidad del producto.
     * @return la cantidad
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del producto.
     * @param cantidad la cantidad a establecer
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene la descripción del producto.
     * @return la descripción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del producto.
     * @param descripcion la descripción a establecer
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene las observaciones del producto.
     * @return las observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones del producto.
     * @param observaciones las observaciones a establecer
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Obtiene el peso del producto en gramos.
     * @return el peso
     */
    public double getPeso() {
        return peso;
    }

    /**
     * Establece el peso del producto en gramos.
     * @param peso el peso a establecer
     */
    public void setPeso(double peso) {
        this.peso = peso;
    }

    /**
     * Obtiene el precio por gramo del producto.
     * @return el precio por gramo
     */
    public double getPrecioGramo() {
        return precioGramo;
    }

    /**
     * Establece el precio por gramo del producto.
     * @param precioGramo el precio por gramo a establecer
     */
    public void setPrecioGramo(double precioGramo) {
        this.precioGramo = precioGramo;
    }

    /**
     * Obtiene el importe total del producto.
     * @return el importe
     */
    public double getImporte() {
        return importe;
    }

    /**
     * Establece el importe total del producto.
     * @param importe el importe a establecer
     */
    public void setImporte(double importe) {
        this.importe = importe;
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

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                ", descripcion='" + descripcion + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", peso=" + peso +
                ", precioGramo=" + precioGramo +
                ", importe=" + importe +
                ", idContrato=" + idContrato +
                '}';
    }
}
