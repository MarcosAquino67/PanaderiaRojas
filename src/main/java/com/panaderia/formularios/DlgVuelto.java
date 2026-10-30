/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.panaderia.formularios;

import java.awt.Frame;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Diálogo de cobro en efectivo: el cajero ingresa el dinero que le entregó el
 * cliente y el diálogo calcula el vuelto en tiempo real.
 *
 * @author Lilian
 */
public class DlgVuelto extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgVuelto.class.getName());

    private static final java.awt.Color COLOR_VUELTO = new java.awt.Color(39, 174, 96);
    private static final java.awt.Color COLOR_FALTA = new java.awt.Color(192, 57, 43);
    private static final java.awt.Color COLOR_NEUTRO = new java.awt.Color(96, 100, 102);

    private final NumberFormat fmtPY = NumberFormat.getInstance(new Locale("es", "PY"));

    private final double total;
    private boolean confirmado = false;
    private double recibido = 0;
    private double vuelto = 0;

    /**
     * Creates new form DlgVuelto
     *
     * @param parent ventana padre
     * @param modal  si bloquea la ventana de origen
     * @param total  monto total que debe pagar el cliente
     */
    public DlgVuelto(Frame parent, boolean modal, double total) {
        super(parent, modal);
        this.total = total;
        initComponents();
        initDatos();
        setLocationRelativeTo(parent);
    }

    private void initDatos() {
        lblTotal.setText("₲ " + fmtPY.format(total));
        txtRecibido.setHorizontalAlignment(JTextField.RIGHT);
        txtRecibido.setText(fmtPY.format(Math.round(total)));
        txtRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // No interferir con las teclas de navegación
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT
                        || evt.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT
                        || evt.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    return;
                }
                SwingUtilities.invokeLater(() -> reformatear());
            }
        });

        getRootPane().setDefaultButton(btnConfirmar);

        SwingUtilities.invokeLater(() -> {
            txtRecibido.requestFocusInWindow();
            txtRecibido.selectAll();
        });

        calcular();
    }

    /**
     * Da formato de miles al monto escrito y recalcula el vuelto.
     */
    private void reformatear() {
        String digitos = txtRecibido.getText().replaceAll("[^0-9]", "");
        if (digitos.length() > 15) {
            digitos = digitos.substring(0, 15);
        }
        String formateado = digitos.isEmpty() ? "" : fmtPY.format(Long.parseLong(digitos));
        if (!formateado.equals(txtRecibido.getText())) {
            txtRecibido.setText(formateado);
        }
        calcular();
    }

    /**
     * @return el monto escrito en el campo, sin separadores de miles.
     */
    private double leerRecibido() {
        String digitos = txtRecibido.getText().replaceAll("[^0-9]", "");
        if (digitos.isEmpty()) {
            return 0;
        }
        try {
            return Long.parseLong(digitos);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Recalcula y muestra el vuelto (o cuánto falta) según el monto recibido.
     */
    private void calcular() {
        recibido = leerRecibido();
        vuelto = recibido - total;

        if (vuelto < 0) {
            lblVuelto.setText("₲ 0");
            lblVuelto.setForeground(COLOR_NEUTRO);
            lblMensaje.setText("Falta ₲ " + fmtPY.format(Math.round(-vuelto)));
            lblMensaje.setForeground(COLOR_FALTA);
            btnConfirmar.setEnabled(false);
        } else {
            lblVuelto.setText("₲ " + fmtPY.format(Math.round(vuelto)));
            lblVuelto.setForeground(recibido == 0 ? COLOR_NEUTRO : COLOR_VUELTO);
            lblMensaje.setText(vuelto == 0 ? "Pago exacto" : "Vuelto a entregar al cliente");
            lblMensaje.setForeground(COLOR_NEUTRO);
            btnConfirmar.setEnabled(true);
        }
    }

    /**
     * Coloca un monto en el campo de efectivo recibido (botones rápidos).
     */
    private void ponerMonto(long monto) {
        txtRecibido.setText(fmtPY.format(monto));
        txtRecibido.requestFocusInWindow();
        txtRecibido.selectAll();
        calcular();
    }

    private void confirmar() {
        reformatear();
        recibido = leerRecibido();

        if (recibido + 0.5 < total) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "El efectivo recibido es menor al total a pagar.",
                    "Atención", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        vuelto = recibido - total;
        confirmado = true;
        dispose();
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
        lblTotalTitulo = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblRecibido = new javax.swing.JLabel();
        txtRecibido = new javax.swing.JTextField();
        btnExacto = new javax.swing.JButton();
        btnPreset10 = new javax.swing.JButton();
        btnPreset20 = new javax.swing.JButton();
        btnPreset50 = new javax.swing.JButton();
        btnPreset100 = new javax.swing.JButton();
        lblVueltoTitulo = new javax.swing.JLabel();
        lblVuelto = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("Vuelto");

        jPanelFondo.setBackground(new java.awt.Color(255, 248, 240));
        jPanelFondo.setPreferredSize(new java.awt.Dimension(460, 460));
        jPanelFondo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(92, 61, 46));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Vuelto");
        jPanelFondo.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 420, 34));

        lblTotalTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalTitulo.setForeground(new java.awt.Color(92, 61, 46));
        lblTotalTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalTitulo.setText("TOTAL A PAGAR");
        jPanelFondo.add(lblTotalTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 62, 420, 20));

        lblTotal.setFont(new java.awt.Font("Segoe UI Black", 1, 30)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(230, 126, 34));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("₲ 0");
        jPanelFondo.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 82, 420, 44));

        lblRecibido.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblRecibido.setForeground(new java.awt.Color(92, 61, 46));
        lblRecibido.setText("Efectivo recibido (Gs.):");
        jPanelFondo.add(lblRecibido, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 142, 400, 22));

        txtRecibido.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtRecibido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecibido.setText("");
        jPanelFondo.add(txtRecibido, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 168, 340, 46));

        btnExacto.setBackground(new java.awt.Color(240, 225, 211));
        btnExacto.setBorderPainted(false);
        btnExacto.setFocusPainted(false);
        btnExacto.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        btnExacto.setForeground(new java.awt.Color(92, 61, 46));
        btnExacto.setText("Exacto");
        btnExacto.addActionListener(this::btnExactoActionPerformed);
        jPanelFondo.add(btnExacto, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 226, 76, 30));

        btnPreset10.setBackground(new java.awt.Color(240, 225, 211));
        btnPreset10.setBorderPainted(false);
        btnPreset10.setFocusPainted(false);
        btnPreset10.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        btnPreset10.setForeground(new java.awt.Color(92, 61, 46));
        btnPreset10.setText("10.000");
        btnPreset10.addActionListener(this::btnPreset10ActionPerformed);
        jPanelFondo.add(btnPreset10, new org.netbeans.lib.awtextra.AbsoluteConstraints(112, 226, 76, 30));

        btnPreset20.setBackground(new java.awt.Color(240, 225, 211));
        btnPreset20.setBorderPainted(false);
        btnPreset20.setFocusPainted(false);
        btnPreset20.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        btnPreset20.setForeground(new java.awt.Color(92, 61, 46));
        btnPreset20.setText("20.000");
        btnPreset20.addActionListener(this::btnPreset20ActionPerformed);
        jPanelFondo.add(btnPreset20, new org.netbeans.lib.awtextra.AbsoluteConstraints(194, 226, 76, 30));

        btnPreset50.setBackground(new java.awt.Color(240, 225, 211));
        btnPreset50.setBorderPainted(false);
        btnPreset50.setFocusPainted(false);
        btnPreset50.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        btnPreset50.setForeground(new java.awt.Color(92, 61, 46));
        btnPreset50.setText("50.000");
        btnPreset50.addActionListener(this::btnPreset50ActionPerformed);
        jPanelFondo.add(btnPreset50, new org.netbeans.lib.awtextra.AbsoluteConstraints(276, 226, 76, 30));

        btnPreset100.setBackground(new java.awt.Color(240, 225, 211));
        btnPreset100.setBorderPainted(false);
        btnPreset100.setFocusPainted(false);
        btnPreset100.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        btnPreset100.setForeground(new java.awt.Color(92, 61, 46));
        btnPreset100.setText("100.000");
        btnPreset100.addActionListener(this::btnPreset100ActionPerformed);
        jPanelFondo.add(btnPreset100, new org.netbeans.lib.awtextra.AbsoluteConstraints(358, 226, 76, 30));

        lblVueltoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblVueltoTitulo.setForeground(new java.awt.Color(92, 61, 46));
        lblVueltoTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVueltoTitulo.setText("VUELTO A ENTREGAR");
        jPanelFondo.add(lblVueltoTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 278, 420, 20));

        lblVuelto.setFont(new java.awt.Font("Segoe UI Black", 1, 32)); // NOI18N
        lblVuelto.setForeground(new java.awt.Color(39, 174, 96));
        lblVuelto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVuelto.setText("₲ 0");
        jPanelFondo.add(lblVuelto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 298, 420, 52));

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(96, 100, 102));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensaje.setText("");
        jPanelFondo.add(lblMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 352, 420, 20));

        btnCancelar.setBackground(new java.awt.Color(217, 136, 128));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        jPanelFondo.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 390, 170, 46));

        btnConfirmar.setBackground(new java.awt.Color(230, 126, 34));
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnConfirmar.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmar.setText("Confirmar cobro");
        btnConfirmar.addActionListener(this::btnConfirmarActionPerformed);
        jPanelFondo.add(btnConfirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 390, 170, 46));

        getContentPane().add(jPanelFondo, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExactoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExactoActionPerformed
        ponerMonto(Math.round(total));
    }//GEN-LAST:event_btnExactoActionPerformed

    private void btnPreset10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreset10ActionPerformed
        ponerMonto(10_000);
    }//GEN-LAST:event_btnPreset10ActionPerformed

    private void btnPreset20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreset20ActionPerformed
        ponerMonto(20_000);
    }//GEN-LAST:event_btnPreset20ActionPerformed

    private void btnPreset50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreset50ActionPerformed
        ponerMonto(50_000);
    }//GEN-LAST:event_btnPreset50ActionPerformed

    private void btnPreset100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreset100ActionPerformed
        ponerMonto(100_000);
    }//GEN-LAST:event_btnPreset100ActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        confirmado = false;
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmar();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    // Getters

    /**
     * @return true si el cajero confirmó el cobro.
     */
    public boolean isConfirmado() {
        return confirmado;
    }

    /**
     * @return el efectivo recibido del cliente.
     */
    public double getMontoRecibido() {
        return recibido;
    }

    /**
     * @return el vuelto que hay que entregar al cliente.
     */
    public double getVuelto() {
        return vuelto;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnExacto;
    private javax.swing.JButton btnPreset10;
    private javax.swing.JButton btnPreset100;
    private javax.swing.JButton btnPreset20;
    private javax.swing.JButton btnPreset50;
    private javax.swing.JPanel jPanelFondo;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblRecibido;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalTitulo;
    private javax.swing.JLabel lblVuelto;
    private javax.swing.JLabel lblVueltoTitulo;
    private javax.swing.JTextField txtRecibido;
    // End of variables declaration//GEN-END:variables
}
