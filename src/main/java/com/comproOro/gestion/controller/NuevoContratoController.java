package com.comproOro.gestion.controller;

import com.comproOro.gestion.MainApp;
import com.comproOro.gestion.model.dao.ContratoDAO;
import com.comproOro.gestion.model.dao.ProductoDAO;
import com.comproOro.gestion.model.modelos.Contrato;
import com.comproOro.gestion.model.modelos.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Controlador para la vista de creación de un nuevo contrato.
 * Permite gestionar productos, tipo de contrato y generar documentos.
 */
public class NuevoContratoController {

    @FXML
    private Label lblDni;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblApellido;
    @FXML
    private TextArea txtDetallesContrato;
    @FXML
    private Label lblMensaje;
    @FXML
    private ListView<Producto> listViewProductos;
    @FXML
    private ComboBox<String> comboBoxTipoContrato;
    @FXML
    private Button btnGuardarContrato;
    @FXML
    private Button btnProcesar;
    @FXML
    private Button btnCancelar;
    @FXML
    private CheckBox checkBoxGenerarIdPol;
    @FXML
    private Button btnEliminarProducto;
    @FXML
    private Button btnAnadirProducto;

    private ObservableList<Producto> productos;
    private ContratoDAO contratoDAO;
    private boolean contratoGuardado = false;
    private Contrato contrato;
    private MainApp mainApp;

    /**
     * Inicializa los componentes de la vista.
     */
    @FXML
    private void initialize() {
        productos = FXCollections.observableArrayList();
        listViewProductos.setItems(productos);
        contratoDAO = new ContratoDAO();

        comboBoxTipoContrato.setItems(FXCollections.observableArrayList("Empeno", "Compra"));
        comboBoxTipoContrato.getSelectionModel().selectFirst();

        btnProcesar.setDisable(true);
        btnEliminarProducto.setDisable(true);

        listViewProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEliminarProducto.setDisable(newSelection == null);
        });
    }

    /**
     * Maneja el evento para guardar un contrato.
     */
    @FXML
    private void handleGuardarContrato() {
        if (!validarAntesDeGuardar()) {
            return;
        }

        crearContrato();
        guardarContratoEnBaseDeDatos();
    }

    /**
     * Valida si el contrato se puede guardar.
     *
     * @return true si es válido, false en caso contrario
     */
    private boolean validarAntesDeGuardar() {
        if (contratoGuardado) {
            lblMensaje.setText("El contrato ya ha sido guardado.");
            return false;
        }

        if (productos.isEmpty()) {
            lblMensaje.setText("No puedes guardar un contrato sin productos. Añade al menos un producto.");
            return false;
        }

        return true;
    }

    /**
     * Establece la instancia principal de la aplicación.
     *
     * @param mainApp instancia de MainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Crea una instancia de contrato con los datos del formulario.
     */
    private void crearContrato() {
        contrato = new Contrato();
        contrato.setDniCliente(lblDni.getText());
        contrato.setDetallesContrato(txtDetallesContrato.getText());
        contrato.setProductos(new ArrayList<>(productos));
        contrato.setFechaInicio(new Date(System.currentTimeMillis()));

        String tipoContrato = comboBoxTipoContrato.getValue();
        contrato.setTipo(tipoContrato);

        double sumaImportes = productos.stream().mapToDouble(Producto::getImporte).sum();
        contrato.setImporte(sumaImportes);

        if ("Empeno".equals(tipoContrato)) {
            contrato.setFechaFinal(new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)));
        } else {
            contrato.setFechaFinal(null);
        }

        if (checkBoxGenerarIdPol.isSelected()) {
            int numeroPol = contratoDAO.obtenerProximoNumeroPoliza();
            contrato.generarIdPol(numeroPol);
        } else {
            contrato.setIdPol(null);
        }
    }

    /**
     * Guarda el contrato y los productos en la base de datos.
     */
    private void guardarContratoEnBaseDeDatos() {
        try {
            if (contratoDAO.guardarContrato(contrato)) {
                contratoGuardado = true;
                btnGuardarContrato.setDisable(true);
                btnProcesar.setDisable(false);
                btnAnadirProducto.setDisable(true);

                guardarProductosEnBaseDeDatos();
                lblMensaje.setText("Contrato y productos guardados exitosamente.");
                productos.clear();
                listViewProductos.refresh();
            } else {
                lblMensaje.setText("Error al guardar el contrato.");
            }
        } catch (Exception e) {
            lblMensaje.setText("Ocurrió un error al guardar el contrato: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Guarda los productos asociados al contrato en la base de datos.
     */
    private void guardarProductosEnBaseDeDatos() {
        ProductoDAO productoDAO = new ProductoDAO();
        for (Producto producto : productos) {
            producto.setIdContrato(contrato.getIdContrato());
            if (!productoDAO.guardarProducto(producto)) {
                lblMensaje.setText("Error al guardar uno o más productos.");
            }
        }
    }

    /**
     * Procesa el contrato y genera un documento Word.
     */
    @FXML
    private void handleProcesar() {
        if (contrato == null) {
            lblMensaje.setText("No hay contrato guardado para procesar.");
            return;
        }

        try {
            ProductoDAO productoDAO = new ProductoDAO();
            ArrayList<Producto> productosDelContrato = productoDAO.obtenerProductosPorContrato(contrato.getIdContrato());

            if (productosDelContrato.isEmpty()) {
                lblMensaje.setText("No hay productos asociados a este contrato.");
                return;
            }

            WordGeneratorController wordGenerator = new WordGeneratorController();
            String contratoId = contrato.getIdContrato();

            ArrayList<String> productoIds = new ArrayList<>();
            for (Producto producto : productosDelContrato) {
                productoIds.add(String.valueOf(producto.getIdProducto()));
            }

            wordGenerator.generarDocumento(lblDni.getText(), contratoId, productosDelContrato);
            lblMensaje.setText("Contrato procesado y documento Word generado.");
        } catch (Exception e) {
            lblMensaje.setText("Error al procesar el contrato: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana para añadir un nuevo producto.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void handleAnadirProducto() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/anadir_producto.fxml"));
        Parent root = loader.load();

        AnadirProductoController controller = loader.getController();
        controller.setNuevoContratoController(this);

        Stage stage = new Stage();
        stage.setTitle("Añadir Producto");
        stage.setScene(new Scene(root));
        controller.setDialogStage(stage);
        stage.showAndWait();
    }

    /**
     * Añade un producto a la lista de productos del contrato.
     *
     * @param producto producto a añadir
     */
    public void addProducto(Producto producto) {
        productos.add(producto);
        listViewProductos.refresh();
    }

    /**
     * Establece los datos del cliente en la vista.
     *
     * @param dni      DNI del cliente
     * @param nombre   nombre del cliente
     * @param apellido apellido del cliente
     */
    public void setClienteDatos(String dni, String nombre, String apellido) {
        lblDni.setText(dni);
        lblNombre.setText(nombre);
        lblApellido.setText(apellido);
    }

    /**
     * Cancela la creación del contrato y vuelve al área de trabajo.
     */
    @FXML
    private void handleCancelar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/area_trabajo.fxml"));
            Parent root = loader.load();

            AreaTrabajoController controller = loader.getController();
            controller.setMainApp(mainApp);

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

    /**
     * Elimina el producto seleccionado de la lista.
     */
    @FXML
    private void handleEliminarProducto() {
        Producto productoSeleccionado = listViewProductos.getSelectionModel().getSelectedItem();

        if (productoSeleccionado != null) {
            productos.remove(productoSeleccionado);
            lblMensaje.setText("Producto eliminado exitosamente.");
        } else {
            lblMensaje.setText("Por favor, selecciona un producto para eliminar.");
        }
    }
}
