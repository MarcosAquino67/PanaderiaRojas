/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.panaderia.formularios;

import com.panaderia.clases.CMovimientosStock;
import java.awt.Font;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

/**
 * Registro de entradas, salidas y ajustes de stock de un producto
 * (JDialog modal).
 *
 * @author Lilian
 */
public class DlgAjustarStock extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgAjustarStock.class.getName());

    private int idProducto;
    private String nombreProducto;
    private int stockActual;

    private boolean stockAjustado = false;
    private boolean inicializado = false;

    private final CMovimientosStock daoMovimientos = new CMovimientosStock();
    private final DecimalFormat formatoPuntos;

    /**
     * Creates new form DlgAjustarStock
     */
    public DlgAjustarStock(Frame parent, boolean modal, int idProducto, String nombreProducto, int stockActual) {
        super(parent, modal);
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.stockActual = stockActual;

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.GERMAN);
        this.formatoPuntos = new DecimalFormat("#,##0", simbolos);

        initComponents();
        initDatos();
        setLocationRelativeTo(parent);
    }

    private void initDatos() {
        lblNombreProducto.setText(nombreProducto);
        lblStockActualVal.setText(formatoPuntos.format(stockActual) + " unidades");

        cargarTipos();
        cargarMotivos();
        sincronizarSpinnerConTipo();
        spiCantidad.addChangeListener(e -> actualizarVistaPrevia());

        inicializado = true;
        actualizarVistaPrevia();

        getRootPane().setDefaultButton(btnGuardar);
    }

    private void cargarTipos() {
        cbxTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Entrada  (ingreso de mercadería)",
            "Salida   (merma, consumo, egreso)",
            "Ajuste   (inventario físico contado)"
        }));
    }

    private void cargarMotivos() {
        String[] motivos;
        switch (tipoSeleccionado()) {
            case CMovimientosStock.ENTRADA:
                motivos = new String[]{
                    "Compra a proveedor",
                    "Ingreso de producción / lote",
                    "Devolución de cliente",
                    "Otro motivo"
                };
                break;
            case CMovimientosStock.SALIDA:
                motivos = new String[]{
                    "Merma / pan vencido",
                    "Materia prima dañada",
                    "Consumo interno",
                    "Otro motivo"
                };
                break;
            default:
                motivos = new String[]{
                    "Inventario físico / arqueo",
                    "Corrección de error de carga",
                    "Otro motivo"
                };
                break;
        }
        cbxMotivo.setModel(new javax.swing.DefaultComboBoxModel<>(motivos));
    }

    private void configurarSpinner(int valorInicial, int minimo) {
        SpinnerNumberModel model = new SpinnerNumberModel(valorInicial, minimo, 1000000, 1);
        spiCantidad.setModel(model);

        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spiCantidad, "#,##0");
        JFormattedTextField ftf = editor.getTextField();
        if (ftf.getFormatter() instanceof NumberFormatter) {
            NumberFormatter formatter = (NumberFormatter) ftf.getFormatter();
            formatter.setFormat(formatoPuntos);
            formatter.setAllowsInvalid(true);
        }

        spiCantidad.setEditor(editor);
        ftf.setFont(new Font("Segoe UI", Font.BOLD, 15));
        ftf.setHorizontalAlignment(JTextField.RIGHT);
    }

    /**
     * Al cambiar el tipo se reinicia la cantidad: en un ajuste se parte del
     * stock actual, en una entrada o salida se parte de 1 unidad.
     */
    private void sincronizarSpinnerConTipo() {
        if (CMovimientosStock.AJUSTE.equals(tipoSeleccionado())) {
            configurarSpinner(stockActual, 0);
        } else {
            configurarSpinner(1, 1);
        }
    }

    private String tipoSeleccionado() {
        switch (cbxTipo.getSelectedIndex()) {
            case 0:
                return CMovimientosStock.ENTRADA;
            case 1:
                return CMovimientosStock.SALIDA;
            default:
                return CMovimientosStock.AJUSTE;
        }
    }

    private int cantidadActual() {
        Object valor = spiCantidad.getValue();
        return valor == null ? 0 : ((Number) valor).intValue();
    }

    private void actualizarVistaPrevia() {
        if (lblResultadoVal == null) {
            return;
        }

        int cantidad = cantidadActual();
        int resultado;

        switch (tipoSeleccionado()) {
            case CMovimientosStock.ENTRADA:
                resultado = stockActual + cantidad;
                lblResultado.setText("Stock Resultante:");
                break;
            case CMovimientosStock.SALIDA:
                resultado = stockActual - cantidad;
                lblResultado.setText("Stock Resultante:");
                break;
            default:
                resultado = cantidad;
                lblResultado.setText("Nuevo Stock Físico:");
                break;
        }

        if (resultado < 0) {
            lblResultadoVal.setText("Insuficiente (disponible: " + formatoPuntos.format(stockActual) + ")");
            lblResultadoVal.setForeground(new java.awt.Color(217, 83, 79));
        } else {
            lblResultadoVal.setText(formatoPuntos.format(resultado) + " unidades");
            lblResultadoVal.setForeground(new java.awt.Color(230, 126, 34));
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
        lblProducto = new javax.swing.JLabel();
        lblNombreProducto = new javax.swing.JLabel();
        lblStockActual = new javax.swing.JLabel();
        lblStockActualVal = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        cbxTipo = new javax.swing.JComboBox();
        lblCantidad = new javax.swing.JLabel();
        spiCantidad = new javax.swing.JSpinner();
        lblResultado = new javax.swing.JLabel();
        lblResultadoVal = new javax.swing.JLabel();
        lblMotivo = new javax.swing.JLabel();
        cbxMotivo = new javax.swing.JComboBox();
        btnCancelar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("Movimiento de Stock");

        jPanelFondo.setBackground(new java.awt.Color(255, 248, 240));
        jPanelFondo.setPreferredSize(new java.awt.Dimension(460, 400));
        jPanelFondo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(92, 61, 46));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Entrada / Salida de Stock");
        jPanelFondo.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 15, 420, 32));

        lblProducto.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblProducto.setForeground(new java.awt.Color(92, 61, 46));
        lblProducto.setText("Producto:");
        jPanelFondo.add(lblProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 120, 28));

        lblNombreProducto.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNombreProducto.setForeground(new java.awt.Color(92, 61, 46));
        jPanelFondo.add(lblNombreProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, 290, 28));

        lblStockActual.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblStockActual.setForeground(new java.awt.Color(92, 61, 46));
        lblStockActual.setText("Stock Actual:");
        jPanelFondo.add(lblStockActual, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 120, 28));

        lblStockActualVal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStockActualVal.setForeground(new java.awt.Color(230, 126, 34));
        jPanelFondo.add(lblStockActualVal, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, 290, 28));

        lblTipo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTipo.setForeground(new java.awt.Color(92, 61, 46));
        lblTipo.setText("Movimiento:");
        jPanelFondo.add(lblTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 145, 120, 28));

        cbxTipo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cbxTipo.addActionListener(this::cbxTipoActionPerformed);
        jPanelFondo.add(cbxTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 145, 290, 32));

        lblCantidad.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblCantidad.setForeground(new java.awt.Color(92, 61, 46));
        lblCantidad.setText("Cantidad:");
        jPanelFondo.add(lblCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 120, 28));
        jPanelFondo.add(spiCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 190, 150, 32));

        lblResultado.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblResultado.setForeground(new java.awt.Color(92, 61, 46));
        lblResultado.setText("Stock Resultante:");
        jPanelFondo.add(lblResultado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 235, 120, 28));

        lblResultadoVal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblResultadoVal.setForeground(new java.awt.Color(230, 126, 34));
        jPanelFondo.add(lblResultadoVal, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 235, 290, 28));

        lblMotivo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblMotivo.setForeground(new java.awt.Color(92, 61, 46));
        lblMotivo.setText("Motivo:");
        jPanelFondo.add(lblMotivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 120, 28));

        cbxMotivo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanelFondo.add(cbxMotivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 280, 290, 32));

        btnCancelar.setBackground(new java.awt.Color(217, 136, 128));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        jPanelFondo.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 340, 140, 35));

        btnGuardar.setBackground(new java.awt.Color(230, 126, 34));
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Registrar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        jPanelFondo.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 340, 140, 35));

        getContentPane().add(jPanelFondo, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbxTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxTipoActionPerformed
        if (!inicializado) {
            return;
        }
        cargarMotivos();
        sincronizarSpinnerConTipo();
        actualizarVistaPrevia();
    }//GEN-LAST:event_cbxTipoActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        registrarMovimiento();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void registrarMovimiento() {
        String tipo = tipoSeleccionado();
        int cantidad = cantidadActual();
        String motivo = cbxMotivo.getSelectedItem() == null ? "" : cbxMotivo.getSelectedItem().toString();

        if (CMovimientosStock.ENTRADA.equals(tipo) && cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad mayor a cero.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (CMovimientosStock.SALIDA.equals(tipo)) {
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Ingrese una cantidad mayor a cero.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cantidad > stockActual) {
                JOptionPane.showMessageDialog(this,
                        "No hay stock suficiente.\nDisponible: " + stockActual + " unidades.",
                        "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (CMovimientosStock.AJUSTE.equals(tipo) && cantidad == stockActual) {
            JOptionPane.showMessageDialog(this,
                    "El stock contado es igual al actual: no hay ajuste que registrar.",
                    "Sin Cambios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (motivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un motivo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (daoMovimientos.registrarMovimiento(idProducto, tipo, cantidad, motivo)) {
            int stockNuevo = daoMovimientos.obtenerStock(idProducto);

            String etiqueta;
            switch (tipo) {
                case CMovimientosStock.ENTRADA:
                    etiqueta = "Entrada registrada";
                    break;
                case CMovimientosStock.SALIDA:
                    etiqueta = "Salida registrada";
                    break;
                default:
                    etiqueta = "Ajuste registrado";
                    break;
            }

            JOptionPane.showMessageDialog(this,
                    etiqueta + " correctamente.\n\n"
                    + "Producto: " + nombreProducto + "\n"
                    + "Stock anterior: " + formatoPuntos.format(stockActual) + "\n"
                    + "Stock actual: " + formatoPuntos.format(stockNuevo) + "\n"
                    + "Motivo: " + motivo,
                    "Movimiento Guardado", JOptionPane.INFORMATION_MESSAGE);

            stockAjustado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el movimiento.\n" + daoMovimientos.getUltimoError(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isStockAjustado() {
        return stockAjustado;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox cbxMotivo;
    private javax.swing.JComboBox cbxTipo;
    private javax.swing.JPanel jPanelFondo;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblNombreProducto;
    private javax.swing.JLabel lblProducto;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblResultadoVal;
    private javax.swing.JLabel lblStockActual;
    private javax.swing.JLabel lblStockActualVal;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JSpinner spiCantidad;
    // End of variables declaration//GEN-END:variables
}
