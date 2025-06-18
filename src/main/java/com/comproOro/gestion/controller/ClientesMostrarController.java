package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.dao.ClienteDAO;
import com.comproOro.gestion.model.modelos.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para mostrar la lista de clientes en una tabla.
 * Permite cargar los clientes y volver a la vista anterior.
 */
public class ClientesMostrarController {

    @FXML
    private TableView<Cliente> clientesTable;

    @FXML
    private TableColumn<Cliente, String> dniColumn;

    @FXML
    private TableColumn<Cliente, String> nombreColumn;

    @FXML
    private TableColumn<Cliente, String> apellidoColumn;

    @FXML
    private TableColumn<Cliente, String> poblacionColumn;

    @FXML
    private TableColumn<Cliente, String> telefonoColumn;

    @FXML
    private TableColumn<Cliente, String> direccionColumn;

    private ClienteDAO clienteDAO;

    private ObservableList<Cliente> clientesList;

    private MainApp mainApp;

    /**
     * Referencia al controlador que llamó a esta vista.
     * Se usa para volver correctamente a la pantalla anterior.
     */
    private Object origenController;

    /**
     * Constructor que inicializa el DAO y la lista observable.
     */
    public ClientesMostrarController() {
        clienteDAO = new ClienteDAO();
        clientesList = FXCollections.observableArrayList();
    }

    /**
     * Método llamado automáticamente después de cargar el FXML.
     * Configura las columnas de la tabla y carga los clientes.
     */
    @FXML
    public void initialize() {
        // Configura qué propiedad mostrar en cada columna
        dniColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDni()));
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        apellidoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        poblacionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPoblacion()));
        telefonoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        direccionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDireccion()));

        cargarClientes();
    }

    /**
     * Obtiene todos los clientes del DAO y los coloca en la tabla.
     */
    private void cargarClientes() {
        List<Cliente> clientes = clienteDAO.obtenerTodosLosClientes();
        clientesList.setAll(clientes);
        clientesTable.setItems(clientesList);
    }

    /**
     * Acción para cancelar y regresar a la vista anterior.
     * Decide a qué vista regresar según el controlador origen.
     */
    @FXML
    private void handleCancelar() {
        try {
            FXMLLoader loader;

            // Decide qué vista cargar según el controlador origen
            if (origenController instanceof AreaTrabajoController) {
                loader = new FXMLLoader(getClass().getResource("/fxml/area_trabajo.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/fxml/area_datos.fxml"));
            }

            Parent root = loader.load();

            // Configura el controlador de la vista destino con mainApp
            if (origenController instanceof AreaTrabajoController) {
                AreaTrabajoController areaTrabajoController = loader.getController();
                areaTrabajoController.setMainApp(mainApp);
            } else if (origenController instanceof AreaDatosController) {
                AreaDatosController areaDatosController = loader.getController();
                areaDatosController.setMainApp(mainApp);
            }

            // Cambia la escena actual a la vista anterior
            Stage stage = (Stage) clientesTable.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.setMaximized(true);
            stage.setMaximized(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el controlador que llamó a esta vista.
     * @param origenController controlador origen (puede ser AreaTrabajoController o AreaDatosController)
     */
    public void setOrigenController(Object origenController) {
        this.origenController = origenController;
    }

    /**
     * Establece la referencia a la aplicación principal.
     * @param mainApp instancia principal MainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
