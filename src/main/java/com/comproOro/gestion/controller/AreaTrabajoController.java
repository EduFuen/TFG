package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.dao.ClienteDAO;
import com.comproOro.gestion.model.dao.ContratoDAO;
import com.comproOro.gestion.model.modelos.Cliente;
import com.comproOro.gestion.model.modelos.Contrato;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controlador para el area de trabajo donde se ven los datos de  clientes y contratos.
 *
 */
public class AreaTrabajoController {

    @FXML
    private Button btnCancelar;

    @FXML
    private TextField txtDni;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtPoblacion;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtDireccion;

    @FXML
    private Button btnNuevoContrato;

    @FXML
    private Button btnRenovarContrato;

    @FXML
    private Label lblMensaje;

    private ClienteDAO clienteDAO;

    private MainApp mainApp;

    /**
     * Constructor que inicializa el DAO de clientes.
     */
    public AreaTrabajoController() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Establece la referencia a la aplicación principal.
     * @param mainApp instancia principal MainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Guarda un nuevo cliente tras validar los datos ingresados.
     * Muestra mensajes según el resultado.
     */
    @FXML
    private void handleSave() {
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String poblacion = txtPoblacion.getText();
        String telefono = txtTelefono.getText();
        String direccion = txtDireccion.getText();

        if (dni.isEmpty() || !esDniValido(dni)) {
            lblMensaje.setText("Por favor, introduce un DNI válido.");
            return;
        }

        if (nombre.isEmpty() || apellido.isEmpty() || poblacion.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            lblMensaje.setText("Por favor, completa todos los campos.");
            return;
        }

        Cliente cliente = new Cliente(dni, nombre, apellido, poblacion, telefono, direccion);

        boolean guardado = clienteDAO.guardarCliente(cliente);
        if (guardado) {
            lblMensaje.setText("Cliente guardado exitosamente.");
        } else {
            lblMensaje.setText("El DNI ya existe. No se puede duplicar.");
        }
    }

    /**
     * Busca un cliente por DNI y muestra sus datos si lo encuentra.
     * Habilita o deshabilita botones según existan contratos.
     */
    @FXML
    private void handleSearch() {
        String dni = txtDni.getText().trim();

        if (dni.isEmpty() || !esDniValido(dni)) {
            lblMensaje.setText("Por favor, introduce un DNI válido.");
            return;
        }

        Cliente cliente = clienteDAO.obtenerClientePorDni(dni);
        if (cliente != null) {
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
            txtPoblacion.setText(cliente.getPoblacion());
            txtTelefono.setText(cliente.getTelefono());
            txtDireccion.setText(cliente.getDireccion());
            lblMensaje.setText("Cliente encontrado.");
            btnNuevoContrato.setDisable(false);

            ContratoDAO contratoDAO = new ContratoDAO();
            List<Contrato> contratosEmpeno = contratoDAO.obtenerContratosEmpenoPorDni(dni);

            List<Contrato> contratosNoRescatados = contratosEmpeno.stream()
                    .filter(contrato -> !Objects.equals(contrato.getRescatado(), "S"))
                    .collect(Collectors.toList());

            if (contratosNoRescatados.isEmpty()) {
                lblMensaje.setText("No se encontraron contratos de empeño no rescatados.");
                btnRenovarContrato.setDisable(true);
            } else {
                btnRenovarContrato.setDisable(false);
                lblMensaje.setText("Contratos de empeño no rescatados encontrados: " + contratosNoRescatados.size());
            }
        } else {
            lblMensaje.setText("Cliente no encontrado.");
            txtNombre.clear();
            txtApellido.clear();
            txtPoblacion.clear();
            txtTelefono.clear();
            txtDireccion.clear();
            btnNuevoContrato.setDisable(true);
            btnRenovarContrato.setDisable(true);
        }
    }

    /**
     * Abre la ventana para mostrar la lista de clientes.
     * @throws IOException en caso de error al cargar la vista
     */
    @FXML
    private void mostrarClientes() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cliente_mostrar.fxml"));
        Parent clienteMostrarRoot = loader.load();

        ClientesMostrarController controller = loader.getController();
        controller.setMainApp(mainApp);
        controller.setOrigenController(this);

        Scene clienteMostrarScene = new Scene(clienteMostrarRoot);
        mainApp.getPrimaryStage().setScene(clienteMostrarScene);
        mainApp.getPrimaryStage().setTitle("Mostrar Clientes");
        mainApp.getPrimaryStage().setMaximized(true);
        mainApp.getPrimaryStage().setMaximized(false);
    }

    /**
     * Abre la ventana para crear un nuevo contrato con los datos del cliente actual.
     */
    @FXML
    private void handleNuevoContrato() {
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nuevo_contrato.fxml"));
            Parent nuevoContratoRoot = loader.load();

            NuevoContratoController nuevoContratoController = loader.getController();
            nuevoContratoController.setClienteDatos(dni, nombre, apellido);
            nuevoContratoController.setMainApp(mainApp);

            Scene nuevoContratoScene = new Scene(nuevoContratoRoot);
            mainApp.getPrimaryStage().setScene(nuevoContratoScene);
            mainApp.getPrimaryStage().setTitle("Nuevo Contrato");
            mainApp.getPrimaryStage().setMaximized(true);
            mainApp.getPrimaryStage().setMaximized(false);

        } catch (IOException e) {
            e.printStackTrace();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error");
            dialog.setContentText("No se pudo cargar la vista del contrato.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    /**
     * Abre la ventana para renovar contratos de empeño no rescatados del cliente.
     * @param event evento de acción disparado por el botón
     */
    @FXML
    private void handleRenovarContrato(ActionEvent event) {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            lblMensaje.setText("Por favor, introduce un DNI para buscar contratos.");
            return;
        }

        ContratoDAO contratoDAO = new ContratoDAO();
        List<Contrato> contratosEmpeno = contratoDAO.obtenerContratosEmpenoPorDni(dni);

        if (contratosEmpeno.isEmpty()) {
            lblMensaje.setText("No se encontraron contratos de empeño para el DNI proporcionado.");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/selecionar_contrato.fxml"));
                Parent renovarContratoRoot = loader.load();

                SeleccionarContratoController seleccionarContratoController = loader.getController();

                Cliente cliente = new Cliente(txtDni.getText(), txtNombre.getText(), txtApellido.getText(),
                        txtPoblacion.getText(), txtTelefono.getText(), txtDireccion.getText());

                seleccionarContratoController.setDatosClienteYContratos(cliente, contratosEmpeno);
                seleccionarContratoController.setMainApp(mainApp);

                Scene renovarContratoScene = new Scene(renovarContratoRoot);
                mainApp.getPrimaryStage().setScene(renovarContratoScene);
                mainApp.getPrimaryStage().setTitle("Renovar Contrato");
                mainApp.getPrimaryStage().setMaximized(true);
                mainApp.getPrimaryStage().setMaximized(false);

            } catch (IOException e) {
                e.printStackTrace();
                lblMensaje.setText("Error al cargar la vista de renovación de contrato.");
            }
        }
    }

    /**
     * Valida que el DNI tenga un formato correcto y una letra válida.
     * Soporta formatos comunes de NIF/NIE.
     * @param dni cadena con el DNI a validar
     * @return true si el DNI es válido, false en caso contrario
     */
    private boolean esDniValido(String dni) {
        if (dni == null || dni.isEmpty()) {
            return false;
        }

        // DNI formato: 8 números y una letra al final
        if (dni.matches("\\d{8}[A-Z]")) {
            String numeros = dni.substring(0, 8);
            char letra = dni.charAt(8);

            int dniNum = Integer.parseInt(numeros);
            char letraCalculada = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(dniNum % 23);

            return letra == letraCalculada;
        }

        // NIF válido: letra seguida de 8 dígitos
        if (dni.matches("[A-Z]\\d{8}")) {
            return true;
        }

        // NIE válido: X, Y o Z seguido de 7 dígitos y una letra
        if (dni.matches("[XYZ]\\d{7}[A-Z]")) {
            return true;
        }

        return false;
    }

    @FXML
    private void handleCancelar() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inicio.fxml"));
            Parent root = loader.load();


            Inicio controller = loader.getController();
            controller.setMainApp(mainApp); // Establecer mainApp para el controlador de la vista cliente


            Stage stage = (Stage) btnCancelar.getScene().getWindow();


            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);

            stage.setMaximized(true);
            stage.setMaximized(false);


            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al cargar la vista del cliente.");
        }
    }
}
