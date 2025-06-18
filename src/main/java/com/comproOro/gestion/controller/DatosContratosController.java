package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.dao.ContratoDAO;
import com.comproOro.gestion.model.dao.RenovacionDAO;
import com.comproOro.gestion.model.modelos.Contrato;
import com.comproOro.gestion.model.modelos.Renovacion;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

/**
 * Controlador para la gestión de contratos:
 * muestra los contratos en una tabla, permite buscar con filtros,
 * limpiar filtros, cancelar y navegar a renovaciones de un contrato seleccionado.
 */
public class DatosContratosController {

    @FXML
    private TableView<Contrato> contratosTableView;

    @FXML
    private TableColumn<Contrato, String> idContratoColumn;
    @FXML
    private TableColumn<Contrato, String> idPolColumn;
    @FXML
    private TableColumn<Contrato, String> clienteColumn;
    @FXML
    private TableColumn<Contrato, String> fechaColumn;
    @FXML
    private TableColumn<Contrato, String> fechaFinalColumn;
    @FXML
    private TableColumn<Contrato, String> tipoColumn;
    @FXML
    private TableColumn<Contrato, String> estadoColumn;
    @FXML
    private TableColumn<Contrato, String> importeColumn;

    @FXML
    private TextField searchTextField;
    @FXML
    private ComboBox<String> tipoComboBox;
    @FXML
    private DatePicker fechaInicioPicker;
    @FXML
    private DatePicker fechaFinalPicker;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnCancelar;

    private MainApp mainApp;
    private ContratoDAO contratoDAO;

    public DatosContratosController() {
        contratoDAO = new ContratoDAO();
    }

    /**
     * Método llamado automáticamente al cargar el controlador.
     * Configura columnas, carga datos iniciales y configura combobox y botones.
     */
    @FXML
    private void initialize() {
        // Configuración de columnas, asignando valores desde objetos Contrato
        idContratoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdContrato()));
        idPolColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdPol()));
        clienteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDniCliente()));

        fechaColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaInicio() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                return new SimpleStringProperty(sdf.format(cellData.getValue().getFechaInicio()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        fechaFinalColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaFinal() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                return new SimpleStringProperty(sdf.format(cellData.getValue().getFechaFinal()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        tipoColumn.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getTipo();
            // Corrección visual para "Empeño"
            if ("Empeno".equals(tipo)) {
                tipo = "Empeño";
            }
            return new SimpleStringProperty(tipo);
        });

        estadoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRescatado()));
        importeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getImporte())));


        cargarContratos();


        tipoComboBox.getItems().clear();
        tipoComboBox.getItems().add("Cualquiera");
        tipoComboBox.getItems().add("Compra");
        tipoComboBox.getItems().add("Empeño");
        tipoComboBox.setValue("Cualquiera");


        btnBuscar.setOnAction(event -> handleBuscar());
    }

    /**
     * Carga todos los contratos en la tabla.
     */
    private void cargarContratos() {
        List<Contrato> contratos = contratoDAO.obtenerTodosLosContratos();
        contratosTableView.getItems().setAll(contratos);
    }

    /**
     * Filtra los contratos según texto de búsqueda, tipo y fechas seleccionadas.
     */
    @FXML
    private void handleBuscar() {
        String searchText = searchTextField.getText().trim();
        String tipoSeleccionado = tipoComboBox.getValue();

        Date fechaInicio = null;
        Date fechaFinal = null;

        if (fechaInicioPicker.getValue() != null) {
            fechaInicio = java.sql.Date.valueOf(fechaInicioPicker.getValue());
        }

        if (fechaFinalPicker.getValue() != null) {
            fechaFinal = java.sql.Date.valueOf(fechaFinalPicker.getValue());
        }

        if ("Cualquiera".equals(tipoSeleccionado)) {
            tipoSeleccionado = null;
        } else if ("Empeño".equals(tipoSeleccionado)) {
            tipoSeleccionado = "Empeno";  // Adaptar al valor interno
        }

        List<Contrato> contratosFiltrados;


        if (fechaInicio == null && fechaFinal == null) {
            contratosFiltrados = contratoDAO.buscarContratos(searchText, tipoSeleccionado);
        } else if (fechaInicio != null && fechaFinal != null) {
            contratosFiltrados = contratoDAO.buscarContratosConFechas(
                    searchText, tipoSeleccionado,
                    (java.sql.Date) fechaInicio, (java.sql.Date) fechaFinal);
        } else if (fechaInicio != null) {
            contratosFiltrados = contratoDAO.buscarContratosConFechaInicio(
                    searchText, tipoSeleccionado, (java.sql.Date) fechaInicio);
        } else {
            contratosFiltrados = contratoDAO.buscarContratosConFechaFinal(
                    searchText, tipoSeleccionado, (java.sql.Date) fechaFinal);
        }

        contratosTableView.getItems().setAll(contratosFiltrados);
    }

    /**
     * Cancela y vuelve a la pantalla anterior (área de datos).
     */
    @FXML
    private void handleCancelar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/area_datos.fxml"));
            Parent root = loader.load();

            AreaDatosController datosController = loader.getController();
            if (datosController != null) {
                datosController.setMainApp(mainApp);
            }

            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);

            // Forzar redimensionado para que UI se adapte correctamente
            stage.setMaximized(true);
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter para la instancia principal de la aplicación.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Limpia los filtros y la tabla.
     */
    @FXML
    private void handleLimpiar() {
        searchTextField.clear();
        tipoComboBox.getSelectionModel().select("Cualquiera");
        fechaInicioPicker.setValue(null);
        fechaFinalPicker.setValue(null);

        contratosTableView.getItems().clear();
    }

    /**
     * Muestra las renovaciones del contrato seleccionado en una nueva ventana.
     */
    @FXML
    public void handleVerRenovaciones(ActionEvent actionEvent) {
        Contrato contratoSeleccionado = contratosTableView.getSelectionModel().getSelectedItem();

        if (contratoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleccionar Contrato");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un contrato para ver sus renovaciones.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/datos_renovaciones.fxml"));
            Parent root = loader.load();

            DatosRenovacionesController renovacionesController = loader.getController();

            RenovacionDAO renovacionDAO = new RenovacionDAO();
            List<Renovacion> renovaciones = renovacionDAO.obtenerRenovacionesPorIdContrato(contratoSeleccionado.getIdContrato());

            renovacionesController.setRenovaciones(renovaciones);

            Stage stage = new Stage();
            stage.setTitle("Renovaciones del Contrato " + contratoSeleccionado.getIdContrato());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
