/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.panaderia.formularios;

import com.panaderia.clases.CDashboard;
import com.panaderia.clases.Sesion;

/**
 *
 * @author Lilian
 */
public class FormDashboard extends javax.swing.JPanel {

    private final CDashboard objetoDashboard = new CDashboard();

    /**
     * Creates new form FormDashboard
     */
public FormDashboard() {
        initComponents();    
        btnNuevoProductoAcceso.setContentAreaFilled(false);
        btnNuevoProductoAcceso.setOpaque(true);
        
        btnNuevaVentaAcceso.setContentAreaFilled(false);
        btnNuevaVentaAcceso.setOpaque(true);
        btnNuevaVentaAcceso.setFocusPainted(false);
        btnNuevaVentaAcceso.setBorderPainted(false);

        btnNuevoProductoAcceso.setContentAreaFilled(false);
        btnNuevoProductoAcceso.setOpaque(true);
        btnNuevoProductoAcceso.setFocusPainted(false);
        btnNuevoProductoAcceso.setBorderPainted(false);

        btnProveedoresAcceso.setContentAreaFilled(false);
        btnProveedoresAcceso.setOpaque(true);
        btnProveedoresAcceso.setFocusPainted(false);
        btnProveedoresAcceso.setBorderPainted(false);

        btnCierreCajaAcceso.setContentAreaFilled(false);
        btnCierreCajaAcceso.setOpaque(true);
        btnCierreCajaAcceso.setFocusPainted(false);
        btnCierreCajaAcceso.setBorderPainted(false);

        if (!Sesion.esAdmin()) {
            btnNuevoProductoAcceso.setVisible(false);
            btnProveedoresAcceso.setVisible(false);
            reacomodarAccesosRapidos();
        }

        initEstilosDashboard();
        objetoDashboard.cargarIndicadores(this);
    }

    /**
     * Reubica los accesos rápidos visibles para que no queden huecos cuando
     * el cajero no puede ver Productos ni Proveedores.
     */
    private void reacomodarAccesosRapidos() {
        javax.swing.JButton[] accesos = {
            btnNuevoProductoAcceso, btnNuevaVentaAcceso, btnProveedoresAcceso, btnCierreCajaAcceso
        };
        int y = 60;
        for (javax.swing.JButton boton : accesos) {
            if (!boton.isVisible()) {
                continue;
            }
            panelContenedorAccesos.remove(boton);
            panelContenedorAccesos.add(boton,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(30, y, 380, 40));
            y += 50;
        }
        panelContenedorAccesos.revalidate();
        panelContenedorAccesos.repaint();
    }

    private void initEstilosDashboard() {
        // Fondo de la página (crema) para que las tarjetas resalten
        panelPrincipalBase.setBackground(new java.awt.Color(255, 248, 240));

        // 1. Transparencia y separación (hgap/vgap) en los layouts de cuadrícula
        panelKPIs.setOpaque(false);
        panelInferior.setOpaque(false);
        
        // Dar separación automática de 15px entre las tarjetas de arriba y abajo
        ((java.awt.GridLayout) panelKPIs.getLayout()).setHgap(15);
        ((java.awt.GridLayout) panelInferior.getLayout()).setHgap(15);

        // 2. Paleta del proyecto
        java.awt.Color cafe = new java.awt.Color(78, 52, 46);       // #4E342E (marrón café)
        java.awt.Color cafeBorde = new java.awt.Color(120, 80, 65); // borde del café
        java.awt.Color crema = new java.awt.Color(245, 230, 211);   // #F5E6D3
        java.awt.Color naranja = new java.awt.Color(230, 126, 34);  // #E67E22
        java.awt.Color blanco = new java.awt.Color(255, 255, 255);

        // 3. Tarjetas KPI: panel marrón café oscuro, con ícono y textos claros
        estiloTarjetaKpi(panelCardVentas, cafe, cafeBorde);
        estiloTarjetaKpi(panelCardProductos, cafe, cafeBorde);
        estiloTarjetaKpi(panelCardUsuarios, cafe, cafeBorde);

        java.awt.Font fuenteTitulo = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15);
        java.awt.Font fuenteNumero = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 26);

        lblTituloVentas.setForeground(crema);
        lblTituloVentas.setFont(fuenteTitulo);
        lblTituloVentas.setIcon(com.panaderia.util.Iconos.tintado("/iconos/ventas.png", naranja, 18));
        lblTituloVentas.setIconTextGap(6);

        lblTituloUsuarios.setForeground(crema);
        lblTituloUsuarios.setFont(fuenteTitulo);
        lblTituloUsuarios.setIcon(com.panaderia.util.Iconos.tintado("/iconos/usuarios.png", naranja, 18));
        lblTituloUsuarios.setIconTextGap(6);

        lblTituloProductos.setForeground(crema);
        lblTituloProductos.setFont(fuenteTitulo);
        lblTituloProductos.setIcon(com.panaderia.util.Iconos.tintado("/iconos/productos.png", naranja, 18));
        lblTituloProductos.setIconTextGap(6);

        lblTotalVentas.setForeground(blanco);
        lblTotalVentas.setFont(fuenteNumero);
        lblCantUsuarios.setForeground(blanco);
        lblCantUsuarios.setFont(fuenteNumero);
        lblCantProductos.setForeground(blanco);
        lblCantProductos.setFont(fuenteNumero);

        // 4. Paneles inferiores: beige cálido con borde marrón redondeado
        estiloTarjeta(panelTablaRecientes, true);
        // El panel de accesos usa posiciones absolutas: sin padding interno para no mover los botones
        estiloTarjeta(panelContenedorAccesos, false);
    }

    /**
     * Estilo de las tarjetas KPI (arriba): panel de color propio bien marcado,
     * como una tarjeta de indicador.
     */
    private void estiloTarjetaKpi(javax.swing.JPanel panel, java.awt.Color fondo, java.awt.Color borde) {
        panel.setOpaque(true);
        panel.setBackground(fondo);
        javax.swing.border.Border bordeExterior =
                new javax.swing.border.LineBorder(borde, 1, true);
        javax.swing.border.Border bordeInterior =
                javax.swing.BorderFactory.createEmptyBorder(12, 16, 12, 16);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(bordeExterior, bordeInterior));
    }

    private void estiloTarjeta(javax.swing.JPanel panel, boolean conPadding) {
        panel.setOpaque(true);
        panel.setBackground(new java.awt.Color(245, 230, 211));
        javax.swing.border.Border bordeExterior =
                new javax.swing.border.LineBorder(new java.awt.Color(201, 168, 124), 1, true);
        if (conPadding) {
            javax.swing.border.Border bordeInterior =
                    javax.swing.BorderFactory.createEmptyBorder(12, 16, 12, 16);
            panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(bordeExterior, bordeInterior));
        } else {
            panel.setBorder(bordeExterior);
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

        panelPrincipalBase = new javax.swing.JPanel();
        panelKPIs = new javax.swing.JPanel();
        panelCardVentas = new javax.swing.JPanel();
        lblTituloVentas = new javax.swing.JLabel();
        lblTotalVentas = new javax.swing.JLabel();
        panelCardProductos = new javax.swing.JPanel();
        lblTituloUsuarios = new javax.swing.JLabel();
        lblCantUsuarios = new javax.swing.JLabel();
        panelCardUsuarios = new javax.swing.JPanel();
        lblTituloProductos = new javax.swing.JLabel();
        lblCantProductos = new javax.swing.JLabel();
        panelInferior = new javax.swing.JPanel();
        panelTablaRecientes = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaRecientes = new javax.swing.JTable();
        lblTituloRecientes = new javax.swing.JLabel();
        panelContenedorAccesos = new javax.swing.JPanel();
        lblTituloAccesos = new javax.swing.JLabel();
        btnNuevaVentaAcceso = new javax.swing.JButton();
        btnProveedoresAcceso = new javax.swing.JButton();
        btnCierreCajaAcceso = new javax.swing.JButton();
        btnNuevoProductoAcceso = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(800, 550));

        panelPrincipalBase.setLayout(new java.awt.BorderLayout());

        panelKPIs.setLayout(new java.awt.GridLayout(1, 3, 15, 0));
        panelPrincipalBase.add(panelKPIs, java.awt.BorderLayout.NORTH);

        panelCardVentas.setBackground(new java.awt.Color(253, 251, 247));

        lblTituloVentas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTituloVentas.setForeground(new java.awt.Color(92, 61, 46));
        lblTituloVentas.setText("Ventas de Hoy");

        lblTotalVentas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalVentas.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalVentas.setText("Gs. 0");

        javax.swing.GroupLayout panelCardVentasLayout = new javax.swing.GroupLayout(panelCardVentas);
        panelCardVentas.setLayout(panelCardVentasLayout);
        panelCardVentasLayout.setHorizontalGroup(
            panelCardVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCardVentasLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panelCardVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        panelCardVentasLayout.setVerticalGroup(
            panelCardVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCardVentasLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblTituloVentas)
                .addGap(28, 28, 28)
                .addComponent(lblTotalVentas)
                .addContainerGap(99, Short.MAX_VALUE))
        );

        panelPrincipalBase.add(panelCardVentas, java.awt.BorderLayout.LINE_START);

        panelCardProductos.setBackground(new java.awt.Color(253, 251, 247));

        lblTituloUsuarios.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTituloUsuarios.setForeground(new java.awt.Color(92, 61, 46));
        lblTituloUsuarios.setText("Usuarios Registrados");

        lblCantUsuarios.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCantUsuarios.setForeground(new java.awt.Color(0, 0, 0));
        lblCantUsuarios.setText("0");

        javax.swing.GroupLayout panelCardProductosLayout = new javax.swing.GroupLayout(panelCardProductos);
        panelCardProductos.setLayout(panelCardProductosLayout);
        panelCardProductosLayout.setHorizontalGroup(
            panelCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCardProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCardProductosLayout.createSequentialGroup()
                        .addComponent(lblCantUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(30, 30, 30))
                    .addGroup(panelCardProductosLayout.createSequentialGroup()
                        .addComponent(lblTituloUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(30, Short.MAX_VALUE))))
        );
        panelCardProductosLayout.setVerticalGroup(
            panelCardProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCardProductosLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblTituloUsuarios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCantUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(102, Short.MAX_VALUE))
        );

        panelPrincipalBase.add(panelCardProductos, java.awt.BorderLayout.LINE_END);

        panelCardUsuarios.setBackground(new java.awt.Color(253, 251, 247));

        lblTituloProductos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTituloProductos.setForeground(new java.awt.Color(92, 61, 46));
        lblTituloProductos.setText("Total Productos");

        lblCantProductos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCantProductos.setForeground(new java.awt.Color(0, 0, 0));
        lblCantProductos.setText("0");

        javax.swing.GroupLayout panelCardUsuariosLayout = new javax.swing.GroupLayout(panelCardUsuarios);
        panelCardUsuarios.setLayout(panelCardUsuariosLayout);
        panelCardUsuariosLayout.setHorizontalGroup(
            panelCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCardUsuariosLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(panelCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTituloProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(lblCantProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(298, Short.MAX_VALUE))
        );
        panelCardUsuariosLayout.setVerticalGroup(
            panelCardUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCardUsuariosLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblTituloProductos)
                .addGap(18, 18, 18)
                .addComponent(lblCantProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(101, Short.MAX_VALUE))
        );

        panelPrincipalBase.add(panelCardUsuarios, java.awt.BorderLayout.CENTER);

        panelInferior.setBackground(new java.awt.Color(253, 251, 247));
        panelInferior.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        panelTablaRecientes.setBackground(new java.awt.Color(253, 251, 247));
        panelTablaRecientes.setPreferredSize(new java.awt.Dimension(300, 100));
        panelTablaRecientes.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        tablaRecientes.setBackground(new java.awt.Color(255, 255, 255));
        tablaRecientes.setForeground(new java.awt.Color(92, 61, 46));
        tablaRecientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaRecientes.setGridColor(new java.awt.Color(240, 230, 220));
        tablaRecientes.setRowHeight(30);
        jScrollPane1.setViewportView(tablaRecientes);

        panelTablaRecientes.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        lblTituloRecientes.setBackground(new java.awt.Color(253, 251, 247));
        lblTituloRecientes.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTituloRecientes.setForeground(new java.awt.Color(92, 61, 46));
        lblTituloRecientes.setText("Últimas Ventas");
        panelTablaRecientes.add(lblTituloRecientes, java.awt.BorderLayout.PAGE_START);

        panelInferior.add(panelTablaRecientes);

        panelContenedorAccesos.setBackground(new java.awt.Color(253, 251, 247));
        panelContenedorAccesos.setPreferredSize(new java.awt.Dimension(500, 350));
        panelContenedorAccesos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTituloAccesos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTituloAccesos.setForeground(new java.awt.Color(92, 61, 46));
        lblTituloAccesos.setText("Accesos Rápidos");
        panelContenedorAccesos.add(lblTituloAccesos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 150, -1));

        btnNuevaVentaAcceso.setBackground(new java.awt.Color(230, 126, 34));
        btnNuevaVentaAcceso.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnNuevaVentaAcceso.setForeground(new java.awt.Color(0, 0, 0));
        btnNuevaVentaAcceso.setText("+ Nueva Venta");
        btnNuevaVentaAcceso.addActionListener(this::btnNuevaVentaAccesoActionPerformed);
        panelContenedorAccesos.add(btnNuevaVentaAcceso, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 380, 40));

        btnProveedoresAcceso.setBackground(new java.awt.Color(230, 126, 34));
        btnProveedoresAcceso.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnProveedoresAcceso.setForeground(new java.awt.Color(0, 0, 0));
        btnProveedoresAcceso.setText("+ Proveedores");
        btnProveedoresAcceso.addActionListener(this::btnProveedoresAccesoActionPerformed);
        panelContenedorAccesos.add(btnProveedoresAcceso, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 380, 40));

        btnCierreCajaAcceso.setBackground(new java.awt.Color(230, 126, 34));
        btnCierreCajaAcceso.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCierreCajaAcceso.setForeground(new java.awt.Color(0, 0, 0));
        btnCierreCajaAcceso.setText("Cierre de Caja");
        btnCierreCajaAcceso.addActionListener(this::btnCierreCajaAccesoActionPerformed);
        panelContenedorAccesos.add(btnCierreCajaAcceso, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 380, 40));

        btnNuevoProductoAcceso.setBackground(new java.awt.Color(230, 126, 34));
        btnNuevoProductoAcceso.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnNuevoProductoAcceso.setForeground(new java.awt.Color(0, 0, 0));
        btnNuevoProductoAcceso.setText("+ Nuevo Producto");
        btnNuevoProductoAcceso.setPreferredSize(new java.awt.Dimension(240, 42));
        btnNuevoProductoAcceso.addActionListener(this::btnNuevoProductoAccesoActionPerformed);
        panelContenedorAccesos.add(btnNuevoProductoAcceso, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 380, 40));

        panelInferior.add(panelContenedorAccesos);

        panelPrincipalBase.add(panelInferior, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipalBase, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipalBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevaVentaAccesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaVentaAccesoActionPerformed
        objetoDashboard.abrirNuevaVenta(this);
    }//GEN-LAST:event_btnNuevaVentaAccesoActionPerformed

    private void btnNuevoProductoAccesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProductoAccesoActionPerformed
        objetoDashboard.abrirNuevoProducto(this);
    }//GEN-LAST:event_btnNuevoProductoAccesoActionPerformed

    private void btnProveedoresAccesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresAccesoActionPerformed
        objetoDashboard.abrirProveedores(this);
    }//GEN-LAST:event_btnProveedoresAccesoActionPerformed

    private void btnCierreCajaAccesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCierreCajaAccesoActionPerformed
        objetoDashboard.abrirCierreCaja(this);
    }//GEN-LAST:event_btnCierreCajaAccesoActionPerformed

    public javax.swing.JLabel getLblTotalVentas() {
        return lblTotalVentas;
    }

    public javax.swing.JLabel getLblCantUsuarios() {
        return lblCantUsuarios;
    }

    public javax.swing.JLabel getLblCantProductos() {
        return lblCantProductos;
    }

    public javax.swing.JTable getTablaRecientes() {
        return tablaRecientes;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCierreCajaAcceso;
    private javax.swing.JButton btnNuevaVentaAcceso;
    private javax.swing.JButton btnNuevoProductoAcceso;
    private javax.swing.JButton btnProveedoresAcceso;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCantProductos;
    private javax.swing.JLabel lblCantUsuarios;
    private javax.swing.JLabel lblTituloAccesos;
    private javax.swing.JLabel lblTituloProductos;
    private javax.swing.JLabel lblTituloRecientes;
    private javax.swing.JLabel lblTituloUsuarios;
    private javax.swing.JLabel lblTituloVentas;
    private javax.swing.JLabel lblTotalVentas;
    private javax.swing.JPanel panelCardProductos;
    private javax.swing.JPanel panelCardUsuarios;
    private javax.swing.JPanel panelCardVentas;
    private javax.swing.JPanel panelContenedorAccesos;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelKPIs;
    private javax.swing.JPanel panelPrincipalBase;
    private javax.swing.JPanel panelTablaRecientes;
    private javax.swing.JTable tablaRecientes;
    // End of variables declaration//GEN-END:variables
}
