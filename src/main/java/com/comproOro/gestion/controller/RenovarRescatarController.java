package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.dao.ContratoDAO;
import com.comproOro.gestion.model.dao.ProductoDAO;
import com.comproOro.gestion.model.dao.RenovacionDAO;
import com.comproOro.gestion.model.modelos.Cliente;
import com.comproOro.gestion.model.modelos.Contrato;
import com.comproOro.gestion.model.modelos.Producto;
import com.comproOro.gestion.model.modelos.Renovacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controlador para manejar las acciones de renovación y rescate de contratos.
 */
public class RenovarRescatarController {

    @FXML private Label labelIdContrato;
    @FXML private Label labelFechaInicio;
    @FXML private Label labelFechaFinal;
    @FXML private Label labelDetalles;
    @FXML private Label lblMensaje;
    @FXML private Label labelNombreCliente;
    @FXML private Label labelImporteContrato;
    @FXML private TextField inputAportacion;
    @FXML private Label labelImporteRenovacion;

    private Contrato contrato;
    private Cliente cliente;
    private List<Contrato> contratos;
    private MainApp mainApp;

    private final ContratoDAO contratoDAO = new ContratoDAO();

    /**
     * Establece el contrato y cliente actual para mostrar sus datos.
     *
     * @param contrato El contrato a gestionar.
     * @param cliente  El cliente dueño del contrato.
     */
    @FXML
    public void setContrato(Contrato contrato, Cliente cliente) {
        this.contrato = contrato;
        this.cliente = cliente;

        labelIdContrato.setText("ID Contrato: " + contrato.getIdContrato());
        labelFechaInicio.setText("Fecha Inicio: " + contrato.getFechaInicio());

        RenovacionDAO renovacionDAO = new RenovacionDAO();
        Renovacion ultimaRenovacion = renovacionDAO.obtenerUltRenovPorIdContrato(contrato.getIdContrato());

        if (ultimaRenovacion != null) {
            labelImporteContrato.setText("Importe Última Renovación: " + ultimaRenovacion.getImporte());
            labelFechaFinal.setText("Fecha Final: " + ultimaRenovacion.getFechaFinRenovacion());
        } else {
            labelImporteContrato.setText("Importe: " + contrato.getImporte());
            labelFechaFinal.setText("Fecha Final: " + (contrato.getFechaFinal() != null ? contrato.getFechaFinal() : "N/A"));
        }

        labelDetalles.setText("Detalles: " + contrato.getDetallesContrato());
        labelNombreCliente.setText("Cliente: " + cliente.getNombre());
    }

    /**
     * Establece el cliente y la lista de contratos asociados.
     *
     * @param cliente   Cliente actual.
     * @param contratos Lista de contratos del cliente.
     */
    public void setClienteYContratos(Cliente cliente, List<Contrato> contratos) {
        this.cliente = cliente;
        this.contratos = contratos;
    }

    /**
     * Establece la instancia principal de la aplicación.
     *
     * @param mainApp Instancia de MainApp.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Maneja la acción de rescatar un contrato. Actualiza su estado y genera documento Word.
     */
    @FXML
    public void handleRescatarContrato() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas rescatar este contrato?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String idContrato = contrato.getIdContrato();

                if (idContrato != null && !idContrato.isEmpty()) {
                    Contrato contratoBD = contratoDAO.obtenerContratoPorId(idContrato);

                    if (contratoBD != null) {
                        if (!"S".equals(contratoBD.getRescatado())) {
                            contratoBD.setRescatado("S");
                            contratoBD.setFechaRescate(new Date());

                            if (contratoDAO.actualizarContrato(contratoBD)) {
                                lblMensaje.setText("Contrato rescatado exitosamente.");
                                generarDocumentosRESWord(contratoBD);
                            } else {
                                lblMensaje.setText("Error al rescatar el contrato.");
                            }
                        } else {
                            lblMensaje.setText("El contrato ya ha sido rescatado.");
                        }
                    } else {
                        lblMensaje.setText("No se encontró el contrato con ID: " + idContrato);
                    }
                } else {
                    lblMensaje.setText("ID de contrato no válido.");
                }
            }
        });
    }

    /**
     * Genera el documento Word al rescatar un contrato.
     *
     * @param contrato Contrato a procesar.
     */
    private void generarDocumentosRESWord(Contrato contrato) {
        String dniCliente = cliente.getDni();
        ProductoDAO productoDAO = new ProductoDAO();
        ArrayList<Producto> productos = productoDAO.obtenerProductosPorContrato(contrato.getIdContrato());

        WordGeneratorController wordGenerator = new WordGeneratorController();

        try {
            wordGenerator.rescatarContrato(dniCliente, contrato.getIdContrato(), productos);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al generar el documento Word: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de renovar un contrato, actualizando productos y generando renovación.
     */
    @FXML
    public void handleRenovarContrato() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas renovar este contrato?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String idContrato = contrato.getIdContrato();

                if (idContrato != null && !idContrato.isEmpty()) {
                    Contrato contratoBD = contratoDAO.obtenerContratoPorId(idContrato);

                    if (contratoBD != null) {
                        if (!"S".equals(contratoBD.getRescatado())) {
                            Date fechaActual = new Date();
                            RenovacionDAO renovacionDAO = new RenovacionDAO();
                            Renovacion ultimaRenovacion = renovacionDAO.obtenerUltRenovPorIdContrato(idContrato);

                            Date fechaReferencia = (ultimaRenovacion != null)
                                    ? Date.from(ultimaRenovacion.getFechaFinRenovacion().atStartOfDay(ZoneId.systemDefault()).toInstant())
                                    : contratoBD.getFechaFinal();

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(fechaReferencia);
                            calendar.add(Calendar.MONTH, 1);
                            Date nuevaFechaFin = calendar.getTime();

                            try {
                                double aportacion = inputAportacion.getText().isEmpty()
                                        ? 0
                                        : Double.parseDouble(inputAportacion.getText());

                                double importeRenov = (ultimaRenovacion != null)
                                        ? ultimaRenovacion.getImporte()
                                        : contrato.getImporte();

                                if (aportacion > importeRenov) {
                                    lblMensaje.setText("La aportación no puede ser mayor que el importe total de la última renovación.");
                                    return;
                                }

                                double nuevoImporteRenov = importeRenov - aportacion;
                                labelImporteRenovacion.setText("Importe de la Renovación: " + nuevoImporteRenov);

                                ProductoDAO productoDAO = new ProductoDAO();
                                ArrayList<Producto> productos = productoDAO.obtenerProductosPorContrato(idContrato);

                                if (productos != null && !productos.isEmpty()) {
                                    productos.sort((p1, p2) -> Double.compare(p2.getImporte(), p1.getImporte()));
                                    double aporteRestante = aportacion;

                                    for (Producto producto : productos) {
                                        if (aporteRestante <= 0) break;
                                        double importeProd = producto.getImporte();

                                        if (importeProd <= aporteRestante) {
                                            aporteRestante -= importeProd;
                                            producto.setImporte(0);
                                        } else {
                                            producto.setImporte(importeProd - aporteRestante);
                                            aporteRestante = 0;
                                        }

                                        if (!productoDAO.actualizarProducto(producto)) {
                                            lblMensaje.setText("Error al actualizar el producto: " + producto.getIdProducto());
                                            return;
                                        }
                                    }
                                }

                                guardarRenovacion(idContrato, fechaActual, nuevaFechaFin, nuevoImporteRenov);
                                lblMensaje.setText("Renovación guardada exitosamente.");
                                generarDocumentosRENWord(contratoBD);
                            } catch (NumberFormatException e) {
                                lblMensaje.setText("Por favor, ingrese un número válido para la aportación.");
                            }
                        } else {
                            lblMensaje.setText("El contrato ya ha sido rescatado.");
                        }
                    } else {
                        lblMensaje.setText("No se encontró el contrato con ID: " + idContrato);
                    }
                } else {
                    lblMensaje.setText("ID de contrato no válido.");
                }
            }
        });
    }

    /**
     * Genera el documento Word al renovar un contrato.
     *
     * @param contrato Contrato a renovar.
     */
    private void generarDocumentosRENWord(Contrato contrato) {
        String dniCliente = cliente.getDni();
        ProductoDAO productoDAO = new ProductoDAO();
        ArrayList<Producto> productos = productoDAO.obtenerProductosPorContrato(contrato.getIdContrato());

        WordGeneratorController wordGenerator = new WordGeneratorController();

        try {
            wordGenerator.renovarContrato(dniCliente, contrato.getIdContrato(), productos);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al generar el documento Word: " + e.getMessage());
        }
    }

    /**
     * Vuelve a la vista anterior (selección de contratos).
     *
     * @param actionEvent Evento de acción del botón cancelar.
     */
    @FXML
    public void handleCancelar(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/selecionar_contrato.fxml"));
            Parent root = loader.load();

            SeleccionarContratoController seleccionarContratoController = loader.getController();

            if (seleccionarContratoController != null) {
                seleccionarContratoController.setMainApp(this.mainApp);
                if (cliente != null && contratos != null) {
                    seleccionarContratoController.setDatosClienteYContratos(cliente, contratos);
                }
            }

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Renovar/Rescatar Contrato");
            stage.setMaximized(true);
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al cargar la vista anterior: " + e.getMessage());
        }
    }

    /**
     * Guarda una nueva renovación del contrato en la base de datos.
     *
     * @param idContrato          ID del contrato.
     * @param fechaRenovacion     Fecha en que se realiza la renovación.
     * @param fechaFinRenovacion  Nueva fecha de fin de la renovación.
     * @param importeRenovacion   Importe final de la renovación.
     */
    private void guardarRenovacion(String idContrato, Date fechaRenovacion, Date fechaFinRenovacion, double importeRenovacion) {
        RenovacionDAO renovacionDAO = new RenovacionDAO();
        int nuevaVersion = renovacionDAO.obtenerUltimaVersionPorIdContrato(idContrato) + 1;

        Renovacion renovacion = new Renovacion();
        renovacion.setIdContrato(idContrato);
        renovacion.setFechaRenovacion(fechaRenovacion.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        renovacion.setFechaFinRenovacion(fechaFinRenovacion.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        renovacion.setVersion(nuevaVersion);
        renovacion.setImporte(importeRenovacion);

        if (renovacionDAO.guardarRenovacion(renovacion)) {
            lblMensaje.setText("Renovación guardada exitosamente.");
        } else {
            lblMensaje.setText("Error al guardar la renovación.");
        }
    }
}
