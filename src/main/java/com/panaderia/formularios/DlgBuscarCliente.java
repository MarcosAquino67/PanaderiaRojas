/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.panaderia.formularios;

import com.panaderia.clases.CClientes;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * Diálogo (JDialog modal) para elegir un cliente desde una tabla con buscador,
 * registrar uno nuevo o Continuar como Consumidor Final.
 *
 * @author Lilian
 */
public class DlgBuscarCliente extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgBuscarCliente.class.getName());

    private static final String PLACEHOLDER = "Buscar por nombre o RUC/CI...";
    private static final int COL_ID = 0;

    private final CClientes daoClientes = new CClientes();
    private String ultimoFiltro = null;

    public int idClienteSeleccionado = -1;
    public String nombreCliente = "Consumidor Final";
    public String rucCliente = "N/A";
    public boolean clienteConfirmado = false;

    /**
     * Creates new form DlgBuscarCliente
     */
    public DlgBuscarCliente(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configurarDialogo();
        setLocationRelativeTo(parent);
    }

    /**
     * Creates new form DlgBuscarCliente
     */
    public DlgBuscarCliente(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        configurarDialogo();
        setLocationRelativeTo(parent);
    }

    /* ---------------------------------------------------------------------
     * Lógica del diálogo (fuera del código generado por el diseñador)
     * ------------------------------------------------------------------ */

    private void configurarDialogo() {
        jScrollPane1.getViewport().setBackground(java.awt.Color.WHITE);
        configurarEncabezado();
        configurarTooltips();

        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (PLACEHOLDER.equals(txtBuscar.getText())) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(new java.awt.Color(92, 61, 46));
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setText(PLACEHOLDER);
                    txtBuscar.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String filtro = textoBusqueda();
                if (!filtro.equals(ultimoFiltro)) {
                    cargarClientes(filtro);
                }
            }
        });

        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablaClientes.getSelectedRow() >= 0) {
                    seleccionarCliente();
                }
            }
        });

        tablaClientes.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                actualizarEtiqueta();
            }
        });

        getRootPane().registerKeyboardAction(evt -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

        getRootPane().setDefaultButton(btnElegir);

        cargarClientes("");
        txtBuscar.requestFocusInWindow();
    }

    /**
     * Encabezado blanco con texto marrón, independiente del Look & Feel.
     */
    private void configurarEncabezado() {
        javax.swing.table.JTableHeader header = tablaClientes.getTableHeader();
        header.setBackground(java.awt.Color.WHITE);
        header.setForeground(new java.awt.Color(92, 61, 46));
        header.setFont(new java.awt.Font("Segoe UI", 1, 12));
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setBackground(java.awt.Color.WHITE);
                setForeground(new java.awt.Color(92, 61, 46));
                setFont(new java.awt.Font("Segoe UI", 1, 12));
                setBorder(javax.swing.BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new java.awt.Color(230, 126, 34)));
                setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                return this;
            }
        });
    }

    /**
     * Muestra el contenido completo de una celda como tooltip cuando no entra.
     */
    private void configurarTooltips() {
        tablaClientes.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof javax.swing.JLabel) {
                    ((javax.swing.JLabel) c).setToolTipText(value == null ? null : value.toString());
                }
                return c;
            }
        });
    }

    private String textoBusqueda() {
        String texto = txtBuscar.getText().trim();
        return PLACEHOLDER.equals(texto) ? "" : texto;
    }

    private void cargarClientes(String filtro) {
        String limpio = filtro == null ? "" : filtro.trim();
        ultimoFiltro = limpio;

        daoClientes.mostrarClientesParaSeleccion(tablaClientes, limpio);
        ocultarColumnaId();
        ajustarColumnas();

        if (tablaClientes.getRowCount() > 0) {
            tablaClientes.setRowSelectionInterval(0, 0);
        } else {
            lblSeleccionado.setText("Cliente seleccionado: —");
        }
    }

    /**
     * Vuelve a ocultar la columna ID después de cada recarga de la tabla
     * (setModel restablece el modelo de columnas).
     */
    private void ocultarColumnaId() {
        if (tablaClientes.getColumnModel().getColumnCount() > 4) {
            tablaClientes.removeColumn(tablaClientes.getColumnModel().getColumn(COL_ID));
        }
    }

    /**
     * Anchos pensados para que nombre y ciudad se lean completos.
     */
    private void ajustarColumnas() {
        int[] anchos = {110, 240, 100, 280};
        if (tablaClientes.getColumnModel().getColumnCount() < anchos.length) {
            return;
        }
        for (int i = 0; i < anchos.length; i++) {
            tablaClientes.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void actualizarEtiqueta() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            lblSeleccionado.setText("Cliente seleccionado: —");
            return;
        }
        int filaModelo = tablaClientes.convertRowIndexToModel(fila);
        Object nombre = tablaClientes.getModel().getValueAt(filaModelo, 2);
        Object ruc = tablaClientes.getModel().getValueAt(filaModelo, 1);
        lblSeleccionado.setText("Cliente seleccionado: " + nombre + "  ·  " + ruc);
    }

    private void seleccionarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccioná un cliente de la tabla.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tablaClientes.convertRowIndexToModel(fila);
        Object id = tablaClientes.getModel().getValueAt(filaModelo, 0);
        idClienteSeleccionado = id instanceof Number ? ((Number) id).intValue()
                : Integer.parseInt(String.valueOf(id));
        rucCliente = String.valueOf(tablaClientes.getModel().getValueAt(filaModelo, 1));
        nombreCliente = String.valueOf(tablaClientes.getModel().getValueAt(filaModelo, 2));
        clienteConfirmado = true;
        dispose();
    }

    private void consumidorFinal() {
        idClienteSeleccionado = -1;
        nombreCliente = "Consumidor Final";
        rucCliente = "N/A";
        clienteConfirmado = true;
        dispose();
    }

    private void nuevoCliente() {
        java.awt.Window ventana = javax.swing.SwingUtilities.getWindowAncestor(this);
        Frame padre = ventana instanceof Frame ? (Frame) ventana : null;

        DialogCliente dlg = new DialogCliente(padre, true);
        dlg.setVisible(true);

        int nuevoId = dlg.getIdClienteGuardado();
        txtBuscar.setText(PLACEHOLDER);
        txtBuscar.setForeground(new java.awt.Color(150, 150, 150));
        cargarClientes("");

        if (nuevoId > 0) {
            seleccionarPorId(nuevoId);
            txtBuscar.requestFocusInWindow();
        }
    }

    private void seleccionarPorId(int id) {
        for (int fila = 0; fila < tablaClientes.getRowCount(); fila++) {
            int filaModelo = tablaClientes.convertRowIndexToModel(fila);
            Object valor = tablaClientes.getModel().getValueAt(filaModelo, 0);
            if (valor instanceof Number && ((Number) valor).intValue() == id) {
                tablaClientes.setRowSelectionInterval(fila, fila);
                tablaClientes.scrollRectToVisible(tablaClientes.getCellRect(fila, 0, true));
                return;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelFondo = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnNuevoCliente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();
        lblSeleccionado = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        btnConsumidor = new javax.swing.JButton();
        btnElegir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("Seleccionar Cliente");

        jPanelFondo.setBackground(new java.awt.Color(255, 248, 240));
        jPanelFondo.setPreferredSize(new java.awt.Dimension(820, 560));
        jPanelFondo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(92, 61, 46));
        lblTitulo.setText("Seleccionar Cliente");
        jPanelFondo.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 14, 600, 30));

        lblSubtitulo.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSubtitulo.setForeground(new java.awt.Color(130, 110, 100));
        lblSubtitulo.setText("Hacé doble clic sobre un cliente o usá el botón Elegir.");
        jPanelFondo.add(lblSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 46, 600, 20));

        lblBuscar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblBuscar.setForeground(new java.awt.Color(92, 61, 46));
        lblBuscar.setText("Buscar:");
        jPanelFondo.add(lblBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 84, 65, 35));

        txtBuscar.setBackground(new java.awt.Color(255, 255, 255));
        txtBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(150, 150, 150));
        txtBuscar.setText("Buscar por nombre o RUC/CI...");
        jPanelFondo.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 84, 490, 35));

        btnNuevoCliente.setBackground(new java.awt.Color(230, 126, 34));
        btnNuevoCliente.setBorderPainted(false);
        btnNuevoCliente.setFocusPainted(false);
        btnNuevoCliente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNuevoCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoCliente.setText("+ Nuevo Cliente");
        btnNuevoCliente.addActionListener(this::btnNuevoClienteActionPerformed);
        jPanelFondo.add(btnNuevoCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 84, 160, 35));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setForeground(new java.awt.Color(92, 61, 46));

        tablaClientes.setBackground(new java.awt.Color(255, 255, 255));
        tablaClientes.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tablaClientes.setForeground(new java.awt.Color(92, 61, 46));
        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaClientes.setGridColor(new java.awt.Color(240, 230, 220));
        tablaClientes.setRowHeight(28);
        tablaClientes.setSelectionBackground(new java.awt.Color(230, 126, 34));
        tablaClientes.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(tablaClientes);

        jPanelFondo.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 132, 770, 320));

        lblSeleccionado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSeleccionado.setForeground(new java.awt.Color(92, 61, 46));
        lblSeleccionado.setText("Cliente seleccionado: —");
        jPanelFondo.add(lblSeleccionado, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 462, 740, 22));

        btnCancelar.setBackground(new java.awt.Color(217, 136, 128));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        jPanelFondo.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 495, 130, 42));

        btnConsumidor.setBackground(new java.awt.Color(141, 110, 99));
        btnConsumidor.setBorderPainted(false);
        btnConsumidor.setFocusPainted(false);
        btnConsumidor.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnConsumidor.setForeground(new java.awt.Color(255, 255, 255));
        btnConsumidor.setText("Consumidor Final");
        btnConsumidor.addActionListener(this::btnConsumidorActionPerformed);
        jPanelFondo.add(btnConsumidor, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 495, 175, 42));

        btnElegir.setBackground(new java.awt.Color(230, 126, 34));
        btnElegir.setBorderPainted(false);
        btnElegir.setFocusPainted(false);
        btnElegir.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnElegir.setForeground(new java.awt.Color(255, 255, 255));
        btnElegir.setText("Elegir Cliente");
        btnElegir.addActionListener(this::btnElegirActionPerformed);
        jPanelFondo.add(btnElegir, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 495, 185, 42));

        getContentPane().add(jPanelFondo, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnElegirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElegirActionPerformed
        seleccionarCliente();
    }//GEN-LAST:event_btnElegirActionPerformed

    private void btnConsumidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsumidorActionPerformed
        consumidorFinal();
    }//GEN-LAST:event_btnConsumidorActionPerformed

    private void btnNuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoClienteActionPerformed
        nuevoCliente();
    }//GEN-LAST:event_btnNuevoClienteActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

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
                DlgBuscarCliente dialog = new DlgBuscarCliente(new javax.swing.JFrame(), true);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConsumidor;
    private javax.swing.JButton btnElegir;
    private javax.swing.JButton btnNuevoCliente;
    private javax.swing.JPanel jPanelFondo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblSeleccionado;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
