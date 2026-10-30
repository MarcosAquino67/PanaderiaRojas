/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.panaderia.formularios;

import com.panaderia.clases.CProductos;
import com.panaderia.conexion.CConexion;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Lilian
 */
public class DialogNuevoProducto extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogNuevoProducto.class.getName());

    private int idProducto = -1;
    private final java.util.List<Integer> idsCategoria = new java.util.ArrayList<>();
    private final java.util.List<Integer> idsProveedor = new java.util.ArrayList<>();

    /**
     * Creates new form DialogNuevoProducto
     */
    public DialogNuevoProducto(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        btnGuardar.setContentAreaFilled(false);
          btnGuardar.setOpaque(true);
          btnCancelar.setContentAreaFilled(false);
          btnCancelar.setOpaque(true);
        initEstilosYComponentes();
    }

    /**
     * Constructor para modificar un producto existente.
     *
     * @param parent     El JFrame padre de esta ventana modal
     * @param modal      Indica si bloquea las ventanas de fondo
     * @param idProducto ID del producto a modificar
     */
    public DialogNuevoProducto(java.awt.Frame parent, boolean modal, int idProducto) {
        this(parent, modal);
        this.idProducto = idProducto;
        lblTitulo.setText("Modificar Producto");
        cargarDatosProducto(idProducto);
    }

    private void cargarDatosProducto(int id) {
        CProductos dao = new CProductos();
        Object[] producto = dao.obtenerProductoPorId(id);

        if (producto == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el producto seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        txtNombre.setText(producto[1] != null ? producto[1].toString() : "");
        txtDescripcion.setText(producto[2] != null ? producto[2].toString() : "");

        if (producto[3] != null) {
            long precio = Math.round((Double) producto[3]);
            txtPrecio.setText(java.text.NumberFormat.getInstance(new java.util.Locale("es", "PY")).format(precio));
        }

        txtStock.setText(producto[4] != null ? producto[4].toString() : "0");
        if (producto.length > 8 && producto[8] != null) {
            txtStockMinimo.setText(producto[8].toString());
        }
        seleccionarCombo(cbCategoria, idsCategoria, producto[5]);
        seleccionarCombo(cbProveedor, idsProveedor, producto[6]);

        if (producto.length > 7 && producto[7] != null) {
            int iva = ((Number) producto[7]).intValue();
            cbIva.setSelectedIndex(iva == 5 ? 1 : 0);
        }
    }

    private void seleccionarCombo(javax.swing.JComboBox<String> combo, java.util.List<Integer> ids, Object idObjeto) {
        if (idObjeto == null) {
            return;
        }
        int idBuscado = ((Number) idObjeto).intValue();
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i).intValue() == idBuscado) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void cargarCombos() {
        idsCategoria.clear();
        cbCategoria.removeAllItems();
        String sqlCategorias = "SELECT id_categoria, nombre_categoria FROM categoria ORDER BY nombre_categoria";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlCategorias);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idsCategoria.add(rs.getInt("id_categoria"));
                cbCategoria.addItem(rs.getString("nombre_categoria"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }

        idsProveedor.clear();
        cbProveedor.removeAllItems();
        String sqlProveedores = "SELECT id_proveedor, razon_social FROM proveedor ORDER BY razon_social";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlProveedores);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idsProveedor.add(rs.getInt("id_proveedor"));
                cbProveedor.addItem(rs.getString("razon_social"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }

    private int idCategoriaSeleccionada() {
        int idx = cbCategoria.getSelectedIndex();
        return (idx >= 0 && idx < idsCategoria.size()) ? idsCategoria.get(idx) : -1;
    }

    private int idProveedorSeleccionado() {
        int idx = cbProveedor.getSelectedIndex();
        return (idx >= 0 && idx < idsProveedor.size()) ? idsProveedor.get(idx) : -1;
    }
private void initEstilosYComponentes() {
    // 1. Configuración de la ventana modal centrada y tamaño fijo
    this.setSize(521, 840);
    this.setLocationRelativeTo(getOwner());
    this.setResizable(false);
    
    // 2. Medidas exactas que el GroupLayout respetará
    java.awt.Dimension dimCompleta = new java.awt.Dimension(372, 35);
    java.awt.Dimension dimMitad = new java.awt.Dimension(181, 35); // Dividen el espacio simétricamente

    txtNombre.setPreferredSize(dimCompleta);
    txtNombre.setMinimumSize(dimCompleta);
    
    txtDescripcion.setPreferredSize(new java.awt.Dimension(372, 70)); // Descripción más alta
    
    cbCategoria.setPreferredSize(dimCompleta);
    cbProveedor.setPreferredSize(dimCompleta);
    cbIva.setPreferredSize(dimCompleta);
    cbIva.setMinimumSize(dimCompleta);

    // Precio y Stock comparten la misma línea exactamente a la mitad cada uno
    txtPrecio.setPreferredSize(dimMitad);
    txtStock.setPreferredSize(dimMitad);

    // 3. Acción rápida para los botones Cancelar y Guardar
    btnCancelar.addActionListener(e -> dispose());
    btnGuardar.addActionListener(this::btnGuardarActionPerformed);
    
    panelTarjeta.revalidate();
    panelTarjeta.repaint();

    // 4. Aplicar restricciones y formato automático a Precio y Stock
    ((javax.swing.text.AbstractDocument) txtPrecio.getDocument()).setDocumentFilter(new FormatoPrecioFilter());
    ((javax.swing.text.AbstractDocument) txtStock.getDocument()).setDocumentFilter(new SoloNumerosFilter());
    ((javax.swing.text.AbstractDocument) txtStockMinimo.getDocument()).setDocumentFilter(new SoloNumerosFilter());

    // 5. Cargar categorías y proveedores desde la base de datos
    cargarCombos();
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelTarjeta = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbCategoria = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cbProveedor = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        txtStock = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtStockMinimo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(253, 251, 247));

        panelTarjeta.setBackground(new java.awt.Color(255, 248, 240));
        panelTarjeta.setPreferredSize(new java.awt.Dimension(420, 480));

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(92, 61, 46));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Nuevo Producto");

        txtNombre.setBackground(new java.awt.Color(255, 255, 255));
        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setPreferredSize(new java.awt.Dimension(240, 35));
        txtNombre.addActionListener(this::txtNombreActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(92, 61, 46));
        jLabel1.setText("Nombre del Producto");
        jLabel1.setPreferredSize(new java.awt.Dimension(120, 25));

        txtDescripcion.setBackground(new java.awt.Color(255, 255, 255));
        txtDescripcion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDescripcion.setForeground(new java.awt.Color(0, 0, 0));
        txtDescripcion.setPreferredSize(new java.awt.Dimension(240, 35));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(92, 61, 46));
        jLabel2.setText("Descripción");
        jLabel2.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(92, 61, 46));
        jLabel3.setText("Precio(Gs.)");
        jLabel3.setPreferredSize(new java.awt.Dimension(120, 25));

        txtPrecio.setBackground(new java.awt.Color(255, 255, 255));
        txtPrecio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPrecio.setForeground(new java.awt.Color(0, 0, 0));
        txtPrecio.setPreferredSize(new java.awt.Dimension(240, 35));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(92, 61, 46));
        jLabel6.setText("Stock Actual");
        jLabel6.setPreferredSize(new java.awt.Dimension(120, 25));

        cbCategoria.setBackground(new java.awt.Color(255, 255, 255));
        cbCategoria.setForeground(new java.awt.Color(0, 0, 0));
        cbCategoria.addActionListener(this::cbCategoriaActionPerformed);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(92, 61, 46));
        jLabel4.setText("Categoria");
        jLabel4.setPreferredSize(new java.awt.Dimension(120, 25));

        cbProveedor.setBackground(new java.awt.Color(255, 255, 255));
        cbProveedor.setForeground(new java.awt.Color(0, 0, 0));
        cbProveedor.addActionListener(this::cbProveedorActionPerformed);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(92, 61, 46));
        jLabel5.setText("Proveedor");
        jLabel5.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabel7 = new javax.swing.JLabel();
        cbIva = new javax.swing.JComboBox<>();

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(92, 61, 46));
        jLabel7.setText("IVA (%)");
        jLabel7.setPreferredSize(new java.awt.Dimension(120, 25));

        cbIva.setBackground(new java.awt.Color(255, 255, 255));
        cbIva.setForeground(new java.awt.Color(0, 0, 0));
        cbIva.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"10%", "5%"}));

        btnCancelar.setBackground(new java.awt.Color(217, 136, 128));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar");

        btnGuardar.setBackground(new java.awt.Color(230, 126, 34));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(0, 0, 0));
        btnGuardar.setText("Guardar");

        txtStock.setBackground(new java.awt.Color(255, 255, 255));
        txtStock.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtStock.setForeground(new java.awt.Color(0, 0, 0));
        txtStock.setPreferredSize(new java.awt.Dimension(240, 35));
        txtStock.addActionListener(this::txtStockActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(92, 61, 46));
        jLabel8.setText("Stock Mínimo");
        jLabel8.setPreferredSize(new java.awt.Dimension(120, 25));

        txtStockMinimo.setBackground(new java.awt.Color(255, 255, 255));
        txtStockMinimo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtStockMinimo.setForeground(new java.awt.Color(0, 0, 0));
        txtStockMinimo.setPreferredSize(new java.awt.Dimension(240, 35));

        javax.swing.GroupLayout panelTarjetaLayout = new javax.swing.GroupLayout(panelTarjeta);
        panelTarjeta.setLayout(panelTarjetaLayout);
        panelTarjetaLayout.setHorizontalGroup(
            panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTarjetaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelTarjetaLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTarjetaLayout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbIva, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtDescripcion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                        .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelTarjetaLayout.createSequentialGroup()
                        .addGroup(panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        panelTarjetaLayout.setVerticalGroup(
            panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTarjetaLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbIva, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelTarjetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnGuardar))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(panelTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(panelTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 710, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void cbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbCategoriaActionPerformed

    private void cbProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbProveedorActionPerformed

    private void txtStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String precioTexto = txtPrecio.getText().replaceAll("\\.", "").trim();
        String stockTexto = txtStock.getText().trim();
        String stockMinimoTexto = txtStockMinimo.getText().trim();

        if (nombre.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los campos obligatorios.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCategoria = idCategoriaSeleccionada();
        int idProveedor = idProveedorSeleccionado();
        int porcentajeIva = cbIva.getSelectedIndex() == 1 ? 5 : 10;

        if (idCategoria == -1 || idProveedor == -1) {
            JOptionPane.showMessageDialog(this, "Debe existir al menos una categoría y un proveedor registrados.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto);
            int stock = Integer.parseInt(stockTexto);
            int stockMinimo = stockMinimoTexto.isEmpty() ? 0 : Integer.parseInt(stockMinimoTexto);

            CProductos dao = new CProductos();
            boolean exito;

            if (idProducto == -1) {
                exito = dao.guardarProducto(nombre, descripcion, precio, stock, stockMinimo, idCategoria, idProveedor, porcentajeIva);
            } else {
                exito = dao.modificarProducto(idProducto, nombre, descripcion, precio, stock, stockMinimo, idCategoria, idProveedor, porcentajeIva);
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, "Operación realizada con éxito.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al procesar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos en Precio y Stock.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DialogNuevoProducto dialog = new DialogNuevoProducto(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Filtro para permitir solo números enteros (ideal para Stock)
class SoloNumerosFilter extends javax.swing.text.DocumentFilter {
    @Override
    public void insertString(javax.swing.text.DocumentFilter.FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
        if (string != null && string.matches("\\d+")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(javax.swing.text.DocumentFilter.FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
        if (text != null && text.matches("\\d+")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}

// Filtro con formato automático de puntos de miles para el Precio
class FormatoPrecioFilter extends javax.swing.text.DocumentFilter {
    @Override
    public void insertString(javax.swing.text.DocumentFilter.FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
        if (string != null && string.matches("\\d+")) {
            super.insertString(fb, offset, string, attr);
            formatear(fb);
        }
    }

    @Override
    public void replace(javax.swing.text.DocumentFilter.FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
        if (text != null && text.matches("\\d+")) {
            super.replace(fb, offset, length, text, attrs);
            formatear(fb);
        } else if (text != null && text.isEmpty()) {
            super.replace(fb, offset, length, text, attrs);
            formatear(fb);
        }
    }

    private void formatear(javax.swing.text.DocumentFilter.FilterBypass fb) throws javax.swing.text.BadLocationException {
        javax.swing.text.Document doc = fb.getDocument();
        String textoActual = doc.getText(0, doc.getLength()).replaceAll("\\.", "");
        if (!textoActual.isEmpty()) {
            try {
                long numero = Long.parseLong(textoActual);
                String formateado = java.text.NumberFormat.getInstance(new java.util.Locale("es", "PY")).format(numero);
                fb.replace(0, doc.getLength(), formateado, null);
            } catch (NumberFormatException ignored) {}
        }
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cbCategoria;
    private javax.swing.JComboBox<String> cbIva;
    private javax.swing.JComboBox<String> cbProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelTarjeta;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStockMinimo;
    // End of variables declaration//GEN-END:variables
}
