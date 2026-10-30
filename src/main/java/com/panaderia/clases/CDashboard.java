package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import com.panaderia.formularios.DlgCierreCaja;
import com.panaderia.formularios.FormDashboard;
import com.panaderia.formularios.FormProductos;
import com.panaderia.formularios.FormProveedores;
import com.panaderia.formularios.FormVentas;
import com.panaderia.formularios.MenuPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador del panel de inicio (dashboard): indicadores, últimas ventas
 * y accesos rápidos.
 */
public class CDashboard {

    private final java.text.NumberFormat fmtPY =
            java.text.NumberFormat.getInstance(new java.util.Locale("es", "PY"));

    /**
     * Carga los indicadores del dashboard: ventas de hoy, cantidad de
     * usuarios, cantidad de productos y las últimas ventas.
     */
    public void cargarIndicadores(FormDashboard vista) {
        vista.getLblTotalVentas().setText("Gs. " + fmtPY.format(obtenerVentasHoy()));
        vista.getLblCantUsuarios().setText(String.valueOf(contar("usuario")));
        vista.getLblCantProductos().setText(String.valueOf(contar("producto")));
        mostrarUltimasVentas(vista.getTablaRecientes());
    }

    /**
     * Acceso rápido: muestra el módulo de ventas.
     */
    public void abrirNuevaVenta(FormDashboard vista) {
        MenuPrincipal menu = (MenuPrincipal) SwingUtilities.getWindowAncestor(vista);
        if (menu != null) {
            menu.abrirPanelVentas();
        }
    }

    /**
     * Acceso rápido: muestra el módulo de productos.
     */
    public void abrirNuevoProducto(FormDashboard vista) {
        if (!Sesion.esAdmin()) {
            JOptionPane.showMessageDialog(vista,
                    "El módulo de Productos está disponible solo para administradores.",
                    "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MenuPrincipal menu = (MenuPrincipal) SwingUtilities.getWindowAncestor(vista);
        if (menu != null) {
            menu.mostrarPanel(new FormProductos());
        }
    }

    /**
     * Acceso rápido: muestra el módulo de proveedores.
     */
    public void abrirProveedores(FormDashboard vista) {
        if (!Sesion.esAdmin()) {
            JOptionPane.showMessageDialog(vista,
                    "El módulo de Proveedores está disponible solo para administradores.",
                    "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MenuPrincipal menu = (MenuPrincipal) SwingUtilities.getWindowAncestor(vista);
        if (menu != null) {
            menu.mostrarPanel(new FormProveedores());
        }
    }

    /**
     * Acceso rápido: abre el cierre de caja para terminar el día.
     */
    public void abrirCierreCaja(FormDashboard vista) {
        java.awt.Frame padre = (java.awt.Frame) SwingUtilities.getWindowAncestor(vista);
        DlgCierreCaja dlg = new DlgCierreCaja(padre, true);
        dlg.setVisible(true);
    }

    private double obtenerVentasHoy() {
        String sql = "SELECT COALESCE(SUM(total_venta), 0) AS total "
                   + "FROM venta WHERE DATE(fecha) = CURDATE()";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar ventas de hoy: " + e.getMessage());
        }
        return 0;
    }

    private int contar(String tabla) {
        String sql = "SELECT COUNT(*) AS total FROM " + tabla;
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar " + tabla + ": " + e.getMessage());
        }
        return 0;
    }

    private void mostrarUltimasVentas(JTable tabla) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modelo.addColumn("Fecha");
        modelo.addColumn("Cliente");
        modelo.addColumn("Usuario");
        modelo.addColumn("Total (Gs.)");

        String sql = "SELECT v.fecha, "
                   + "COALESCE(c.nombre_razon_social, 'CONSUMIDOR FINAL') AS cliente, "
                   + "COALESCE(u.nombre, '-') AS usuario, v.total_venta "
                   + "FROM venta v "
                   + "LEFT JOIN cliente c ON c.id_cliente = v.id_cliente "
                   + "LEFT JOIN usuario u ON u.id_usuario = v.id_usuario "
                   + "ORDER BY v.fecha DESC LIMIT 5";

        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getTimestamp("fecha").toLocalDateTime()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    rs.getString("cliente"),
                    rs.getString("usuario"),
                    fmtPY.format(rs.getDouble("total_venta"))
                });
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al cargar últimas ventas: " + e.getMessage());
        }
    }
}