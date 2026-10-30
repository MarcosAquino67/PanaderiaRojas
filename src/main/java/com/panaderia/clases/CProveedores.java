package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import com.panaderia.formularios.DialogProveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador de Proveedores.
 */
public class CProveedores {

    // -------------------------------------------------------------------------
    // VISTA PRINCIPAL
    // -------------------------------------------------------------------------

    public void mostrarProveedores(JTable tabla, String busqueda) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("ID");
        modelo.addColumn("Razón Social");
        modelo.addColumn("RUC/CI");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Dirección");

        String sql = "SELECT id_proveedor, razon_social, ruc_cedula, telefono, direccion FROM proveedor "
                   + "WHERE razon_social LIKE ? OR ruc_cedula LIKE ? ORDER BY razon_social";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busqueda + "%");
            ps.setString(2, "%" + busqueda + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id_proveedor"),
                        rs.getString("razon_social"),
                        rs.getString("ruc_cedula"),
                        rs.getString("telefono"),
                        rs.getString("direccion")
                    });
                }
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // ABM
    // -------------------------------------------------------------------------

    public boolean guardarProveedor(String razonSocial, String rucCedula, String telefono, String direccion, int idCiudad) {
        String sql = "INSERT INTO proveedor (razon_social, ruc_cedula, telefono, direccion, id_ciudad) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, razonSocial);
            ps.setString(2, rucCedula);
            ps.setString(3, telefono);
            ps.setString(4, direccion);
            ps.setInt(5, idCiudad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarProveedor(int idProveedor, String razonSocial, String rucCedula, String telefono, String direccion, int idCiudad) {
        String sql = "UPDATE proveedor SET razon_social = ?, ruc_cedula = ?, telefono = ?, direccion = ?, id_ciudad = ? "
                   + "WHERE id_proveedor = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, razonSocial);
            ps.setString(2, rucCedula);
            ps.setString(3, telefono);
            ps.setString(4, direccion);
            ps.setInt(5, idCiudad);
            ps.setInt(6, idProveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProveedor(int idProveedor) {
        String sql = "DELETE FROM proveedor WHERE id_proveedor = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // CONSULTA POR ID
    // -------------------------------------------------------------------------

    public Object[] obtenerProveedorPorId(int idProveedor) {
        String sql = "SELECT id_proveedor, razon_social, ruc_cedula, telefono, direccion, id_ciudad "
                   + "FROM proveedor WHERE id_proveedor = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_proveedor"),
                        rs.getString("razon_social"),
                        rs.getString("ruc_cedula"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getInt("id_ciudad")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar proveedor: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // ACCIONES DE LOS BOTONES (SOLO LLAMAN A ESTOS MÉTODOS)
    // -------------------------------------------------------------------------

    public void abrirNuevoProveedor(JTable tabla) {
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(tabla);
        DialogProveedor dlg = new DialogProveedor(padre, true);
        dlg.setVisible(true);
        mostrarProveedores(tabla, "");
    }

    public void seleccionarParaModificar(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(tabla, "Seleccione un proveedor de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idProveedor = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(tabla);
        DialogProveedor dlg = new DialogProveedor(padre, true, idProveedor);
        dlg.setVisible(true);
        mostrarProveedores(tabla, "");
    }

    public void seleccionarParaEliminar(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(tabla, "Seleccione un proveedor de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idProveedor = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
        String razonSocial = tabla.getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(tabla,
                "¿Eliminar al proveedor \"" + razonSocial + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (eliminarProveedor(idProveedor)) {
            JOptionPane.showMessageDialog(tabla, "Proveedor eliminado correctamente.");
        } else {
            JOptionPane.showMessageDialog(tabla, "No se pudo eliminar. Verifique que no tenga productos asociados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        mostrarProveedores(tabla, "");
    }
}