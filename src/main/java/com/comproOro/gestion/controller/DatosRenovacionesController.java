package com.comproOro.gestion.controller;

import com.comproOro.gestion.model.modelos.Renovacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controlador para la ventana que muestra las renovaciones de un contrato.
 */
public class DatosRenovacionesController {

    @FXML
    private TableView<Renovacion> renovacionesTableView;

    @FXML
    private TableColumn<Renovacion, Integer> idContratoColumn;

    @FXML
    private TableColumn<Renovacion, String> fechaRenovacionColumn;

    @FXML
    private TableColumn<Renovacion, String> fechaFinRenovacionColumn;

    @FXML
    private TableColumn<Renovacion, Integer> versionColumn;

    @FXML
    private TableColumn<Renovacion, Double> importeColumn;

    @FXML
    private Button cancelButton;

    /**
     * Método para asignar la lista de renovaciones que se mostrarán en la tabla.
     * @param renovaciones Lista de renovaciones a mostrar
     */
    public void setRenovaciones(List<Renovacion> renovaciones) {
        renovacionesTableView.getItems().setAll(renovaciones);
    }

    /**
     * Método llamado automáticamente tras la carga del FXML.
     * Configura las columnas de la tabla vinculándolas con las propiedades de Renovacion.
     */
    @FXML
    public void initialize() {

        idContratoColumn.setCellValueFactory(new PropertyValueFactory<>("idContrato"));
        fechaRenovacionColumn.setCellValueFactory(new PropertyValueFactory<>("fechaRenovacion"));
        fechaFinRenovacionColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFinRenovacion"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        importeColumn.setCellValueFactory(new PropertyValueFactory<>("importe"));
    }

    /**
     * Maneja la acción de cancelar y cerrar la ventana.
     */
    @FXML
    public void handleCancelar() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
