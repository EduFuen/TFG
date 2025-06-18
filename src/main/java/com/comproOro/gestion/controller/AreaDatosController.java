package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para el área de datos, permite navegar a las vistas de clientes, contratos o volver al inicio.
 */
public class AreaDatosController {


    @FXML
    private Button consultarClientesButton;


    @FXML
    private Button consultarContratosButton;


    @FXML
    private Button btnCancelar;


    private MainApp mainApp;

    /**
     * Método que se ejecuta al pulsar el botón para consultar clientes.
     * Carga la ventana con la lista de clientes.
     */
    @FXML
    private void handleConsultarClientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/datos_clientes.fxml"));
            Parent clienteListRoot = loader.load();

            DatosClientesController datosClientesController = loader.getController();
            if (datosClientesController != null) {
                datosClientesController.setMainApp(mainApp);
            }

            Scene clienteListScene = new Scene(clienteListRoot);
            Stage primaryStage = (Stage) consultarClientesButton.getScene().getWindow();
            primaryStage.setScene(clienteListScene);
            primaryStage.setTitle("Lista de Clientes");
            primaryStage.setMaximized(true);
            primaryStage.setMaximized(false); // Forzar restaurar tamaño si es necesario
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que se ejecuta al pulsar el botón para consultar contratos.
     * Carga la ventana con la lista de contratos.
     */
    @FXML
    private void handleConsultarContratos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/datos_contratos.fxml"));
            Parent contratosListRoot = loader.load();

            DatosContratosController contratosListController = loader.getController();
            if (contratosListController != null) {
                contratosListController.setMainApp(mainApp);
            }

            Scene contratosListScene = new Scene(contratosListRoot);
            Stage primaryStage = (Stage) consultarContratosButton.getScene().getWindow();
            primaryStage.setScene(contratosListScene);
            primaryStage.setTitle("Lista de Contratos");
            primaryStage.setMaximized(true);
            primaryStage.setMaximized(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece la referencia a la aplicación principal.
     * @param mainApp instancia de la clase principal MainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Método que se ejecuta al pulsar el botón cancelar.
     * Vuelve a la ventana de inicio de la aplicación.
     */
    @FXML
    private void handleCancelar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inicio.fxml"));
            Parent root = loader.load();

            Inicio controller = loader.getController();
            if (controller != null) {
                controller.setMainApp(mainApp);
            }

            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);

            stage.setMaximized(true);
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
