package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import com.panaderia.formularios.FormReportes;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
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
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador de Reportes.
 */
public class CReportes {

    /**
     * Muestra las ventas realizadas entre dos fechas (formato yyyy-MM-dd).
     */
    public void mostrarVentas(JTable tabla, String desde, String hasta) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("Nro");
        modelo.addColumn("Fecha");
        modelo.addColumn("Cliente");
        modelo.addColumn("Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Subtotal (Gs.)");
        modelo.addColumn("Vendedor");

        String desdeSql = desde.isEmpty() ? "1000-01-01" : desde;
        String hastaSql = hasta.isEmpty() ? "9999-12-31" : hasta;

        String sql = "SELECT v.id_venta, v.fecha, p.nombre AS producto, d.cantidad, d.subtotal, "
                   + "COALESCE(c.nombre_razon_social, 'CONSUMIDOR FINAL') AS cliente, "
                   + "COALESCE(c.ruc_cedula, 'N/A') AS ruc, "
                   + "COALESCE(u.nombre, 'N/A') AS vendedor "
                   + "FROM detalle_venta d "
                   + "JOIN venta v ON v.id_venta = d.id_venta "
                   + "LEFT JOIN producto p ON p.id_producto = d.id_producto "
                   + "LEFT JOIN cliente c ON c.id_cliente = v.id_cliente "
                   + "LEFT JOIN usuario u ON u.id_usuario = v.id_usuario "
                   + "WHERE v.fecha >= ? AND v.fecha < DATE_ADD(?, INTERVAL 1 DAY) "
                   + "ORDER BY v.fecha DESC, v.id_venta DESC";

        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, desdeSql);
            ps.setString(2, hastaSql);
            java.text.NumberFormat fmt = java.text.NumberFormat.getInstance(new java.util.Locale("es", "PY"));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id_venta"),
                        rs.getTimestamp("fecha").toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        rs.getString("cliente") + " - " + rs.getString("ruc"),
                        rs.getString("producto"),
                        rs.getInt("cantidad"),
                        fmt.format(rs.getDouble("subtotal")),
                        rs.getString("vendedor")
                    });
                }
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al cargar reporte de ventas: " + e.getMessage());
        }
    }

    /**
     * Totales del rango: {subtotal, iva5, iva10}.
     */
    public double[] obtenerTotales(String desde, String hasta) {
        String sql = "SELECT COALESCE(SUM(d.subtotal), 0) AS subtotal, "
                   + "COALESCE(SUM(d.iva_5), 0) AS iva5, "
                   + "COALESCE(SUM(d.iva_10), 0) AS iva10 "
                   + "FROM detalle_venta d JOIN venta v ON v.id_venta = d.id_venta "
                   + "WHERE v.fecha >= ? AND v.fecha < DATE_ADD(?, INTERVAL 1 DAY)";
        double[] totales = {0, 0, 0};
        String desdeSql = desde.isEmpty() ? "1000-01-01" : desde;
        String hastaSql = hasta.isEmpty() ? "9999-12-31" : hasta;
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, desdeSql);
            ps.setString(2, hastaSql);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totales[0] = rs.getDouble("subtotal");
                    totales[1] = rs.getDouble("iva5");
                    totales[2] = rs.getDouble("iva10");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular totales del reporte: " + e.getMessage());
        }
        return totales;
    }

    /**
     * Muestra los productos cuyo stock está por debajo del mínimo.
     */
    public void mostrarStockBajo(JTable tabla) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("ID");
        modelo.addColumn("Producto");
        modelo.addColumn("Stock Actual");
        modelo.addColumn("Stock Mínimo");

        String sql = "SELECT id_producto, nombre, stock_actual, stock_minimo FROM producto "
                   + "WHERE stock_actual <= stock_minimo ORDER BY (stock_actual - stock_minimo)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getInt("stock_actual"),
                    rs.getInt("stock_minimo")
                });
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al cargar stock bajo: " + e.getMessage());
        }
    }

    /**
     * Devuelve el rango {desde, hasta} (yyyy-MM-dd) para un período. Si el
     * período es "Todo", ambas cadenas van vacías (rango sin límites).
     */
    public String[] calcularRango(String periodo) {
        LocalDate hoy = LocalDate.now();
        switch (periodo) {
            case "Ayer":
                return new String[]{hoy.minusDays(1).toString(), hoy.minusDays(1).toString()};
            case "Esta Semana":
                return new String[]{hoy.with(java.time.DayOfWeek.MONDAY).toString(), hoy.toString()};
            case "Semana Pasada": {
                LocalDate lunes = hoy.with(java.time.DayOfWeek.MONDAY).minusDays(7);
                return new String[]{lunes.toString(), lunes.plusDays(6).toString()};
            }
            case "Este Mes":
                return new String[]{hoy.withDayOfMonth(1).toString(), hoy.toString()};
            case "Mes Pasado": {
                LocalDate primero = hoy.withDayOfMonth(1).minusMonths(1);
                return new String[]{primero.toString(), primero.withDayOfMonth(primero.lengthOfMonth()).toString()};
            }
            case "Este Año":
                return new String[]{hoy.withDayOfYear(1).toString(), hoy.toString()};
            case "Año Pasado": {
                LocalDate enero = hoy.withDayOfYear(1).minusYears(1);
                return new String[]{enero.toString(), enero.withDayOfYear(enero.lengthOfYear()).toString()};
            }
            case "Todo":
                return new String[]{"", ""};
            default: // "Hoy"
                return new String[]{hoy.toString(), hoy.toString()};
        }
    }

    /**
     * Exporta la tabla de ventas actual a un PDF usando iText.
     */
    public void exportarPdf(FormReportes vista) {
        JTable tabla = vista.getTablaReportes();
        String desde = vista.getDesde();
        String hasta = vista.getHasta();
        String periodo = (String) vista.getComboPeriodo().getSelectedItem();
        String etiquetaRango = calcularRango(periodo)[0].isEmpty()
                ? "Todo el historial"
                : "del " + desde + " al " + hasta;

        java.awt.Frame padre = (java.awt.Frame) SwingUtilities.getWindowAncestor(vista);

        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setDialogTitle("Exportar Reporte de Ventas a PDF");
        fc.setSelectedFile(new File("reporte_ventas_"
                + new java.text.SimpleDateFormat("yyyyMMdd_HHmm").format(new java.util.Date()) + ".pdf"));
        if (fc.showSaveDialog(padre) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destino = fc.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
        }

        java.text.NumberFormat fmt = java.text.NumberFormat.getInstance(new java.util.Locale("es", "PY"));

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            Document documento = new Document();
            PdfWriter.getInstance(documento, fos);
            documento.open();

            Font tituloFont = FontFactory.getFont("Arial", 18, Font.BOLD, new BaseColor(230, 126, 34));
            Font normalFont = FontFactory.getFont("Arial", 10, Font.NORMAL, new BaseColor(60, 60, 60));
            Font headerFont = FontFactory.getFont("Arial", 11, Font.BOLD, BaseColor.WHITE);
            Font negritaFont = FontFactory.getFont("Arial", 11, Font.BOLD, new BaseColor(92, 61, 46));

            documento.add(new Paragraph("PANADERIA ROJAS - REPORTE DE VENTAS", tituloFont));
            documento.add(new Paragraph("Período: " + periodo + " (" + etiquetaRango + ")", normalFont));
            documento.add(new Paragraph("Generado: "
                    + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    normalFont));
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

            double[] totales = obtenerTotales(desde, hasta);
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("TOTALES DEL PERÍODO", negritaFont));
            documento.add(new Paragraph("Subtotal: ₲ " + fmt.format(totales[0]), normalFont));
            documento.add(new Paragraph("IVA 5%: ₲ " + fmt.format(totales[1]), normalFont));
            documento.add(new Paragraph("IVA 10%: ₲ " + fmt.format(totales[2]), normalFont));

            documento.close();

            abrirPdf(destino);

            JOptionPane.showMessageDialog(padre,
                    "Reporte exportado correctamente:\n" + destino.getAbsolutePath(),
                    "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error al exportar PDF: " + e.getMessage());
            JOptionPane.showMessageDialog(padre,
                    "No se pudo exportar el PDF:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre automáticamente el PDF generado con el visor por defecto del sistema.
     */
    private void abrirPdf(File archivo) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir el PDF automáticamente: " + e.getMessage());
        }
    }
}