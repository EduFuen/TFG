package com.comproOro.gestion.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.comproOro.gestion.model.modelos.Producto;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Controlador para la ventana de añadir un producto a un contrato.
 * Gestiona la entrada de datos, validación y creación del objeto Producto.
 */
public class AnadirProductoController {


    @FXML
    private TextField txtCantidad;


    @FXML
    private TextField txtDescripcion;


    @FXML
    private TextArea txtObservaciones;


    @FXML
    private TextField txtPeso;


    @FXML
    private TextField txtImporte;


    @FXML
    private Label lblMensaje;

    /**ventana actual donde se muestra este diálogo. */
    private Stage dialogStage;

    /** Referencia al controlador del nuevo contrato para añadir el producto. */
    private NuevoContratoController nuevoContratoController;

    /** Identificador del contrato al que se añade el producto. */
    private String idContrato;

    /**
     * Establece la referencia al controlador del nuevo contrato.
     * @param controller controlador de nuevo contrato
     */
    public void setNuevoContratoController(NuevoContratoController controller) {
        this.nuevoContratoController = controller;
    }

    /**
     * Establece el Stage (ventana) actual.
     * @param dialogStage ventana actual del diálogo
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Establece el identificador del contrato al que se añadirá el producto.
     * @param idContrato id del contrato
     */
    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    /**
     * Método que se ejecuta al pulsar el botón para guardar el producto.
     * Valida los campos, crea un objeto Producto y lo añade al controlador de nuevo contrato.
     */
    @FXML
    private void handleGuardarProducto() {
        String cantidadText = txtCantidad.getText();
        String descripcion = txtDescripcion.getText();
        String observaciones = txtObservaciones.getText();
        String pesoText = txtPeso.getText();
        String importeText = txtImporte.getText();


        if (cantidadText.isEmpty() || descripcion.isEmpty() || pesoText.isEmpty() || importeText.isEmpty()) {
            lblMensaje.setText("Por favor, completa todos los campos requeridos.");
            return;
        }

        int cantidad;
        double peso;
        double importe;

        try {
            cantidad = Integer.parseInt(cantidadText);

            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

            peso = format.parse(pesoText).doubleValue();
            importe = format.parse(importeText).doubleValue();
        } catch (NumberFormatException e) {
            lblMensaje.setText("Por favor, introduce valores válidos para cantidad, peso e importe.");
            System.err.println("Error de formato numérico: " + e.getMessage());
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            lblMensaje.setText("Error al interpretar el formato numérico. Asegúrate de usar el separador decimal correcto.");
            System.err.println("Error de formato numérico: " + e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            lblMensaje.setText("Ocurrió un error inesperado: " + e.getMessage());
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return;
        }


        double precioGramo = importe / peso;
        DecimalFormat df = new DecimalFormat("#.00");
        String precioGramoFormateado = df.format(precioGramo);
        double precioGramoRedondeado = Double.parseDouble(precioGramoFormateado.replace(",", "."));


        Producto producto = new Producto(0, cantidad, descripcion, observaciones, peso, precioGramoRedondeado, importe, idContrato);


        if (nuevoContratoController != null) {
            nuevoContratoController.addProducto(producto);
            lblMensaje.setText("Producto añadido a la lista del contrato.");
            clearFields();
        } else {
            lblMensaje.setText("Error: controlador de nuevo contrato no establecido.");
        }

        // Cerrar la ventana de diálogo
        dialogStage.close();
    }

    /**
     * Limpia los campos de texto y el mensaje de error/confirmación.
     */
    private void clearFields() {
        txtCantidad.clear();
        txtDescripcion.clear();
        txtObservaciones.clear();
        txtPeso.clear();
        txtImporte.clear();
        lblMensaje.setText("");
    }
}
