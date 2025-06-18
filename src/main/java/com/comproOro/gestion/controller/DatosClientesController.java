package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.dao.ClienteDAO;
import com.comproOro.gestion.model.dao.ContratoDAO;
import com.comproOro.gestion.model.modelos.Cliente;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para gestionar la vista donde se muestran los datos de los clientes,
 * permite buscar, editar, borrar y cancelar.
 */
public class DatosClientesController {

    @FXML
    private TableView<Cliente> clientesTableView;

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

    @FXML
    private TextField buscarDniTextField;

    private MainApp mainApp;

    /**
     * Establece la referencia a la aplicación principal.
     * @param mainApp instancia principal MainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Método llamado automáticamente tras cargar el FXML.
     * Configura las columnas de la tabla y carga los clientes.
     */
    @FXML
    public void initialize() {
        // Configura las columnas para mostrar las propiedades correspondientes del cliente
        dniColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDni()));
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        apellidoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        poblacionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPoblacion()));
        telefonoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        direccionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDireccion()));

        cargarClientes();
    }

    /**
     * Carga todos los clientes desde la base de datos y los muestra en la tabla.
     */
    private void cargarClientes() {
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> clientes = clienteDAO.obtenerTodosLosClientes();
        clientesTableView.getItems().setAll(clientes);
    }

    /**
     * Busca clientes por DNI introducido en el campo de búsqueda.
     * Si el campo está vacío, carga todos los clientes.
     */
    @FXML
    private void handleBuscarDni() {
        String dni = buscarDniTextField.getText();
        if (dni == null || dni.isEmpty()) {
            cargarClientes();
        } else {
            ClienteDAO clienteDAO = new ClienteDAO();
            Cliente clienteFiltrado = clienteDAO.obtenerClientePorDni(dni);
            if (clienteFiltrado != null) {
                clientesTableView.getItems().setAll(clienteFiltrado);
            } else {
                // Si no se encuentra cliente, limpia la tabla o muestra mensaje según convenga
                clientesTableView.getItems().clear();
            }
        }
    }

    /**
     * Abre una ventana para editar el cliente seleccionado en la tabla.
     * Si no hay ningún cliente seleccionado, muestra una alerta.
     */
    @FXML
    private void handleEditarCliente() {
        Cliente clienteSeleccionado = clientesTableView.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editar_cliente.fxml"));
                Parent root = loader.load();

                EditarClienteController editarController = loader.getController();
                editarController.setCliente(clienteSeleccionado);
                editarController.setMainApp(mainApp);

                Stage stage = new Stage();
                stage.setTitle("Editar Cliente");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                cargarClientes();  // Recarga la tabla para reflejar cambios

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Alerta si no se ha seleccionado ningún cliente
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin selección");
            alert.setHeaderText("No se ha seleccionado un cliente");
            alert.setContentText("Por favor, selecciona un cliente para editar.");
            alert.showAndWait();
        }
    }

    /**
     * Acción para cancelar y volver a la vista anterior.
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

            Stage stage = (Stage) clientesTableView.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);

            // Forzar redimensionado para que la interfaz se adapte bien
            stage.setMaximized(true);
            stage.setMaximized(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina el cliente seleccionado después de confirmar que no tiene contratos asociados.
     * Muestra alertas informativas en cada paso.
     */
    @FXML
    private void handleBorrarCliente() {
        Cliente clienteSeleccionado = clientesTableView.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            String dniCliente = clienteSeleccionado.getDni();
            ContratoDAO contratoDAO = new ContratoDAO();

            if (contratoDAO.tieneContratos(dniCliente)) {
                showAlert("Error", "No se puede borrar el cliente porque tiene contratos asociados.", Alert.AlertType.ERROR);
            } else {
                Alert confirmacionAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacionAlert.setTitle("Confirmación de borrado");
                confirmacionAlert.setHeaderText("¿Estás seguro de que deseas borrar al cliente?");
                confirmacionAlert.setContentText("Una vez borrado, no podrás recuperar los datos.");

                if (confirmacionAlert.showAndWait().get() == ButtonType.OK) {
                    ClienteDAO clienteDAO = new ClienteDAO();
                    boolean exitoBorrado = clienteDAO.borrarCliente(dniCliente);

                    if (exitoBorrado) {
                        showAlert("Éxito", "El cliente se ha borrado correctamente.", Alert.AlertType.INFORMATION);
                        cargarClientes();  // Recarga la lista tras borrar
                    } else {
                        showAlert("Error", "Hubo un error al borrar el cliente.", Alert.AlertType.ERROR);
                    }
                }
            }
        } else {
            showAlert("Sin selección", "Por favor selecciona un cliente para borrar.", Alert.AlertType.WARNING);
        }
    }

    /**
     * Muestra una alerta con título, mensaje y tipo especificados.
     * @param title título de la alerta
     * @param message mensaje de la alerta
     * @param alertType tipo de alerta (INFO, ERROR, WARNING, CONFIRMATION)
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
