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
import com.panaderia.formularios.DialogCliente;
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
import javax.swing.table.DefaultTableModel;

/**
 * Controlador de Clientes.
 *
 * @author Lilian
 */
public class CClientes {

    // -------------------------------------------------------------------------
    // VISTA PRINCIPAL
    // -------------------------------------------------------------------------

    public void mostrarClientes(JTable tabla, String busqueda) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("ID");
        modelo.addColumn("Nombre / Razón Social");
        modelo.addColumn("RUC/CI");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Dirección");

        String sql = "SELECT id_cliente, nombre_razon_social, ruc_cedula, telefono, direccion FROM cliente "
                   + "WHERE nombre_razon_social LIKE ? OR ruc_cedula LIKE ? ORDER BY nombre_razon_social";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busqueda + "%");
            ps.setString(2, "%" + busqueda + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("nombre_razon_social"),
                        rs.getString("ruc_cedula"),
                        rs.getString("telefono"),
                        rs.getString("direccion")
                    });
                }
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
        }
    }

    /**
     * Carga la tabla de selección de clientes usada en Ventas. Incluye una
     * primera columna oculta con el ID y muestra la ciudad con su departamento.
     *
     * @param tabla    tabla destino
     * @param busqueda texto a filtrar por nombre o RUC/CI (puede ir vacío)
     */
    public void mostrarClientesParaSeleccion(JTable tabla, String busqueda) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("ID");
        modelo.addColumn("RUC/CI");
        modelo.addColumn("Nombre / Razón Social");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Ciudad");

        String sql = "SELECT c.id_cliente, c.ruc_cedula, c.nombre_razon_social, c.telefono, "
                   + "COALESCE(CONCAT(ci.nombre_ciudad, ' - ', d.nombre_departamento), '-') AS ubicacion "
                   + "FROM cliente c "
                   + "LEFT JOIN ciudad ci ON ci.id_ciudad = c.id_ciudad "
                   + "LEFT JOIN departamento d ON d.id_departamento = ci.id_departamento "
                   + "WHERE c.nombre_razon_social LIKE ? OR c.ruc_cedula LIKE ? "
                   + "ORDER BY c.nombre_razon_social";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String filtro = busqueda == null ? "" : busqueda.trim();
            ps.setString(1, "%" + filtro + "%");
            ps.setString(2, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("ruc_cedula"),
                        rs.getString("nombre_razon_social"),
                        rs.getString("telefono"),
                        rs.getString("ubicacion")
                    });
                }
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al cargar clientes para selección: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // ABM
    // -------------------------------------------------------------------------

    public boolean guardarCliente(String nombre, String rucCedula, String telefono, String direccion, int idCiudad) {
        String sql = "INSERT INTO cliente (nombre_razon_social, ruc_cedula, telefono, direccion, id_ciudad) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, rucCedula);
            ps.setString(3, telefono);
            ps.setString(4, direccion);
            ps.setInt(5, idCiudad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarCliente(int idCliente, String nombre, String rucCedula, String telefono, String direccion, int idCiudad) {
        String sql = "UPDATE cliente SET nombre_razon_social = ?, ruc_cedula = ?, telefono = ?, "
                   + "direccion = ?, id_ciudad = ? WHERE id_cliente = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, rucCedula);
            ps.setString(3, telefono);
            ps.setString(4, direccion);
            ps.setInt(5, idCiudad);
            ps.setInt(6, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCliente(int idCliente) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // CONSULTA POR ID
    // -------------------------------------------------------------------------

    public Object[] obtenerClientePorId(int idCliente) {
        String sql = "SELECT id_cliente, nombre_razon_social, ruc_cedula, telefono, direccion, id_ciudad "
                   + "FROM cliente WHERE id_cliente = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("nombre_razon_social"),
                        rs.getString("ruc_cedula"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getInt("id_ciudad")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar cliente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Devuelve el ID de un cliente a partir de su RUC/CI.
     *
     * @param rucCedula documento del cliente
     * @return ID encontrado o -1 si no existe
     */
    public int obtenerIdPorRuc(String rucCedula) {
        String sql = "SELECT id_cliente FROM cliente WHERE ruc_cedula = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rucCedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_cliente");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar ID de cliente: " + e.getMessage());
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // ACCIONES DE LOS BOTONES
    // -------------------------------------------------------------------------

    public void abrirNuevoCliente(JTable tabla) {
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(tabla);
        DialogCliente dlg = new DialogCliente(padre, true);
        dlg.setVisible(true);
        mostrarClientes(tabla, "");
    }

    public void seleccionarParaModificar(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(tabla, "Seleccione un cliente de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idCliente = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(tabla);
        DialogCliente dlg = new DialogCliente(padre, true, idCliente);
        dlg.setVisible(true);
        mostrarClientes(tabla, "");
    }

    public void seleccionarParaEliminar(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(tabla, "Seleccione un cliente de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idCliente = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
        String nombre = tabla.getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(tabla,
                "¿Eliminar al cliente \"" + nombre + "\"?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (eliminarCliente(idCliente)) {
            JOptionPane.showMessageDialog(tabla, "Cliente eliminado correctamente.");
        } else {
            JOptionPane.showMessageDialog(tabla,
                    "No se pudo eliminar.\nVerifique que el cliente no tenga ventas registradas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        mostrarClientes(tabla, "");
    }

    // -------------------------------------------------------------------------
    // REPORTE PDF
    // -------------------------------------------------------------------------

    /**
     * Exporta a PDF el listado de clientes que se está mostrando en la tabla.
     */
    public void exportarPdf(JTable tabla) {
        if (tabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(tabla, "No hay clientes para exportar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(tabla);

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Exportar Listado de Clientes a PDF");
        fc.setSelectedFile(new File("clientes_"
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

            documento.add(new Paragraph("PANADERIA ROJAS - LISTADO DE CLIENTES", tituloFont));
            documento.add(new Paragraph("Generado: "
                    + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    normalFont));
            documento.add(new Paragraph("Total de clientes: " + tabla.getRowCount(), normalFont));
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

            abrirPdf(destino);

            JOptionPane.showMessageDialog(padre,
                    "Reporte exportado correctamente:\n" + destino.getAbsolutePath(),
                    "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error al exportar PDF de clientes: " + e.getMessage());
            JOptionPane.showMessageDialog(padre,
                    "No se pudo exportar el PDF:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
