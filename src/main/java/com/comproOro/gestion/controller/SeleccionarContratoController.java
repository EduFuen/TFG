package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.modelos.Cliente;
import com.comproOro.gestion.model.modelos.Contrato;
import com.comproOro.gestion.model.modelos.Renovacion;
import com.comproOro.gestion.model.dao.RenovacionDAO;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de selección de contratos.
 * Permite al usuario visualizar los contratos asociados a un cliente
 * y seleccionar uno para renovarlo.
 */
public class SeleccionarContratoController {

    @FXML
    private Label labelDni;

    @FXML
    private Label labelNombre;

    @FXML
    private Label labelApellido;

    @FXML
    private TableView<Contrato> tablaContratos;

    @FXML
    private TableColumn<Contrato, String> colIdContrato;

    @FXML
    private TableColumn<Contrato, String> colFechaInicio;

    @FXML
    private TableColumn<Contrato, Date> colFechaFinal;

    @FXML
    private TableColumn<Contrato, String> colDetalles;

    @FXML
    private TableColumn<Contrato, Double> colImporte;

    private MainApp mainApp;
    private Cliente cliente;

    /**
     * Inicializa la tabla de contratos con columnas y celdas personalizadas.
     */
    @FXML
    public void initialize() {
        colIdContrato.setCellValueFactory(new PropertyValueFactory<>("idContrato"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFinal.setCellValueFactory(new PropertyValueFactory<>("fechaFinal"));
        colDetalles.setCellValueFactory(new PropertyValueFactory<>("detallesContrato"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));

        colFechaFinal.setCellFactory(column -> new TableCell<Contrato, Date>() {
            @Override
            protected void updateItem(Date fechaFinal, boolean empty) {
                super.updateItem(fechaFinal, empty);
                if (empty || fechaFinal == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(fechaFinal.toString());
                    LocalDate fechaActual = LocalDate.now();
                    if (fechaFinal.toLocalDate().isBefore(fechaActual)) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });

        colImporte.setCellFactory(column -> new TableCell<Contrato, Double>() {
            @Override
            protected void updateItem(Double importe, boolean empty) {
                super.updateItem(importe, empty);
                if (empty || importe == null) {
                    setText(null);
                } else {
                    Contrato contrato = getTableView().getItems().get(getIndex());
                    RenovacionDAO renovacionDAO = new RenovacionDAO();
                    Renovacion ultimaRenovacion = renovacionDAO.obtenerUltRenovPorIdContrato(contrato.getIdContrato());

                    if (ultimaRenovacion != null) {
                        setText(String.valueOf(ultimaRenovacion.getImporte()));
                    } else {
                        setText(String.valueOf(importe));
                    }
                }
            }
        });

        colFechaFinal.setCellFactory(column -> new TableCell<Contrato, Date>() {
            @Override
            protected void updateItem(Date fechaFinal, boolean empty) {
                super.updateItem(fechaFinal, empty);
                if (empty || fechaFinal == null) {
                    setText(null);
                } else {
                    Contrato contrato = getTableView().getItems().get(getIndex());
                    RenovacionDAO renovacionDAO = new RenovacionDAO();
                    Renovacion ultimaRenovacion = renovacionDAO.obtenerUltRenovPorIdContrato(contrato.getIdContrato());

                    if (ultimaRenovacion != null) {
                        setText(ultimaRenovacion.getFechaFinRenovacion().toString());
                        setTextFill(Color.BLACK);
                        setStyle("");
                    } else {
                        setText(fechaFinal.toString());
                    }
                }
            }
        });
    }

    /**
     * Establece los datos del cliente y sus contratos en la vista.
     *
     * @param cliente           Cliente seleccionado.
     * @param contratosEmpeno   Lista de contratos asociados al cliente.
     */
    public void setDatosClienteYContratos(Cliente cliente, List<Contrato> contratosEmpeno) {
        this.cliente = cliente;

        if (cliente != null) {
            labelDni.setText(cliente.getDni());
            labelNombre.setText(cliente.getNombre());
            labelApellido.setText(cliente.getApellido());
        } else {
            labelDni.setText("N/A");
            labelNombre.setText("N/A");
            labelApellido.setText("N/A");
        }

        List<Contrato> contratosNoRescatados = contratosEmpeno.stream()
                .filter(contrato -> !Objects.equals(contrato.getRescatado(), "S"))
                .collect(Collectors.toList());

        tablaContratos.getItems().setAll(contratosNoRescatados);
    }

    /**
     * Establece la instancia principal de la aplicación.
     *
     * @param mainApp Referencia a la clase MainApp.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Maneja el evento de renovación del contrato seleccionado.
     * Cambia la escena a la vista de detalles de renovación.
     */
    @FXML
    private void handleRenovarSeleccionado() {
        if (mainApp == null) {
            System.err.println("mainApp no está inicializado.");
            return;
        }

        Contrato contratoSeleccionado = tablaContratos.getSelectionModel().getSelectedItem();
        if (contratoSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/renovarRescatar_contrato.fxml"));
                Parent detallesRenovacionRoot = loader.load();

                RenovarRescatarController renovarRescatarController = loader.getController();
                renovarRescatarController.setContrato(contratoSeleccionado, cliente);
                renovarRescatarController.setMainApp(mainApp);
                renovarRescatarController.setClienteYContratos(cliente, tablaContratos.getItems());

                Scene detallesRenovacionScene = new Scene(detallesRenovacionRoot);
                mainApp.getPrimaryStage().setScene(detallesRenovacionScene);
                mainApp.getPrimaryStage().setTitle("Detalles de Renovación");
                mainApp.getPrimaryStage().setMaximized(true);
                mainApp.getPrimaryStage().setMaximized(false);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al cargar la vista de detalles de renovación: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, selecciona un contrato para renovar.");
        }
    }

    /**
     * Maneja el evento de cancelar la selección.
     * Regresa a la vista del área de trabajo.
     */
    @FXML
    private void handleCancelar() {
        if (mainApp == null) {
            System.err.println("mainApp no está inicializado.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/area_trabajo.fxml"));
            Parent clienteFormRoot = loader.load();

            AreaTrabajoController areaTrabajoController = loader.getController();
            areaTrabajoController.setMainApp(mainApp);

            Scene clienteFormScene = new Scene(clienteFormRoot);
            mainApp.getPrimaryStage().setScene(clienteFormScene);
            mainApp.getPrimaryStage().setTitle("Formulario Cliente");
            mainApp.getPrimaryStage().setMaximized(true);
            mainApp.getPrimaryStage().setMaximized(false);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista anterior: " + e.getMessage());
        }
    }
}
