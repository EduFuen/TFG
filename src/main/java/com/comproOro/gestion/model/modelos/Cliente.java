package com.comproOro.gestion.model.modelos;

/**
 * Representa un cliente con información personal y de contacto.
 */
public class Cliente {

    /** DNI del cliente (Documento Nacional de Identidad). */
    private String dni;

    /** Nombre del cliente. */
    private String nombre;

    /** Apellido del cliente. */
    private String apellido;

    /** Población o localidad del cliente. */
    private String poblacion;

    /** Número de teléfono del cliente. */
    private String telefono;

    /** Dirección del cliente. */
    private String direccion;

    /**
     * Crea un nuevo cliente con todos sus datos especificados.
     *
     * @param dni       el DNI del cliente
     * @param nombre    el nombre del cliente
     * @param apellido  el apellido del cliente
     * @param poblacion la población del cliente
     * @param telefono  el número de teléfono del cliente
     * @param direccion la dirección del cliente
     */
    public Cliente(String dni, String nombre, String apellido, String poblacion, String telefono, String direccion) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.poblacion = poblacion;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    /**
     * Crea un cliente vacío. Se deben establecer los valores usando los métodos {@code set}.
     */
    public Cliente() {
    }

    /**
     * Obtiene el DNI del cliente.
     *
     * @return el DNI
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del cliente.
     *
     * @param dni el nuevo DNI
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene la población del cliente.
     *
     * @return la población
     */
    public String getPoblacion() {
        return poblacion;
    }

    /**
     * Establece la población del cliente.
     *
     * @param poblacion la nueva población
     */
    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     *
     * @param nombre el nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del cliente.
     *
     * @return el apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del cliente.
     *
     * @param apellido el nuevo apellido
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el número de teléfono del cliente.
     *
     * @return el teléfono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del cliente.
     *
     * @param telefono el nuevo teléfono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene la dirección del cliente.
     *
     * @return la dirección
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección del cliente.
     *
     * @param direccion la nueva dirección
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
