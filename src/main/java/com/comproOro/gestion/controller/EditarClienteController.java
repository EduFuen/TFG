package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.dao.ClienteDAO;
import com.comproOro.gestion.model.dao.ContratoDAO;
import com.comproOro.gestion.model.modelos.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;

public class EditarClienteController {

    @FXML
    private TextField dniField;
    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidoField;
    @FXML
    private TextField poblacionField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField direccionField;

    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private ContratoDAO contratoDAO;
    private MainApp mainApp;

    // Constructor inicializa DAOs para acceder a la base de datos
    public EditarClienteController() {
        this.clienteDAO = new ClienteDAO();
        this.contratoDAO = new ContratoDAO();
    }

    /**
     * Método para cargar el cliente a editar y llenar los campos del formulario
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            dniField.setText(cliente.getDni());
            nombreField.setText(cliente.getNombre());
            apellidoField.setText(cliente.getApellido());
            poblacionField.setText(cliente.getPoblacion());
            telefonoField.setText(cliente.getTelefono());
            direccionField.setText(cliente.getDireccion());
        }
    }

    /**
     * Método que maneja el evento de guardar cambios
     */
    @FXML
    private void handleGuardar() {
        if (cliente != null) {
            String dniAntiguo = cliente.getDni();
            String nuevoDni = dniField.getText();


            if (!isValidNifCif(nuevoDni)) {
                showAlert("Error", "El DNI ingresado no es válido. Por favor ingrese un NIF o CIF correcto.", Alert.AlertType.ERROR);
                return;
            }

            // Actualizar datos del cliente con los datos del formulario
            cliente.setDni(nuevoDni);
            cliente.setNombre(nombreField.getText());
            cliente.setApellido(apellidoField.getText());
            cliente.setPoblacion(poblacionField.getText());
            cliente.setTelefono(telefonoField.getText());
            cliente.setDireccion(direccionField.getText());


            boolean exitoCliente = clienteDAO.actualizarCliente(cliente, dniAntiguo);

            if (exitoCliente) {

                boolean exitoContratos = contratoDAO.actualizarDniEnContratos(dniAntiguo, nuevoDni);
                if (exitoContratos) {
                    showAlert("Éxito", "El cliente y sus contratos se actualizaron correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Advertencia", "El cliente se actualizó, pero hubo un problema al actualizar los contratos.", Alert.AlertType.WARNING);
                }
            } else {
                showAlert("Error", "Hubo un error al actualizar el cliente.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Muestra un cuadro de diálogo con título, mensaje y tipo de alerta
     */
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Valida que el DNI/NIF/CIF sea correcto
     */
    private boolean isValidNifCif(String dni) {
        if (dni.matches("[0-9]{8}[A-Za-z]")) {
            return isValidNif(dni);
        } else if (dni.matches("[A-HJ-NP-SUVW][0-9]{7}[0-9A-J]")) {
            return isValidCif(dni);
        }
        return false;
    }

    /**
     * Valida el formato y letra de un NIF
     */
    private boolean isValidNif(String nif) {
        String numberPart = nif.substring(0, 8);
        char letterPart = Character.toUpperCase(nif.charAt(8));
        String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";

        try {
            int number = Integer.parseInt(numberPart);
            char correctLetter = validLetters.charAt(number % 23);
            return correctLetter == letterPart;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida un CIF según la normativa española
     */
    private boolean isValidCif(String cif) {
        if (cif.length() != 9) return false;

        char firstChar = Character.toUpperCase(cif.charAt(0));
        String validStartChars = "ABCDEFGHJKLMNPQRSUVW";
        if (!validStartChars.contains(String.valueOf(firstChar))) return false;

        int sumEven = 0;
        int sumOdd = 0;

        for (int i = 1; i < 8; i++) {
            int digit = Character.getNumericValue(cif.charAt(i));
            if (i % 2 == 0) {
                sumEven += digit;
            } else {
                int product = digit * 2;
                sumOdd += product > 9 ? product - 9 : product;
            }
        }

        int totalSum = sumEven + sumOdd;
        int controlDigit = (10 - (totalSum % 10)) % 10;
        char lastChar = cif.charAt(8);

        if (Character.isDigit(lastChar)) {
            return controlDigit == Character.getNumericValue(lastChar);
        } else {
            return "JABCDEFGHI".charAt(controlDigit) == lastChar;
        }
    }

    /**
     * Valida el DNI en tiempo real mientras el usuario escribe
     */
    @FXML
    private void handleDniValidation(KeyEvent event) {
        String dni = dniField.getText();
        if (dni.isEmpty() || !isValidNifCif(dni)) {
            dniField.setStyle("-fx-border-color: red;");
        } else {
            dniField.setStyle("");
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
