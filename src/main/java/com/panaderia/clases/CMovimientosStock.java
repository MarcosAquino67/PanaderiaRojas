package com.panaderia.clases;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.panaderia.conexion.CConexion;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador de movimientos de stock (entradas, salidas y ajustes).
 *
 * <p>Cada movimiento actualiza el stock del producto y deja registro en la
 * tabla {@code movimiento_stock}, dentro de una misma transacción.</p>
 *
 * @author Lilian
 */
public class CMovimientosStock {

    public static final String ENTRADA = "ENTRADA";
    public static final String SALIDA = "SALIDA";
    public static final String AJUSTE = "AJUSTE";

    private String ultimoError = "";

    public String getUltimoError() {
        return ultimoError;
    }

    // -------------------------------------------------------------------------
    // REGISTRO DE MOVIMIENTOS
    // -------------------------------------------------------------------------

    /**
     * Registra un movimiento en su propia transacción.
     *
     * @param idProducto producto afectado
     * @param tipo ENTRADA, SALIDA o AJUSTE
     * @param cantidad unidades que ingresan / egresan. En AJUSTE es el nuevo
     *                 stock físico contado.
     * @param observacion motivo del movimiento
     * @return true si se registró correctamente
     */
    public boolean registrarMovimiento(int idProducto, String tipo, int cantidad, String observacion) {
        ultimoError = "";
        try (Connection con = CConexion.establecerConexion()) {
            con.setAutoCommit(false);
            try {
                registrarMovimiento(con, idProducto, tipo, cantidad, observacion);
                con.commit();
                return true;
            } catch (SQLException | IllegalArgumentException e) {
                con.rollback();
                ultimoError = e.getMessage();
                System.err.println("Error al registrar movimiento de stock: " + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.err.println("Error de conexión al registrar movimiento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra el movimiento dentro de una transacción ya abierta por el
     * llamador (por ejemplo, la venta completa).
     *
     * @throws SQLException si el producto no existe o el stock es insuficiente
     */
    public void registrarMovimiento(Connection con, int idProducto, String tipo,
                                    int cantidad, String observacion) throws SQLException {
        int stockActual = bloquearStock(con, idProducto);

        int nuevoStock;
        int variacion;

        switch (tipo) {
            case ENTRADA:
                if (cantidad <= 0) {
                    throw new IllegalArgumentException("La cantidad de entrada debe ser mayor a cero.");
                }
                nuevoStock = stockActual + cantidad;
                variacion = cantidad;
                break;
            case SALIDA:
                if (cantidad <= 0) {
                    throw new IllegalArgumentException("La cantidad de salida debe ser mayor a cero.");
                }
                if (cantidad > stockActual) {
                    throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stockActual
                            + " unidades, solicitado: " + cantidad + ".");
                }
                nuevoStock = stockActual - cantidad;
                variacion = -cantidad;
                break;
            case AJUSTE:
                if (cantidad < 0) {
                    throw new IllegalArgumentException("El stock físico no puede ser negativo.");
                }
                nuevoStock = cantidad;
                variacion = nuevoStock - stockActual;
                if (variacion == 0) {
                    throw new IllegalArgumentException("El stock contado es igual al actual: no hay ajuste que registrar.");
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento no válido: " + tipo);
        }

        String sqlUpdate = "UPDATE producto SET stock_actual = ? WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }

        String sqlInsert = "INSERT INTO movimiento_stock (tipo, cantidad, observacion, id_producto, id_usuario) "
                         + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
            ps.setString(1, tipo);
            ps.setInt(2, variacion);
            ps.setString(3, observacion);
            ps.setInt(4, idProducto);
            ps.setInt(5, Sesion.idUsuario > 0 ? Sesion.idUsuario : 1);
            ps.executeUpdate();
        }
    }

    private int bloquearStock(Connection con, int idProducto) throws SQLException {
        String sql = "SELECT stock_actual FROM producto WHERE id_producto = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("El producto seleccionado no existe.");
                }
                return rs.getInt("stock_actual");
            }
        }
    }

    /**
     * Stock actual de un producto (consulta puntual).
     */
    public int obtenerStock(int idProducto) {
        String sql = "SELECT stock_actual FROM producto WHERE id_producto = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock_actual");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar stock: " + e.getMessage());
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // HISTORIAL / REPORTE
    // -------------------------------------------------------------------------

    /**
     * Carga el historial de movimientos entre dos fechas (formato yyyy-MM-dd).
     */
    public void mostrarMovimientos(JTable tabla, String desde, String hasta) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("ID");
        modelo.addColumn("Fecha");
        modelo.addColumn("Tipo");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Producto");
        modelo.addColumn("Usuario");
        modelo.addColumn("Observación");

        String sql = "SELECT m.id_movimiento, m.fecha, m.tipo, m.cantidad, p.nombre AS producto, "
                   + "u.nombre AS usuario, m.observacion "
                   + "FROM movimiento_stock m "
                   + "INNER JOIN producto p ON p.id_producto = m.id_producto "
                   + "INNER JOIN usuario u ON u.id_usuario = m.id_usuario "
                   + "WHERE DATE(m.fecha) BETWEEN ? AND ? "
                   + "ORDER BY m.fecha DESC, m.id_movimiento DESC";

        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Timestamp fecha = rs.getTimestamp("fecha");
                    modelo.addRow(new Object[]{
                        rs.getInt("id_movimiento"),
                        fecha == null ? "" : fmt.format(fecha),
                        rs.getString("tipo"),
                        rs.getInt("cantidad"),
                        rs.getString("producto"),
                        rs.getString("usuario"),
                        rs.getString("observacion")
                    });
                }
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al cargar movimientos de stock: " + e.getMessage());
        }
    }

    /**
     * Exporta a PDF el historial que se está mostrando en la tabla.
     */
    public void exportarPdf(JTable tabla, String desde, String hasta) {
        if (tabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(tabla, "No hay movimientos para exportar.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.awt.Frame padre = (java.awt.Frame) SwingUtilities.getWindowAncestor(tabla);

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Exportar Movimientos de Stock a PDF");
        fc.setSelectedFile(new File("movimientos_stock_"
                + new java.text.SimpleDateFormat("yyyyMMdd_HHmm").format(new java.util.Date()) + ".pdf"));
        if (fc.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destino = fc.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
        }

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            Document documento = new Document();
            PdfWriter.getInstance(documento, fos);
            documento.open();

            Font tituloFont = FontFactory.getFont("Arial", 18, Font.BOLD, new BaseColor(230, 126, 34));
            Font normalFont = FontFactory.getFont("Arial", 10, Font.NORMAL, new BaseColor(60, 60, 60));
            Font headerFont = FontFactory.getFont("Arial", 11, Font.BOLD, BaseColor.WHITE);

            documento.add(new Paragraph("PANADERIA ROJAS - MOVIMIENTOS DE STOCK", tituloFont));
            documento.add(new Paragraph("Periodo: " + desde + " al " + hasta, normalFont));
            documento.add(new Paragraph("Generado: "
                    + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    normalFont));
            documento.add(new Paragraph("Total de movimientos: " + tabla.getRowCount(), normalFont));
            documento.add(new Paragraph(" "));

            PdfPTable tablaPdf = new PdfPTable(tabla.getColumnCount());
            tablaPdf.setWidthPercentage(100);

            for (int c = 0; c < tabla.getColumnCount(); c++) {
                PdfPCell celda = new PdfPCell(new Phrase(String.valueOf(tabla.getColumnName(c)), headerFont));
                celda.setBackgroundColor(new BaseColor(230, 126, 34));
                celda.setPadding(5);
                tablaPdf.addCell(celda);
            }

            for (int r = 0; r < tabla.getRowCount(); r++) {
                for (int c = 0; c < tabla.getColumnCount(); c++) {
                    Object valor = tabla.getValueAt(r, c);
                    PdfPCell celda = new PdfPCell(new Phrase(valor == null ? "" : valor.toString(), normalFont));
                    celda.setPadding(4);
                    tablaPdf.addCell(celda);
                }
            }
            documento.add(tablaPdf);
            documento.close();

            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(destino);
                } catch (Exception ignorado) {
                    // Si no se puede abrir, igual el PDF ya quedó guardado
                }
            }

            JOptionPane.showMessageDialog(padre,
                    "Reporte exportado correctamente:\n" + destino.getAbsolutePath(),
                    "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error al exportar PDF de movimientos: " + e.getMessage());
            JOptionPane.showMessageDialog(padre,
                    "No se pudo exportar el PDF:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}