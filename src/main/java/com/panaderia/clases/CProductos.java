package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import com.panaderia.formularios.DialogNuevoProducto;
import com.panaderia.formularios.DlgAjustarStock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class CProductos {

    // -------------------------------------------------------------------------
    // 1. OPERACIONES DE BASE DE DATOS (CRUD)
    // -------------------------------------------------------------------------

    // Guardar nuevo producto
    public boolean guardarProducto(String nombre, String descripcion, double precio, int stock, int idCategoria, int idProveedor, int porcentajeIva) {
        return guardarProducto(nombre, descripcion, precio, stock, 0, idCategoria, idProveedor, porcentajeIva);
    }

    // Guardar nuevo producto con stock mínimo
    public boolean guardarProducto(String nombre, String descripcion, double precio, int stock, int stockMinimo, int idCategoria, int idProveedor, int porcentajeIva) {
        String sql = "INSERT INTO producto (nombre, descripcion, precio, stock_actual, stock_minimo, id_categoria, id_proveedor, porcentaje_iva) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precio);
            ps.setInt(4, stock);
            ps.setInt(5, stockMinimo);
            ps.setInt(6, idCategoria);
            ps.setInt(7, idProveedor);
            ps.setInt(8, porcentajeIva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    // Actualizar producto existente
    public boolean modificarProducto(int idProducto, String nombre, String descripcion, double precio, int stock, int idCategoria, int idProveedor, int porcentajeIva) {
        return modificarProducto(idProducto, nombre, descripcion, precio, stock, 0, idCategoria, idProveedor, porcentajeIva);
    }

    // Actualizar producto existente con stock mínimo
    public boolean modificarProducto(int idProducto, String nombre, String descripcion, double precio, int stock, int stockMinimo, int idCategoria, int idProveedor, int porcentajeIva) {
        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, precio = ?, stock_actual = ?, stock_minimo = ?, id_categoria = ?, id_proveedor = ?, porcentaje_iva = ? WHERE id_producto = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precio);
            ps.setInt(4, stock);
            ps.setInt(5, stockMinimo);
            ps.setInt(6, idCategoria);
            ps.setInt(7, idProveedor);
            ps.setInt(8, porcentajeIva);
            ps.setInt(9, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar producto: " + e.getMessage());
            return false;
        }
    }

    // Actualizar stock
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE producto SET stock_actual = ? WHERE id_producto = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el stock: " + e.getMessage());
            return false;
        }
    }

    // Eliminar producto por ID
    public boolean eliminarProducto(int idProducto) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
            return false;
        }
    }

    // Consultar datos de un producto por ID
    public Object[] obtenerProductoPorId(int idProducto) {
        String sql = "SELECT id_producto, nombre, descripcion, precio, stock_actual, id_categoria, id_proveedor, porcentaje_iva, stock_minimo "
                   + "FROM producto WHERE id_producto = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("stock_actual"),
                        rs.getInt("id_categoria"),
                        rs.getInt("id_proveedor"),
                        rs.getInt("porcentaje_iva"),
                        rs.getInt("stock_minimo")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar producto: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // 2. VISTA PRINCIPAL (JTABLE) Y MÉTODOS DE CONTROL PARA LOS BOTONES
    // -------------------------------------------------------------------------

    // Cargar o buscar productos en la JTable de la vista principal
    public void mostrarProductos(JTable tabla, String busqueda) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Descripción");
        modelo.addColumn("Precio (Gs.)");
        modelo.addColumn("Stock");
        modelo.addColumn("Stock Mín.");
        modelo.addColumn("Estado");

        String sql = "SELECT id_producto, nombre, descripcion, precio, stock_actual, stock_minimo FROM producto "
                   + "WHERE nombre LIKE ? OR CAST(id_producto AS CHAR) LIKE ? ORDER BY id_producto DESC";

        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + busqueda + "%");
            ps.setString(2, "%" + busqueda + "%");

            try (ResultSet rs = ps.executeQuery()) {
                java.text.NumberFormat fmt = java.text.NumberFormat.getInstance(new java.util.Locale("es", "PY"));
                while (rs.next()) {
                    int stock = rs.getInt("stock_actual");
                    int stockMinimo = rs.getInt("stock_minimo");
                    modelo.addRow(new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        fmt.format(rs.getDouble("precio")),
                        stock,
                        stockMinimo,
                        stock <= stockMinimo ? "BAJO" : "OK"
                    });
                }
            }
            tabla.setModel(modelo);
            resaltarStockBajo(tabla);
        } catch (SQLException e) {
            System.err.println("Error al cargar la tabla de productos: " + e.getMessage());
        }
    }

    /**
     * Pinta en rojo la fila de los productos que están en o por debajo del
     * stock mínimo, para que el cajero los detecte de inmediato.
     */
    private void resaltarStockBajo(JTable tabla) {
        if (tabla.getColumnCount() < 7) {
            return;
        }
        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionado,
                    boolean tieneFoco, int fila, int columna) {
                java.awt.Component c = super.getTableCellRendererComponent(t, valor, seleccionado, tieneFoco, fila, columna);
                Object estado = t.getValueAt(fila, 6);
                if (!seleccionado && estado != null && "BAJO".equals(estado.toString())) {
                    c.setBackground(new java.awt.Color(255, 235, 232));
                    c.setForeground(new java.awt.Color(183, 28, 28));
                } else {
                    c.setBackground(java.awt.Color.WHITE);
                    c.setForeground(new java.awt.Color(92, 61, 46));
                }
                return c;
            }
        };
        for (int c = 0; c < tabla.getColumnCount(); c++) {
            tabla.getColumnModel().getColumn(c).setCellRenderer(renderer);
        }
    }

    // Abrir ventana para nuevo producto
    public void abrirNuevoProducto(JTable tabla) {
        JFrame padre = (JFrame) SwingUtilities.getWindowAncestor(tabla);
        DialogNuevoProducto dlg = new DialogNuevoProducto(padre, true);
        dlg.setVisible(true);
        mostrarProductos(tabla, "");
    }

    // Abrir ventana para Modificar Producto
    public void seleccionarParaModificar(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
        JFrame padre = (JFrame) SwingUtilities.getWindowAncestor(tabla);

        DialogNuevoProducto dlg = new DialogNuevoProducto(padre, true, idProducto);
        dlg.setVisible(true);
        mostrarProductos(tabla, "");
    }

    // Abrir ventana para Ajustar Stock
    public void seleccionarParaAjustarStock(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
        String nombre = tabla.getValueAt(fila, 1).toString();
        int stockActual = Integer.parseInt(tabla.getValueAt(fila, 4).toString().replaceAll("[^0-9]", ""));

        JFrame padre = (JFrame) SwingUtilities.getWindowAncestor(tabla);
        DlgAjustarStock dlg = new DlgAjustarStock(padre, true, idProducto, nombre, stockActual);
        dlg.setVisible(true);
        mostrarProductos(tabla, "");
    }

    // Ejecutar confirmación y borrado de Producto
    public void seleccionarParaEliminar(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
        String nombre = tabla.getValueAt(fila, 1).toString();

        int confirmacion = JOptionPane.showConfirmDialog(
            null,
            "¿Está seguro de eliminar el producto '" + nombre + "'?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (this.eliminarProducto(idProducto)) {
                JOptionPane.showMessageDialog(null, "Producto eliminado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar el producto de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            mostrarProductos(tabla, "");
        }
    }
}