package com.comproOro.gestion.controller;

import com.comproOro.gestion.model.dao.ClienteDAO;
import com.comproOro.gestion.model.dao.ContratoDAO;
import com.comproOro.gestion.model.dao.RenovacionDAO;
import com.comproOro.gestion.model.modelos.Cliente;
import com.comproOro.gestion.model.modelos.Contrato;
import com.comproOro.gestion.model.modelos.Producto;
import com.comproOro.gestion.model.modelos.Renovacion;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
/**
 * Controlador encargado de generar documentos Word a partir de plantillas específicas
 * para diferentes tipos de contratos como empeños, compras, rescates y renovaciones.
 * Utiliza Apache POI para la manipulación de documentos Word (.docx).
 *
 *
 */
public class WordGeneratorController {

    private static final String PLANTILLA_EMPENO = "/plantillas/P-Empeno.docx";
    private static final String PLANTILLA_EMPENO_POL = "/plantillas/P-EmpenoPol.docx"; // Plantilla para "Empeno Pol"
    private static final String PLANTILLA_COMPRA = "/plantillas/P-Compra.docx"; // Plantilla para "Compra"
    private static final String PLANTILLA_COMPRA_POL = "/plantillas/P-CompraPol.docx";
    private static final String PLANTILLA_RESCTE = "/plantillas/P-Rescate.docx"; // Plantilla para "Rescate"
    private static final String PLANTILLA_RESCTE_POL = "/plantillas/P-RescatePol.docx"; // Plantilla para "Rescate Pol"
    private static final String PLANTILLA_RENOVACION = "/plantillas/P-Renovacion.docx"; // Plantilla para "Renovacion"
    private static final String PLANTILLA_RENOVACION_POL = "/plantillas/P-RenovacionPol.docx"; // Plantilla para "Renovacion Pol"

    private static final String APELLIDOS_CAMPO = "apellidos";
    private static final String NOMBRE_CAMPO = "nombre";
    private static final String DNI_CAMPO = "dni";
    private static final String DIRECCION_CAMPO = "direccion";
    private static final String TELEFONO_CAMPO = "telefono";
    private static final String POBLACION_CAMPO = "poblacion";



    /**
     * Genera un documento Word (y su correspondiente póliza si aplica) para un contrato dado,
     * utilizando plantillas predefinidas.
     *
     * @param dni DNI del cliente.
     * @param idContrato ID del contrato.
     * @param productos Lista de productos asociados al contrato.
     * @throws URISyntaxException si ocurre un error al generar la ruta del archivo.
     */

    public void generarDocumento(String dni, String idContrato, ArrayList<Producto> productos) throws URISyntaxException {
        Cliente cliente = obtenerCliente(dni);
        String outputFilePath = obtenerRutaSalida(dni,idContrato);
        Contrato contrato = obtenerContratoId(idContrato);
        boolean tienePoliza= contrato.getIdPol() != null;

        if (cliente != null && contrato != null) {
            try {

                String plantillaPath = seleccionarPlantillaSegunContrato(contrato, tienePoliza);
                try (InputStream fis = getClass().getResourceAsStream(plantillaPath)) {
                    if (fis == null) {
                        System.err.println("No se pudo encontrar el archivo de plantilla.");
                        return;
                    }
                    generarDocumento(cliente, fis, outputFilePath, contrato, productos);
                }


                if (tienePoliza) {
                    String outputPolizaPath = obtenerRutaSalidaPoliza(dni, idContrato);
                    String plantillaPolizaPath = seleccionarPlantillaSegunContratoParaPoliza(contrato);
                    try (InputStream fisPoliza = getClass().getResourceAsStream(plantillaPolizaPath)) {
                        if (fisPoliza == null) {
                            System.err.println("No se pudo encontrar el archivo de plantilla para póliza.");
                            return;
                        }
                        generarDocumentoPol(cliente, fisPoliza, outputPolizaPath, contrato, productos);
                    }
                }

                abrirDocumentoWord(outputFilePath);
                if (tienePoliza) {
                    abrirDocumentoWord(obtenerRutaSalidaPoliza(dni, idContrato));
                }
            } catch (Exception e) {
                System.err.println("Error al generar el documento para el cliente con DNI: " + dni);
                e.printStackTrace();
            }
        } else {
            System.out.println("Cliente con DNI " + dni + " o contrato no encontrado.");
        }
    }

    /**
     * Selecciona la plantilla adecuada según el tipo de contrato.
     *
     * @param contrato Contrato a evaluar.
     * @param tienePoliza Indica si el contrato tiene póliza.
     * @return Ruta de la plantilla correspondiente.
     */
    private String seleccionarPlantillaSegunContrato(Contrato contrato, boolean tienePoliza) {
        if (contrato.getTipo() != null && contrato.getTipo().equalsIgnoreCase("empeno")) {
            return PLANTILLA_EMPENO;
        } else if (contrato.getTipo() != null && contrato.getTipo().equalsIgnoreCase("compra")) {
            return PLANTILLA_COMPRA;
        } else {
            throw new IllegalArgumentException("Tipo de contrato no reconocido: " + contrato.getTipo());
        }
    }

    /**
     * Selecciona la plantilla de póliza adecuada según el tipo de contrato.
     *
     * @param contrato Contrato a evaluar.
     * @return Ruta de la plantilla de póliza correspondiente.
     */
    private String seleccionarPlantillaSegunContratoParaPoliza(Contrato contrato) {
        if (contrato.getTipo() != null && contrato.getTipo().equalsIgnoreCase("empeno")) {
            return PLANTILLA_EMPENO_POL;
        } else if (contrato.getTipo() != null && contrato.getTipo().equalsIgnoreCase("compra")) {
            return PLANTILLA_COMPRA_POL;
        } else {
            throw new IllegalArgumentException("Tipo de contrato no reconocido: " + contrato.getTipo());
        }
    }

    /**
     * Obtiene un cliente a partir de su DNI.
     *
     * @param dni DNI del cliente.
     * @return Objeto Cliente correspondiente.
     */
    private Cliente obtenerCliente(String dni) {
        ClienteDAO clienteDao = new ClienteDAO();
        return clienteDao.obtenerClientePorDni(dni);
    }

    /**
     * Obtiene un contrato a partir de su ID.
     *
     * @param idContrato ID del contrato.
     * @return Objeto Contrato correspondiente.
     */
    private Contrato obtenerContratoId(String idContrato) {
        ContratoDAO contratoDao = new ContratoDAO();
        return contratoDao.obtenerContratoPorId(idContrato);
    }

    /**
     * Genera la ruta de salida del archivo Word para el contrato.
     *
     * @param dni DNI del cliente.
     * @param idContrato ID del contrato.
     * @return Ruta completa del archivo generado.
     * @throws URISyntaxException si hay error al generar la ruta.
     */
    private String obtenerRutaSalida(String dni, String idContrato) throws URISyntaxException {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop/Contratos";
        return desktopPath + File.separator +  dni + "_" + idContrato +".docx";
    }
    /**
     * Genera la ruta de salida para documentos de renovación.
     *
     * @param dni DNI del cliente.
     * @param idContrato ID del contrato.
     * @param ultversion Número de versión de la renovación.
     * @return Ruta completa del archivo generado.
     * @throws URISyntaxException si hay error al generar la ruta.
     */
    private String obtenerRutaSalidaRenovar(String dni, String idContrato, int ultversion) throws URISyntaxException {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop/Contratos";
        return desktopPath + File.separator +  dni + "_" + idContrato+"_RENOVACION" + ultversion+".docx";
    }

    /**
     * Genera la ruta de salida para documentos de rescate.
     *
     * @param dni DNI del cliente.
     * @param idContrato ID del contrato.
     * @return Ruta completa del archivo generado.
     * @throws URISyntaxException si hay error al generar la ruta.
     */
    private String obtenerRutaSalidaRescatar(String dni, String idContrato) throws URISyntaxException {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop/Contratos";
        return desktopPath + File.separator +  dni + "_" + idContrato +"_RESCATE"+ ".docx";
    }

    /**
     * Genera la ruta de salida para pólizas asociadas a contratos.
     *
     * @param dni DNI del cliente.
     * @param idContrato ID del contrato.
     * @return Ruta completa del archivo generado.
     * @throws URISyntaxException si hay error al generar la ruta.
     */
    private String obtenerRutaSalidaPoliza(String dni,  String idContrato) throws URISyntaxException {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop/Contratos";
        return desktopPath + File.separator  + dni + "_" + idContrato +"_POL"+ ".docx";
    }

    /**
     * Genera la ruta de salida para pólizas asociadas a rescates.
     *
     * @param dni DNI del cliente.
     * @param idContrato ID del contrato.
     * @return Ruta completa del archivo generado.
     * @throws URISyntaxException si hay error al generar la ruta.
     */
    private String obtenerRutaSalidaPolizaRescate(String dni,  String idContrato) throws URISyntaxException {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop/Contratos";
        return desktopPath + File.separator  + dni + "_" + idContrato +"_RESCATE" + "_POL"+ ".docx";
    }

    /**
     * Genera la ruta de salida para pólizas de renovación.
     *
     * @param dni DNI del cliente.
     * @param idContrato ID del contrato.
     * @param ultversion Número de versión de la renovación.
     * @return Ruta completa del archivo generado.
     * @throws URISyntaxException si hay error al generar la ruta.
     */
    private String obtenerRutaSalidaPolizaRenovacion(String dni,  String idContrato, int ultversion) throws URISyntaxException {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop/Contratos";
        return desktopPath + File.separator  + dni + "_" + idContrato +"_RENOVACION" + "_POL"+ ultversion +".docx";
    }

    /**
     * Genera el documento Word del contrato principal reemplazando los campos y agregando productos.
     *
     * @param cliente Objeto Cliente con los datos personales.
     * @param fis InputStream de la plantilla Word.
     * @param outputFilePath Ruta donde se guardará el documento.
     * @param contrato Objeto Contrato con los datos del contrato.
     * @param productos Lista de productos asociados al contrato.
     * @throws Exception si ocurre un error al generar o guardar el documento.
     */
    private void generarDocumento(Cliente cliente, InputStream fis, String outputFilePath, Contrato contrato, ArrayList<Producto> productos) throws Exception {
        try (XWPFDocument document = new XWPFDocument(fis)) {
            Map<String, String> campos = crearMapaCampos(cliente, contrato);
            replaceText(document, campos);
            replaceInHeader(document, campos);


            generarTablaProductos(document, productos, contrato);

            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                document.write(fos);
            }

            System.out.println("Documento generado exitosamente en: " + outputFilePath);
        }
    }

    /**
     * Genera el documento Word de la póliza del contrato reemplazando los campos y agregando productos.
     *
     * @param cliente Objeto Cliente con los datos personales.
     * @param fis InputStream de la plantilla Word.
     * @param outputFilePath Ruta donde se guardará el documento.
     * @param contrato Objeto Contrato con los datos del contrato.
     * @param productos Lista de productos asociados al contrato.
     * @throws Exception si ocurre un error al generar o guardar el documento.
     */
    private void generarDocumentoPol(Cliente cliente, InputStream fis, String outputFilePath, Contrato contrato, ArrayList<Producto> productos) throws Exception {
        try (XWPFDocument document = new XWPFDocument(fis)) {
            Map<String, String> campos = crearMapaCampos(cliente, contrato);
            replaceText(document, campos);
            replaceInHeader(document, campos);


            generarTablaProductosPol(document, productos, contrato);

            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                document.write(fos);
            }

            System.out.println("Documento generado exitosamente en: " + outputFilePath);
        }
    }

    /**
     * Crea un mapa de campos con información del cliente y contrato.
     *
     * @param cliente  Objeto  que contiene la información del cliente.
     * @param contrato Objeto  que contiene la información del contrato.
     * @return Un  con claves de texto y valores extraídos del cliente y contrato.
     */
    private Map<String, String> crearMapaCampos(Cliente cliente, Contrato contrato) {
        Map<String, String> campos = new HashMap<>();


        campos.put(NOMBRE_CAMPO, cliente.getNombre() != null ? cliente.getNombre() : "");
        campos.put(APELLIDOS_CAMPO, cliente.getApellido() != null ? cliente.getApellido() : "");
        campos.put(DNI_CAMPO, cliente.getDni() != null ? cliente.getDni() : "");
        campos.put(DIRECCION_CAMPO, cliente.getDireccion() != null ? cliente.getDireccion() : "");
        campos.put(TELEFONO_CAMPO, cliente.getTelefono() != null ? cliente.getTelefono() : "");
        campos.put(POBLACION_CAMPO, cliente.getPoblacion() != null ? cliente.getPoblacion() : "");


        String idPoliza = contrato.getIdPol();
        campos.put("idPoliza", idPoliza != null ? idPoliza : "");


        campos.put("idContrato", contrato.getIdContrato() != null ? contrato.getIdContrato() : "");


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaInicio = contrato.getFechaInicio() != null ? sdf.format(contrato.getFechaInicio()) : "";
        String fechaFinal = contrato.getFechaFinal() != null ? sdf.format(contrato.getFechaFinal()) : "";

        String fechaRescate = contrato.getFechaRescate() != null ? sdf.format(contrato.getFechaRescate()) : "";
        campos.put("fechaRescate", fechaRescate);

        campos.put("fechaInicial", fechaInicio);
        campos.put("fechaFinal", fechaFinal);


        Date fechaActual = new Date();
        String fechaActualStr = sdf.format(fechaActual);
        campos.put("fechaActual", fechaActualStr);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);
        calendar.add(Calendar.MONTH, 1); // Añadir un mes
        String fechaRenovacionStr = sdf.format(calendar.getTime());
        campos.put("fechaRenovacion", fechaRenovacionStr);


        RenovacionDAO renovacionDAO = new RenovacionDAO();
        Renovacion ultimaRenovacion = renovacionDAO.obtenerUltRenovPorIdContrato(contrato.getIdContrato());


        if (ultimaRenovacion != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String fechaFinRenovacion = ultimaRenovacion.getFechaFinRenovacion() != null
                    ? ultimaRenovacion.getFechaFinRenovacion().format(dtf)
                    : "";

            campos.put("fechaFinRenovacion", fechaFinRenovacion);
        } else {
            campos.put("fechaRenovacionRenovacion", "");
            campos.put("fechaFinRenovacion", "");
        }

        return campos;
    }

    /**
     * Genera una tabla de productos dentro de un documento Word a partir de una lista de productos y un contrato.
     * Inserta la tabla en la posición donde se encuentra el marcador {{tablaProductos}}.
     *
     * @param document  Documento Word  donde se insertará la tabla.
     * @param productos Lista de objetos Producto a mostrar en la tabla.
     * @param contrato  Objeto { Contrato asociado a los productos.
     */
    private void generarTablaProductos(XWPFDocument document, ArrayList<Producto> productos, Contrato contrato) {

        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            String paragraphText = paragraph.getText();
            if (paragraphText.contains("{{tablaProductos}}")) {

                for (XWPFRun run : paragraph.getRuns()) {
                    run.setText("", 0);
                }


                XWPFTable table = document.insertNewTbl(paragraph.getCTP().newCursor());


                table.setWidth("100%");

                // Establecer encabezados de columna
                XWPFTableRow headerRow = table.getRow(0);
                headerRow.getCell(0).setText("Descripción");
                headerRow.addNewTableCell().setText("Observaciones");
                headerRow.addNewTableCell().setText("Cantidad");
                headerRow.addNewTableCell().setText("Peso (g)");
                headerRow.addNewTableCell().setText("Precio/g (€)");
                headerRow.addNewTableCell().setText("Importe (€)");


                setTableColumnWidths(table);


                quitarBordesDeTabla(table);


                int totalCantidad = 0;
                double totalPeso = 0;
                double totalImporte = 0;


                for (Producto producto : productos) {
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(producto.getDescripcion() != null ? producto.getDescripcion() : "");

                    // Alinear los valores a la derecha
                    setCellTextCenterAligned(row.getCell(1), String.valueOf(producto.getObservaciones()));
                    setCellTextCenterAligned(row.getCell(2), String.valueOf(producto.getCantidad()));
                    setCellTextCenterAligned(row.getCell(3), String.valueOf(producto.getPeso()));
                    setCellTextCenterAligned(row.getCell(4), String.valueOf(producto.getPrecioGramo()));
                    setCellTextCenterAligned(row.getCell(5), String.valueOf(producto.getImporte()));


                    totalCantidad += producto.getCantidad();
                    totalPeso += producto.getPeso();
                    totalImporte += producto.getImporte();
                }
                setFontSizeForTable(table, 8);

                XWPFParagraph emptyParagraph = document.createParagraph();
                emptyParagraph.setSpacingAfter(0);



                    generarTablaTotales(document, totalCantidad, totalPeso, totalImporte, emptyParagraph.getCTP().newCursor(), obtenerContratoId(productos.get(0).getIdContrato()).getTipo());


                break;
            }
        }
    }

    /**
     * Genera una tabla de productos específica para contratos tipo "Pol" dentro de un documento Word.
     * Inserta la tabla en la posición donde se encuentra el marcador {{tablaProductos}}.
     *
     * @param document  Documento Word   donde se insertará la tabla.
     * @param productos Lista de objetos  Producto a mostrar en la tabla.
     * @param contrato  Objeto  Contrato asociado a los productos.
     */
    private void generarTablaProductosPol(XWPFDocument document, ArrayList<Producto> productos, Contrato contrato) {


        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            String paragraphText = paragraph.getText();
            if (paragraphText.contains("{{tablaProductos}}")) {

                for (XWPFRun run : paragraph.getRuns()) {
                    run.setText("", 0);
                }


                XWPFTable table = document.insertNewTbl(paragraph.getCTP().newCursor());


                table.setWidth("100%");


                XWPFTableRow headerRow = table.getRow(0);
                headerRow.getCell(0).setText("Descripción");
                headerRow.addNewTableCell().setText("Observaciones");
                headerRow.addNewTableCell().setText("Cantidad");
                headerRow.addNewTableCell().setText("Peso (g)");
                headerRow.addNewTableCell().setText("Precio/g (€)");
                headerRow.addNewTableCell().setText("Importe (€)");


                setTableColumnWidths(table);


                quitarBordesDeTabla(table);

                int totalCantidad = 0;
                double totalPeso = 0;
                double totalImporte = 0;


                for (Producto producto : productos) {
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(producto.getDescripcion() != null ? producto.getDescripcion() : "");


                    setCellTextCenterAligned(row.getCell(1), String.valueOf(producto.getObservaciones()));
                    setCellTextCenterAligned(row.getCell(2), String.valueOf(producto.getCantidad()));
                    setCellTextCenterAligned(row.getCell(3), String.valueOf(producto.getPeso()));
                    setCellTextCenterAligned(row.getCell(4), String.valueOf(producto.getPrecioGramo()));
                    setCellTextCenterAligned(row.getCell(5), String.valueOf(producto.getImporte()));


                    totalCantidad += producto.getCantidad();
                    totalPeso += producto.getPeso();
                    totalImporte += producto.getImporte();
                }
                setFontSizeForTable(table, 8);

                XWPFParagraph emptyParagraph = document.createParagraph();
                emptyParagraph.setSpacingAfter(0);


                break;
            }
        }
    }

    /**
     * Genera una tabla con los totales de productos, incluyendo cantidad, peso, y importe total.
     * Si el contrato es de tipo "empeño", incluye el importe de renovación.
     *
     * @param document           Documento Word donde se insertará la tabla.
     * @param totalCantidad      Total de piezas.
     * @param totalPeso          Peso total en gramos.
     * @param totalImporte       Importe total.
     * @param cursor             Cursor de posición donde se insertará la tabla.
     * @param tipoContrato       Tipo de contrato, usado para determinar si se muestra el importe de renovación.
     */

    private void generarTablaTotales(XWPFDocument document, int totalCantidad, double totalPeso, double totalImporte, XmlCursor cursor, String tipoContrato) {

        boolean esEmpeno = tipoContrato.equalsIgnoreCase("empeno");


        double totalImporteRescate = esEmpeno ? totalImporte * 0.10 : 0;


        XWPFTable tablaTotales = document.insertNewTbl(cursor);


        tablaTotales.setWidth("40%");
        tablaTotales.setTableAlignment(TableRowAlign.RIGHT);




        XWPFTableRow filaPiezas = tablaTotales.getRow(0);
        filaPiezas.getCell(0).setText("Total de piezas:");
        filaPiezas.addNewTableCell().setText(String.valueOf(totalCantidad));


        XWPFTableRow filaGramos = tablaTotales.createRow();
        filaGramos.getCell(0).setText("Total gramos:");
        filaGramos.getCell(1).setText(String.format("%.2f", totalPeso) + " g");


        if (esEmpeno) {
            XWPFTableRow filaImporteRescate = tablaTotales.createRow();
            filaImporteRescate.getCell(0).setText("Total renovación:");
            filaImporteRescate.getCell(1).setText(String.format("%.2f", totalImporteRescate ) + " €");
        }


        XWPFTableRow filaImporte = tablaTotales.createRow();
        filaImporte.getCell(0).setText("Total importe:");
        filaImporte.getCell(1).setText(String.format("%.2f", totalImporte+totalImporteRescate ) + " €");


        setTableColumnWidthsTotales(tablaTotales, esEmpeno);


        quitarBordesDeTabla(tablaTotales);
    }

    /**
     * Elimina los bordes de una tabla en un documento Word.
     *
     * @param table Tabla  a la que se le eliminarán los bordes.
     */
    // Método auxiliar para quitar los bordes de una tabla
    private void quitarBordesDeTabla(XWPFTable table) {
        table.getCTTbl().getTblPr().unsetTblBorders();
    }

    /**
     * Establece el tamaño de fuente para todo el contenido de una tabla.
     *
     * @param table     Tabla  cuyo texto será modificado.
     * @param fontSize  Tamaño de fuente en puntos.
     */
    private void setFontSizeForTable(XWPFTable table, int fontSize) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        run.setFontSize(fontSize); // Establece el tamaño de la fuente en puntos
                    }
                }
            }
        }
    }

    /**
     * Establece texto centrado dentro de una celda de una tabla.
     *
     * @param cell Celda  donde se establecerá el texto.
     * @param text Texto a insertar en la celda.
     */
    private void setCellTextCenterAligned(XWPFTableCell cell, String text) {

        cell.removeParagraph(0);


        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER); // Alinear al centro


        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }

    /**
     * Ajusta los anchos de las columnas de una tabla de productos.
     *
     * @param table Tabla  a la que se le ajustarán los anchos de columna.
     */
    private void setTableColumnWidths(XWPFTable table) {

        int[] colWidths = {5000,3000, 1500, 1500, 1500, 1500};
        int numCols = table.getRow(0).getTableCells().size();

        for (int i = 0; i < numCols; i++) {
            for (XWPFTableRow row : table.getRows()) {
                XWPFTableCell cell = row.getCell(i);
                CTTc ctTc = cell.getCTTc();
                CTTcPr ctTcPr = (ctTc.isSetTcPr()) ? ctTc.getTcPr() : ctTc.addNewTcPr();
                CTTblWidth cellWidth = ctTcPr.isSetTcW() ? ctTcPr.getTcW() : ctTcPr.addNewTcW();
                cellWidth.setW(BigInteger.valueOf(colWidths[i]));
                cellWidth.setType(STTblWidth.DXA);
            }
        }
    }

    /**
     * Ajusta los anchos de las columnas de una tabla de totales en un documento Word.
     *
     * @param table     Tabla  a la que se le ajustarán los anchos de columna.
     * @param esEmpeno  Indica si se trata de un contrato de empeño. Afecta el número y ancho de columnas.
     */
    private void setTableColumnWidthsTotales(XWPFTable table, boolean esEmpeno) {

        int[] colWidths = esEmpeno ? new int[]{3000, 3000, 3000, 3000} : new int[]{3000, 3000, 4000};
        int numCols = table.getRow(0).getTableCells().size();

        for (int i = 0; i < numCols; i++) {
            for (XWPFTableRow row : table.getRows()) {
                XWPFTableCell cell = row.getCell(i);
                CTTc ctTc = cell.getCTTc();
                CTTcPr ctTcPr = (ctTc.isSetTcPr()) ? ctTc.getTcPr() : ctTc.addNewTcPr();
                CTTblWidth cellWidth = ctTcPr.isSetTcW() ? ctTcPr.getTcW() : ctTcPr.addNewTcW();
                cellWidth.setW(BigInteger.valueOf(colWidths[i]));
                cellWidth.setType(STTblWidth.DXA);
            }
        }
    }


    /**
     * Reemplaza los campos (placeholders) en un documento Word con los valores proporcionados en el mapa.
     *
     * @param document Documento Word  en el que se realizará el reemplazo.
     * @param fields   Mapa con claves y valores a reemplazar dentro del texto (usando {{clave}}).
     */
    private void replaceText(XWPFDocument document, Map<String, String> fields) {

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, fields);
        }


        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, fields);
                    }
                }
            }
        }
    }

    /**
     * Reemplaza los campos dentro de los encabezados de un documento Word.
     *
     * @param document Documento Word  cuyos encabezados se van a procesar.
     * @param fields   Mapa con claves y valores para reemplazar en el texto del encabezado.
     */
    private void replaceInHeader(XWPFDocument document, Map<String, String> fields) {
        for (XWPFHeader header : document.getHeaderList()) {
            for (XWPFParagraph paragraph : header.getParagraphs()) {
                replaceInParagraph(paragraph, fields);
            }
        }
    }



    /**
     * Reemplaza los campos dentro de un párrafo específico con los valores del mapa.
     *
     * @param paragraph Párrafo  en el que se realizará el reemplazo de texto.
     * @param fields    Mapa con claves y valores para sustituir dentro del texto (usando {{clave}}).
     */
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> fields) {
        String paragraphText = paragraph.getText();

        if (paragraphText != null) {

            for (Map.Entry<String, String> entry : fields.entrySet()) {
                paragraphText = paragraphText.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }


            for (int i = 0; i < paragraph.getRuns().size(); i++) {
                paragraph.getRuns().get(i).setText("", 0);
            }


            XWPFRun run = paragraph.createRun();
            run.setText(paragraphText);
        }
    }

    /**
     * Abre un documento Word utilizando la aplicación predeterminada del sistema operativo.
     *
     * @param rutaArchivo Ruta absoluta al archivo que se desea abrir.
     */
    private void abrirDocumentoWord(String rutaArchivo) {
        try {

            if (Desktop.isDesktopSupported()) {
                File archivo = new File(rutaArchivo);
                Desktop desktop = Desktop.getDesktop();


                if (archivo.exists()) {
                    desktop.open(archivo);
                } else {
                    System.err.println("El archivo no existe: " + rutaArchivo);
                }
            } else {
                System.err.println("La funcionalidad Desktop no está soportada.");
            }
        } catch (IOException e) {
            System.err.println("Error al intentar abrir el archivo: " + rutaArchivo);
            e.printStackTrace();
        }
    }


    /**
     * Genera un documento de rescate a partir de una plantilla y lo guarda en el sistema de archivos.
     * Si el contrato incluye póliza, también se genera un documento adicional con dicha póliza.
     *
     * @param dni         DNI del cliente.
     * @param idContrato  Identificador del contrato.
     * @param productos   Lista de productos asociados al contrato.
     * @throws URISyntaxException Si ocurre un error al acceder a los recursos internos.
     */

    public void rescatarContrato(String dni, String idContrato, ArrayList<Producto> productos) throws URISyntaxException {
        Cliente cliente = obtenerCliente(dni);
        Contrato contrato = obtenerContratoId(idContrato);
        String outputFilePath = obtenerRutaSalidaRescatar(dni, idContrato);
        boolean tienePoliza = contrato.getIdPol() != null;

        if (cliente != null && contrato != null) {
            try {

                if (tienePoliza) {

                    try (InputStream fis = getClass().getResourceAsStream(PLANTILLA_RESCTE)) {
                        if (fis == null) {
                            System.err.println("No se pudo encontrar el archivo de plantilla para rescate.");
                            return;
                        }
                        generarDocumento(cliente, fis, outputFilePath, contrato, productos);
                    }

                    String outputPolizaPath = obtenerRutaSalidaPolizaRescate(dni, idContrato);
                    try (InputStream fisPoliza = getClass().getResourceAsStream(PLANTILLA_RESCTE_POL)) {
                        if (fisPoliza == null) {
                            System.err.println("No se pudo encontrar el archivo de plantilla para póliza de rescate.");
                            return;
                        }
                        generarDocumentoPol(cliente, fisPoliza, outputPolizaPath, contrato, productos);
                    }


                    abrirDocumentoWord(outputFilePath);
                    abrirDocumentoWord(outputPolizaPath);
                } else {

                    try (InputStream fis = getClass().getResourceAsStream(PLANTILLA_RESCTE)) {
                        if (fis == null) {
                            System.err.println("No se pudo encontrar el archivo de plantilla para rescate.");
                            return;
                        }
                        generarDocumento(cliente, fis, outputFilePath, contrato, productos);
                    }

                    abrirDocumentoWord(outputFilePath);
                }
            } catch (Exception e) {
                System.err.println("Error al generar el documento de rescate para el cliente con DNI: " + dni);
                e.printStackTrace();
            }
        } else {
            System.out.println("Cliente con DNI " + dni + " o contrato no encontrado.");
        }
    }


    /**
     * Genera un documento de renovación de contrato y lo guarda en el sistema de archivos.
     * Si el contrato incluye póliza, también se genera el documento correspondiente a dicha póliza.
     *
     * @param dni         DNI del cliente.
     * @param idContrato  Identificador del contrato.
     * @param productos   Lista de productos asociados al contrato.
     * @throws URISyntaxException Si ocurre un error al acceder a los recursos internos.
     */
    public void renovarContrato(String dni, String idContrato, ArrayList<Producto> productos) throws URISyntaxException {
        Cliente cliente = obtenerCliente(dni);
        Contrato contrato = obtenerContratoId(idContrato);
        RenovacionDAO renovacionDAO = new RenovacionDAO();
        int versionUltimaRenovacion = renovacionDAO.obtenerUltimaVersionPorIdContrato(idContrato);
        String outputFilePath = obtenerRutaSalidaRenovar(dni, idContrato, versionUltimaRenovacion);
        boolean tienePoliza = contrato.getIdPol() != null;

        if (cliente != null && contrato != null) {
            try {

                if (tienePoliza) {

                    try (InputStream fis = getClass().getResourceAsStream(PLANTILLA_RENOVACION)) {
                        if (fis == null) {
                            System.err.println("No se pudo encontrar el archivo de plantilla para rescate.");
                            return;
                        }
                        generarDocumento(cliente, fis, outputFilePath, contrato, productos);
                    }

                    String outputPolizaPath = obtenerRutaSalidaPolizaRenovacion(dni, idContrato, versionUltimaRenovacion);
                    try (InputStream fisPoliza = getClass().getResourceAsStream(PLANTILLA_RENOVACION_POL)) {
                        if (fisPoliza == null) {
                            System.err.println("No se pudo encontrar el archivo de plantilla para póliza de rescate.");
                            return;
                        }
                        generarDocumentoPol(cliente, fisPoliza, outputPolizaPath, contrato, productos);
                    }


                    abrirDocumentoWord(outputFilePath);
                    abrirDocumentoWord(outputPolizaPath);
                } else {

                    try (InputStream fis = getClass().getResourceAsStream(PLANTILLA_RENOVACION)) {
                        if (fis == null) {
                            System.err.println("No se pudo encontrar el archivo de plantilla para rescate.");
                            return;
                        }
                        generarDocumento(cliente, fis, outputFilePath, contrato, productos);
                    }

                    abrirDocumentoWord(outputFilePath);
                }
            } catch (Exception e) {
                System.err.println("Error al generar el documento de rescate para el cliente con DNI: " + dni);
                e.printStackTrace();
            }
        } else {
            System.out.println("Cliente con DNI " + dni + " o contrato no encontrado.");
        }
    }




}
