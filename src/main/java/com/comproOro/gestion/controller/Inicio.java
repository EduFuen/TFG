package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Inicio {

    private MainApp mainApp;

    // Setter para la instancia de MainApp
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        // Método de inicialización vacío, puede usarse para preparar datos o UI
    }

    /**
     * Abre el área de trabajo cargando su escena y controlador
     */
    @FXML
    private void handleOpenAreaTrabajo() {
        if (mainApp == null) {
            System.out.println("mainApp es null");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/area_trabajo.fxml"));
            Parent areaTrabajoRoot = loader.load();

            // Configurar el controlador con la instancia de MainApp
            AreaTrabajoController controller = loader.getController();
            if (controller != null) {
                controller.setMainApp(mainApp);
            } else {
                System.err.println("El controlador de AreaTrabajoController no se inicializó correctamente.");
            }

            Stage primaryStage = mainApp.getPrimaryStage();
            Scene areaTrabajoScene = new Scene(areaTrabajoRoot);
            primaryStage.setScene(areaTrabajoScene);
            primaryStage.setTitle("Formulario de Cliente");

            // Forzar actualización del tamaño de la ventana (puede ser necesario por algún bug gráfico)
            primaryStage.setMaximized(true);
            primaryStage.setMaximized(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre el área de datos cargando su escena y controlador
     */
    @FXML
    private void handleOpenAreaDatos(ActionEvent actionEvent) {
        if (mainApp == null) {
            System.out.println("Error: mainApp no ha sido inicializado.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/area_datos.fxml"));
            Parent areaDatosRoot = loader.load();

            AreaDatosController datosController = loader.getController();
            if (datosController != null) {
                datosController.setMainApp(mainApp);
            }

            Stage primaryStage = mainApp.getPrimaryStage();
            Scene areaDatosScene = new Scene(areaDatosRoot);
            primaryStage.setScene(areaDatosScene);
            primaryStage.setTitle("Datos");

            primaryStage.setMaximized(true);
            primaryStage.setMaximized(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
