package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import com.panaderia.formularios.DlgBuscarCliente;
import com.panaderia.formularios.DlgBuscarProducto;
import com.panaderia.formularios.DlgConfigTicket;
import com.panaderia.formularios.DlgVuelto;
import com.panaderia.formularios.FormVentas;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Controlador de Lógica de Negocio para el Punto de Venta.
 *
 * @author Lilian
 */
public class CVentas {

    // -1 identifica al cliente por defecto "Consumidor Final" (se guarda NULL en BD)
    public static final int CLIENTE_CONSUMIDOR_FINAL = -1;
    private static final String TIPO_COMPROBANTE = "Boleta";
    /** Carpeta del proyecto donde se guardan los tickets generados. */
    private static final String CARPETA_TICKETS = "ticket generados";

    // Forma de pago EFECTIVO en la tabla forma_pago
    private static final int FORMA_PAGO_EFECTIVO = 1;

    // Estado del cliente seleccionado para la orden actual
    private int idClienteSeleccionado = CLIENTE_CONSUMIDOR_FINAL;
    private String nombreCliente = "Consumidor Final";
    private String rucCliente = "N/A";

    // Datos del cobro en efectivo de la última venta (se imprimen en el ticket)
    private double ultimoRecibido = 0;
    private double ultimoVuelto = 0;
    /** ID de la última venta guardada, se usa para nombrar el ticket PDF. */
    private int ultimoIdVenta = 0;

    private void reiniciarCliente() {
        this.idClienteSeleccionado = CLIENTE_CONSUMIDOR_FINAL;
        this.nombreCliente = "Consumidor Final";
        this.rucCliente = "N/A";
    }

    /**
     * Guarda la cabecera de la venta, el detalle con liquidación de IVA 5% y 10%
     * y descuenta el stock de los productos en una sola transacción atómica.
     */
    public boolean guardarVenta(int idCliente, int idUsuario, int idFormaPago, double total, JTable tablaVentas) {
        String sqlVenta = "INSERT INTO venta (fecha, tipo_comprobante, total_venta, id_cliente, id_usuario, id_forma_pago, id_config) "
                        + "VALUES (NOW(), ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal, iva_5, iva_10) "
                          + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ?";
        String sqlMovimiento = "INSERT INTO movimiento_stock (tipo, cantidad, observacion, id_producto, id_usuario) "
                             + "VALUES (?, ?, ?, ?, ?)";

        Connection con = null;

        try {
            con = CConexion.establecerConexion();
            con.setAutoCommit(false); // Iniciar transacción (Todo o nada)

            // 1. Inserción de la venta
            PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setString(1, TIPO_COMPROBANTE);
            psVenta.setDouble(2, total);

            if (idCliente > 0) {
                psVenta.setInt(3, idCliente);
            } else {
                psVenta.setNull(3, java.sql.Types.INTEGER); // Consumidor Final
            }

            psVenta.setInt(4, idUsuario);
            psVenta.setInt(5, idFormaPago);
            psVenta.setInt(6, 1); // Configuración global del ticket
            psVenta.executeUpdate();

            ResultSet rsKeys = psVenta.getGeneratedKeys();
            int idVentaGenerada = 0;
            if (rsKeys.next()) {
                idVentaGenerada = rsKeys.getInt(1);
                ultimoIdVenta = idVentaGenerada;
            }

            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
            PreparedStatement psStock = con.prepareStatement(sqlStock);
            PreparedStatement psMovimiento = con.prepareStatement(sqlMovimiento);

            // 2. Procesar cada fila de la tabla de ventas
            for (int i = 0; i < tablaVentas.getRowCount(); i++) {
                int idProducto = Integer.parseInt(tablaVentas.getValueAt(i, 0).toString());
                int cantidad = Integer.parseInt(tablaVentas.getValueAt(i, 2).toString());
                double precioUnitario = Double.parseDouble(tablaVentas.getValueAt(i, 3).toString().replaceAll("[^0-9]", ""));
                double subtotal = Double.parseDouble(tablaVentas.getValueAt(i, 4).toString().replaceAll("[^0-9]", ""));

                // Porcentaje de IVA de la fila (columna 6); si no está cargado, 10% por defecto
                int porcentajeIva = 10;
                if (tablaVentas.getColumnCount() > 6 && tablaVentas.getValueAt(i, 6) != null) {
                    try {
                        porcentajeIva = Integer.parseInt(tablaVentas.getValueAt(i, 6).toString());
                    } catch (NumberFormatException ignorado) {
                        // Si el valor no es numérico, se mantiene 10%
                    }
                }

                double iva5 = 0;
                double iva10 = 0;

                if (porcentajeIva == 10) {
                    iva10 = Math.round(subtotal / 11.0);
                } else if (porcentajeIva == 5) {
                    iva5 = Math.round(subtotal / 21.0);
                }

                // Inserción en detalle_venta
                psDetalle.setInt(1, idVentaGenerada);
                psDetalle.setInt(2, idProducto);
                psDetalle.setInt(3, cantidad);
                psDetalle.setDouble(4, precioUnitario);
                psDetalle.setDouble(5, subtotal);
                psDetalle.setDouble(6, iva5);
                psDetalle.setDouble(7, iva10);
                psDetalle.addBatch();

                // Actualización de stock
                psStock.setInt(1, cantidad);
                psStock.setInt(2, idProducto);
                psStock.addBatch();

                // Historial de stock: salida automática por la venta
                psMovimiento.setString(1, CMovimientosStock.SALIDA);
                psMovimiento.setInt(2, -cantidad);
                psMovimiento.setString(3, "Venta #" + idVentaGenerada);
                psMovimiento.setInt(4, idProducto);
                psMovimiento.setInt(5, idUsuario > 0 ? idUsuario : 1);
                psMovimiento.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();
            psMovimiento.executeBatch();

            con.commit(); // Confirmar transacción en MySQL
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // Deshacer cambios si ocurre un error
                } catch (SQLException ex) {
                    System.err.println("Error en Rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al procesar la venta: " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Obtiene el número correlativo para la siguiente orden de venta.
     */
    public int obtenerSiguienteNumeroVenta() {
        String sql = "SELECT MAX(id_venta) FROM venta";
        try (Connection con = CConexion.establecerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener correlativo: " + e.getMessage());
        }
        return 1;
    }

    /**
     * Abre el diálogo de búsqueda de clientes y, si se confirma una selección,
     * actualiza el cliente de la orden actual.
     */
    public void abrirBuscarCliente(FormVentas vista) {
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(vista);
        DlgBuscarCliente dlg = new DlgBuscarCliente(padre, true);
        dlg.setVisible(true);
        if (dlg.clienteConfirmado) {
            idClienteSeleccionado = dlg.idClienteSeleccionado;
            nombreCliente = dlg.nombreCliente;
            rucCliente = dlg.rucCliente;
            vista.mostrarCliente(nombreCliente, rucCliente);
        }
    }

    /**
     * Abre el diálogo de búsqueda de productos para agregar a la orden.
     */
    public void abrirBuscarProducto(FormVentas vista) {
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(vista);
        DlgBuscarProducto dlg = new DlgBuscarProducto(padre, true, vista);
        dlg.setVisible(true);
    }

    /**
     * Abre la ventana de configuración del ticket de venta.
     */
    public void abrirConfigTicket(FormVentas vista) {
        if (!Sesion.esAdmin()) {
            JOptionPane.showMessageDialog(vista,
                    "La configuración del ticket está disponible solo para administradores.",
                    "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(vista);
        DlgConfigTicket dlg = new DlgConfigTicket(padre, true);
        dlg.setVisible(true);
    }

    /**
     * Valida la orden, cobra la venta y limpia la orden si todo es correcto.
     */
    public void cobrarVenta(FormVentas vista) {
        if (vista.getTablaVentas().getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "Debe agregar productos a la orden.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idFormaPago = vista.getFormaPagoSeleccionada();
        double total = vista.getTotalParaCobrar();

        ultimoRecibido = 0;
        ultimoVuelto = 0;

        // En efectivo se pide el monto recibido para calcular el vuelto
        if (idFormaPago == FORMA_PAGO_EFECTIVO) {
            java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(vista);
            DlgVuelto dlgVuelto = new DlgVuelto(padre, true, total);
            dlgVuelto.setVisible(true);

            if (!dlgVuelto.isConfirmado()) {
                return; // El cajero canceló: la venta no se registra
            }

            ultimoRecibido = dlgVuelto.getMontoRecibido();
            ultimoVuelto = dlgVuelto.getVuelto();
        }

        boolean exito = guardarVenta(idClienteSeleccionado,
                Sesion.idUsuario > 0 ? Sesion.idUsuario : 1,
                idFormaPago, total, vista.getTablaVentas());

        if (exito) {
            imprimirTicket(vista, idFormaPago);
            reiniciarCliente();
            vista.limpiarVenta();
        } else {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error al registrar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Genera el ticket de la venta recién realizada como PDF y lo abre
     * automáticamente con el visor por defecto.
     */
    public void imprimirTicket(FormVentas vista, int idFormaPago) {
        try {
            Object[] cfg = new CConfiguracion().obtenerConfigTicket();
            String nombre = cfg != null && cfg[0] != null ? cfg[0].toString() : "PANADERIA ROJAS";
            String direccion = cfg != null && cfg[1] != null ? cfg[1].toString() : "";
            String telefono = cfg != null && cfg[2] != null ? cfg[2].toString() : "";
            String mensaje = cfg != null && cfg[3] != null ? cfg[3].toString() : "";

            String formaPago = idFormaPago == 3 ? "TARJETA" : (idFormaPago == 5 ? "QR / BILLETERA DIGITAL" : "EFECTIVO");
            int nroVenta = ultimoIdVenta > 0 ? ultimoIdVenta : obtenerSiguienteNumeroVenta() - 1;
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

            Document documento = new Document(new Rectangle(302, 620));
            File destino = ticketDestino(nroVenta);
            try (FileOutputStream fos = new FileOutputStream(destino)) {
                PdfWriter.getInstance(documento, fos);
                documento.open();

                Float left = 20f;
                Float right = 20f;
                Float top = 20f;
                Float bottom = 20f;
                Float ancho = 262f;

                Font fontNegra = FontFactory.getFont("Arial", 10, Font.NORMAL, new BaseColor(0, 0, 0));
                Font fontNegrita = FontFactory.getFont("Arial", 10, Font.BOLD, new BaseColor(0, 0, 0));
                Font fontTitulo = FontFactory.getFont("Arial", 14, Font.BOLD, new BaseColor(230, 126, 34));
                Font fontTotal = FontFactory.getFont("Arial", 13, Font.BOLD, new BaseColor(0, 0, 0));

                Paragraph p;

                p = new Paragraph(nombre, fontTitulo);
                p.setAlignment(Paragraph.ALIGN_CENTER);
                documento.add(p);
                if (!direccion.isEmpty()) {
                    p = new Paragraph(direccion, fontNegra);
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    documento.add(p);
                }
                if (!telefono.isEmpty()) {
                    p = new Paragraph("Tel: " + telefono, fontNegra);
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    documento.add(p);
                }
                documento.add(new Paragraph("==================================\n", fontNegrita));
                documento.add(new Paragraph("NRO VENTA:  " + nroVenta, fontNegra));
                documento.add(new Paragraph("FECHA:      " + fecha, fontNegra));
                documento.add(new Paragraph("CLIENTE:    " + nombreCliente, fontNegra));
                documento.add(new Paragraph("RUC/CI:     " + rucCliente, fontNegra));
                documento.add(new Paragraph("VENDEDOR:   " + Sesion.nombre, fontNegra));
                documento.add(new Paragraph("==================================\n", fontNegrita));

                JTable tabla = vista.getTablaVentas();
                PdfPTable detalle = new PdfPTable(3);
                detalle.setWidths(new float[]{3f, 1f, 2f});
                detalle.setWidthPercentage(100);
                detalle.addCell(celdaNegrita("DESCRIPCION", fontNegrita, ancho / 2f - 20f, 1));
                detalle.addCell(celdaNegrita("CANT", fontNegrita, 40f, 1));
                detalle.addCell(celdaNegrita("SUBTOTAL", fontNegrita, 60f, 1));
                for (int i = 0; i < tabla.getRowCount(); i++) {
                    String desc = String.valueOf(tabla.getValueAt(i, 1));
                    int cant = Integer.parseInt(tabla.getValueAt(i, 2).toString());
                    String sub = String.valueOf(tabla.getValueAt(i, 4)).replaceAll("[^0-9]", "");
                    detalle.addCell(celda(desc, fontNegra, ancho / 2f - 20f, 2));
                    detalle.addCell(celda(String.valueOf(cant), fontNegra, 40f, 2));
                    detalle.addCell(celda(sub, fontNegra, 60f, 2));
                }
                documento.add(detalle);

                p = new Paragraph("==================================\n", fontNegrita);
                documento.add(p);
                p = new Paragraph("TOTAL A PAGAR:  " + String.format("%,.0f", vista.getTotalParaCobrar()), fontTotal);
                p.setAlignment(Paragraph.ALIGN_CENTER);
                documento.add(p);
                p = new Paragraph("FORMA DE PAGO: " + formaPago, fontNegra);
                p.setAlignment(Paragraph.ALIGN_CENTER);
                documento.add(p);
                if (idFormaPago == FORMA_PAGO_EFECTIVO && ultimoRecibido > 0) {
                    p = new Paragraph("EFECTIVO RECIBIDO: " + String.format("%,.0f", ultimoRecibido), fontNegra);
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    documento.add(p);
                    p = new Paragraph("VUELTO: " + String.format("%,.0f", ultimoVuelto), fontNegrita);
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    documento.add(p);
                }
                documento.add(new Paragraph("==================================\n", fontNegrita));
                if (!mensaje.isEmpty()) {
                    p = new Paragraph(mensaje, fontNegra);
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    documento.add(p);
                }
                p = new Paragraph("¡Gracias por su compra!", fontNegrita);
                p.setAlignment(Paragraph.ALIGN_CENTER);
                documento.add(p);
                documento.close();
            }

            abrirPdf(destino);

        } catch (Exception e) {
            System.err.println("Error al generar el ticket PDF: " + e.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudo generar el ticket PDF:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private PdfPCell celda(String texto, Font font, Float ancho, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setBorder(0);
        celda.setPadding(2);
        celda.setHorizontalAlignment(alineacion);
        return celda;
    }

    private PdfPCell celdaNegrita(String texto, Font font, Float ancho, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setBorder(0);
        celda.setPadding(2);
        celda.setHorizontalAlignment(alineacion);
        return celda;
    }

    private File ticketDestino(int nroVenta) {
        // 1) Carpeta del proyecto: <proyecto>\ticket generados
        File carpeta = new File(System.getProperty("user.dir"), CARPETA_TICKETS);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            // 2) Respaldo: Documentos del usuario
            carpeta = new File(System.getProperty("user.home") + File.separator + "Documents", CARPETA_TICKETS);
            if (!carpeta.exists() && !carpeta.mkdirs()) {
                // 3) Último respaldo: carpeta temporal del sistema
                carpeta = new File(System.getProperty("java.io.tmpdir"), CARPETA_TICKETS);
                carpeta.mkdirs();
            }
        }
        return new File(carpeta, "Ticket_Venta_" + nroVenta + ".pdf");
    }

    /**
     * Abre el PDF generado con el visor por defecto del sistema.
     */
    private void abrirPdf(File archivo) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir el ticket PDF: " + e.getMessage());
        }
    }

    /**
     * Inicia una nueva orden de venta, confirmando si hay una en curso.
     */
    public void nuevaVenta(FormVentas vista) {
        if (vista.getTablaVentas().getRowCount() > 0) {
            int confirm = JOptionPane.showConfirmDialog(vista, "¿Desea cancelar la orden actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        reiniciarCliente();
        vista.limpiarVenta();
    }
}
