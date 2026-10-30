/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.panaderia.formularios;

import com.panaderia.clases.CVentas;
import com.panaderia.clases.Sesion;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lilian
 */
public class FormVentas extends javax.swing.JPanel {

    private final CVentas objetoVentas = new CVentas();
    private final java.text.NumberFormat fmtPY = java.text.NumberFormat.getInstance(new java.util.Locale("es", "PY"));

    /**
     * Creates new form FormVentas
     */
    public FormVentas() {
        initComponents();
        aplicarColoresVentas();
        configurarMetodosPago();
        configurarModeloTabla();
        btnClientes.addActionListener(this::btnClientesActionPerformed);
        btnBuscarProducto.addActionListener(this::btnBuscarProductoActionPerformed);
        btnCobrar.addActionListener(this::btnCobrarActionPerformed);
        btnNuevaVenta.addActionListener(this::btnNuevaVentaActionPerformed);
        inicializarVentas();

        if (!Sesion.esAdmin()) {
            btnConfigTicket.setEnabled(false);
            btnConfigTicket.setBackground(new java.awt.Color(229, 224, 219));
            btnConfigTicket.setToolTipText("Solo administradores");
        }
    }

    /**
     * Cambia el color del método de pago seleccionado para que se note cuál está activo.
     */
    private void configurarMetodosPago() {
        javax.swing.JToggleButton[] botonesPago = { btnEfectivo, btnTarjeta, btnQR };
        for (javax.swing.JToggleButton btn : botonesPago) {
            btn.addItemListener(e -> actualizarColorMetodoPago());
        }
        actualizarColorMetodoPago();
    }

    private void actualizarColorMetodoPago() {
        java.awt.Color seleccionado = new java.awt.Color(230, 126, 34);
        java.awt.Color noSeleccionado = new java.awt.Color(96, 100, 102);
        javax.swing.JToggleButton[] botonesPago = { btnEfectivo, btnTarjeta, btnQR };
        for (javax.swing.JToggleButton btn : botonesPago) {
            btn.setBackground(btn.isSelected() ? seleccionado : noSeleccionado);
        }
    }
private void aplicarColoresVentas() {
    // 1. Definición de colores
    java.awt.Color naranjaCorp = new java.awt.Color(230, 126, 34);
    java.awt.Color grisMetodos = new java.awt.Color(96, 100, 102);
    java.awt.Color cremaFondo = new java.awt.Color(255, 248, 240);
    java.awt.Color fondoVentana = new java.awt.Color(253, 251, 247);
    java.awt.Color textoMarron = new java.awt.Color(92, 61, 46);

   

    // 3. Botones Naranjas Principales
    javax.swing.JButton[] botonesNaranja = {
        btnBuscarProducto, btnClientes, btnNuevaVenta, btnCobrar
    };
    for (javax.swing.JButton btn : botonesNaranja) {
        if (btn != null) {
            btn.setBackground(naranjaCorp);
            btn.setForeground(java.awt.Color.WHITE);
            btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
        }
    }

    // 4. Botones de Métodos de Pago (ToggleButtons)
    javax.swing.JToggleButton[] botonesPago = { btnEfectivo, btnTarjeta, btnQR };
    for (javax.swing.JToggleButton btn : botonesPago) {
        if (btn != null) {
            btn.setBackground(grisMetodos);
            btn.setForeground(java.awt.Color.WHITE);
            btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
        }
    }

    // 5. Títulos y Etiquetas de Texto
    lblTituloOrden.setForeground(textoMarron);
    lblMetodoPago.setForeground(textoMarron);
    lblTotalPagar.setForeground(textoMarron);
    
    // Panel de Total A Pagar
    panelTotal.setBackground(cremaFondo);

    // 6. Ícono de ticket en el botón "Configurar Ticket"
    btnConfigTicket.setIcon(com.panaderia.util.Iconos.tintado(
            "/iconos/ticket.png", java.awt.Color.WHITE, 16));
    btnConfigTicket.setIconTextGap(6);
}

    private void configurarModeloTabla() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Cant.", "Precio Unit.", "Subtotal", "Eliminar", "IVA %"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Solo la cantidad es editable directamente
            }
        };
        jTableVentas.setModel(model);

        // Clic en la columna "Eliminar" (índice 5) para quitar la fila
        jTableVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int col = jTableVentas.columnAtPoint(evt.getPoint());
                int fila = jTableVentas.rowAtPoint(evt.getPoint());
                if (col == 5 && fila >= 0) {
                    ((DefaultTableModel) jTableVentas.getModel()).removeRow(fila);
                    recalcularTotales();
                }
            }
        });
    }

    private void inicializarVentas() {
        actualizarNumeroOrden();
        recalcularTotales();
        escucharCambiosTabla();
    }

    /**
     * Consulta la BD y muestra el número correlativo de orden disponible
     */
    private void actualizarNumeroOrden() {
        CVentas controlador = new CVentas();
        int siguienteVenta = controlador.obtenerSiguienteNumeroVenta();
        jLabel4.setText(String.valueOf(siguienteVenta));
    }

    /**
     * Recalcula Subtotal, IVA 5%, IVA 10%, Total IVA y Total a Pagar en tiempo real
     */
    private void recalcularTotales() {
        double subtotalAcumulado = 0;
        double totalIva5 = 0;
        double totalIva10 = 0;

        DefaultTableModel model = (DefaultTableModel) jTableVentas.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            double subtotalFila = Double.parseDouble(model.getValueAt(i, 4).toString().replaceAll("[^0-9]", ""));

            int porcentajeIva = 10;
            if (model.getColumnCount() > 6 && model.getValueAt(i, 6) != null) {
                try {
                    porcentajeIva = Integer.parseInt(model.getValueAt(i, 6).toString());
                } catch (NumberFormatException ex) {
                    // Si no es numérico, se mantiene 10%
                }
            }

            subtotalAcumulado += subtotalFila;

            if (porcentajeIva == 10) {
                totalIva10 += Math.round(subtotalFila / 11.0);
            } else if (porcentajeIva == 5) {
                totalIva5 += Math.round(subtotalFila / 21.0);
            }
        }

        lblSubtotal.setText("Subtotal: ₲ " + fmtPY.format(subtotalAcumulado));
        lblIva5.setText("IVA (5%): ₲ " + fmtPY.format(totalIva5));
        lblIva10.setText("IVA (10%): ₲ " + fmtPY.format(totalIva10));
        lblTotalIva.setText("Total IVA: ₲ " + fmtPY.format(totalIva5 + totalIva10));

        lblTotalPagar.setText("TOTAL A PAGAR: ₲ " + fmtPY.format(subtotalAcumulado));
        btnCobrar.setText("COBRAR ₲ " + fmtPY.format(subtotalAcumulado));
    }

    /**
     * Escucha la modificación directa de la cantidad en la celda de la JTable
     */
    private void escucharCambiosTabla() {
        DefaultTableModel model = (DefaultTableModel) jTableVentas.getModel();
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 2) {
                int fila = e.getFirstRow();
                if (fila < 0 || fila >= model.getRowCount()) {
                    return;
                }
                try {
                    int cantidad = Integer.parseInt(model.getValueAt(fila, 2).toString());
                    double precio = Double.parseDouble(model.getValueAt(fila, 3).toString().replaceAll("[^0-9]", ""));
                    double nuevoSubtotal = cantidad * precio;

                    model.setValueAt("₲ " + fmtPY.format(nuevoSubtotal), fila, 4);
                    recalcularTotales();
                } catch (Exception ex) {
                    System.err.println("Error al recalcular fila: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * Método auxiliar para agregar productos desde el diálogo de búsqueda
     */
    public void agregarProductoACarrito(int idProducto, String nombre, int cantidad, double precioUnitario, int porcentajeIva) {
        DefaultTableModel model = (DefaultTableModel) jTableVentas.getModel();

        // Si el producto ya está en la lista, incrementa la cantidad
        for (int i = 0; i < model.getRowCount(); i++) {
            int idActual = Integer.parseInt(model.getValueAt(i, 0).toString());
            if (idActual == idProducto) {
                int cantActual = Integer.parseInt(model.getValueAt(i, 2).toString());
                model.setValueAt(cantActual + cantidad, i, 2);
                return; // El listener de la tabla actualizará el subtotal
            }
        }

        double subtotal = cantidad * precioUnitario;
        model.addRow(new Object[]{
            idProducto,
            nombre,
            cantidad,
            "₲ " + fmtPY.format(precioUnitario),
            "₲ " + fmtPY.format(subtotal),
            "❌",
            porcentajeIva
        });

        recalcularTotales();
    }

    /**
     * Retorna el ID de la forma de pago según el ToggleButton seleccionado
     */
    private int obtenerIdFormaPago() {
        if (btnTarjeta.isSelected()) {
            return 3; // Tarjeta de Crédito
        }
        if (btnQR.isSelected()) {
            return 5; // QR / Billetera Digital
        }
        return 1; // Efectivo
    }

    // ===== API pública: CVentas controla estas acciones =====

    public javax.swing.JTable getTablaVentas() {
        return jTableVentas;
    }

    /**
     * Devuelve el monto total a cobrar (sin símbolos ni separadores).
     */
    public double getTotalParaCobrar() {
        return Double.parseDouble(lblTotalPagar.getText().replaceAll("[^0-9]", ""));
    }

    /**
     * Devuelve el ID de la forma de pago seleccionada.
     */
    public int getFormaPagoSeleccionada() {
        return obtenerIdFormaPago();
    }

    /**
     * Muestra el cliente seleccionado en la etiqueta de la orden.
     */
    public void mostrarCliente(String nombre, String ruc) {
        jLabel2.setText("Cliente: " + nombre + "      RUC/CI: " + ruc);
    }

    /**
     * Limpia la tabla, el cliente y los totales de la orden actual.
     */
    public void limpiarVenta() {
        DefaultTableModel model = (DefaultTableModel) jTableVentas.getModel();
        model.setRowCount(0);

        jLabel2.setText("Cliente: Consumidor Final      RUC/CI: N/A");

        actualizarNumeroOrden();
        recalcularTotales();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        PanelVentas = new javax.swing.JPanel();
        fondoVentana = new javax.swing.JPanel();
        btnClientes = new javax.swing.JButton();
        btnBuscarProducto = new javax.swing.JButton();
        btnNuevaVenta = new javax.swing.JButton();
        lblMetodoPago = new javax.swing.JLabel();
        btnTarjeta = new javax.swing.JToggleButton();
        btnEfectivo = new javax.swing.JToggleButton();
        btnQR = new javax.swing.JToggleButton();
        btnCobrar = new javax.swing.JButton();
        panelOrden = new javax.swing.JPanel();
        lblTituloOrden = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableVentas = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnConfigTicket = new javax.swing.JButton();
        btnCambiar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblDescuento = new javax.swing.JLabel();
        lblIva5 = new javax.swing.JLabel();
        lblIva10 = new javax.swing.JLabel();
        lblTotalIva = new javax.swing.JLabel();
        panelTotal = new javax.swing.JPanel();
        lblTotalPagar = new javax.swing.JLabel();

        PanelVentas.setLayout(new java.awt.BorderLayout());

        fondoVentana.setBackground(new java.awt.Color(255, 248, 240));
        fondoVentana.setPreferredSize(new java.awt.Dimension(280, 400));

        btnClientes.setBackground(new java.awt.Color(230, 126, 34));
        btnClientes.setForeground(new java.awt.Color(0, 0, 0));
        btnClientes.setText("CLIENTE");

        btnBuscarProducto.setBackground(new java.awt.Color(230, 126, 34));
        btnBuscarProducto.setForeground(new java.awt.Color(0, 0, 0));
        btnBuscarProducto.setText("BUSCAR PRODUCTO");

        btnNuevaVenta.setBackground(new java.awt.Color(230, 126, 34));
        btnNuevaVenta.setForeground(new java.awt.Color(0, 0, 0));
        btnNuevaVenta.setText("NUEVA VENTA");
        btnNuevaVenta.addActionListener(this::btnNuevaVentaActionPerformed);

        lblMetodoPago.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMetodoPago.setForeground(new java.awt.Color(92, 61, 46));
        lblMetodoPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMetodoPago.setText("MÉTODO DE PAGO");

        btnTarjeta.setBackground(new java.awt.Color(96, 100, 102));
        buttonGroup1.add(btnTarjeta);
        btnTarjeta.setText("TARJETA");

        buttonGroup1.add(btnEfectivo);
        btnEfectivo.setSelected(true);
        btnEfectivo.setText("EFECTIVO");

        btnQR.setBackground(new java.awt.Color(96, 100, 102));
        buttonGroup1.add(btnQR);
        btnQR.setText("QR");

        btnCobrar.setBackground(new java.awt.Color(230, 126, 34));
        btnCobrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCobrar.setForeground(new java.awt.Color(0, 0, 0));
        btnCobrar.setText("COBRAR");

        javax.swing.GroupLayout fondoVentanaLayout = new javax.swing.GroupLayout(fondoVentana);
        fondoVentana.setLayout(fondoVentanaLayout);
        fondoVentanaLayout.setHorizontalGroup(
            fondoVentanaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fondoVentanaLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(fondoVentanaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblMetodoPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevaVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(btnBuscarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEfectivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTarjeta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCobrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        fondoVentanaLayout.setVerticalGroup(
            fondoVentanaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fondoVentanaLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(btnBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnNuevaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnQR, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(btnCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        PanelVentas.add(fondoVentana, java.awt.BorderLayout.EAST);

        panelOrden.setBackground(new java.awt.Color(253, 251, 247));

        lblTituloOrden.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTituloOrden.setForeground(new java.awt.Color(92, 61, 46));
        lblTituloOrden.setText("ORDEN DE VENTA #");

        jTableVentas.setBackground(new java.awt.Color(255, 255, 255));
        jTableVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableVentas.setRowHeight(30);
        jScrollPane2.setViewportView(jTableVentas);

        jPanel1.setBackground(new java.awt.Color(255, 248, 240));

        btnConfigTicket.setBackground(new java.awt.Color(230, 126, 34));
        btnConfigTicket.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConfigTicket.setForeground(new java.awt.Color(255, 255, 255));
        btnConfigTicket.setText("Configurar Ticket");
        btnConfigTicket.setContentAreaFilled(false);
        btnConfigTicket.setFocusPainted(false);
        btnConfigTicket.setOpaque(true);
        btnConfigTicket.setPreferredSize(new java.awt.Dimension(180, 30));
        btnConfigTicket.addActionListener(this::btnConfigTicketActionPerformed);

        btnCambiar.setBackground(new java.awt.Color(255, 248, 240));
        btnCambiar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCambiar.setForeground(new java.awt.Color(92, 61, 46));
        btnCambiar.setText("[Cambiar]");
        btnCambiar.setBorder(null);
        btnCambiar.addActionListener(this::btnCambiarActionPerformed);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(92, 61, 46));
        jLabel2.setText("Cliente: Consumidor Final                                RUC/CI: N/A");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(btnConfigTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnCambiar)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfigTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCambiar)))
        );

        lblSubtotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSubtotal.setForeground(new java.awt.Color(92, 61, 46));
        lblSubtotal.setText("Subtotal: ₲ 0");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(92, 61, 46));
        jLabel4.setText("0");

        lblDescuento.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDescuento.setForeground(new java.awt.Color(92, 61, 46));
        lblDescuento.setText("Descuento: ₲ 0 (0%)");

        lblIva5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblIva5.setForeground(new java.awt.Color(92, 61, 46));
        lblIva5.setText("IVA (5%): ₲ 0");

        lblIva10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblIva10.setForeground(new java.awt.Color(92, 61, 46));
        lblIva10.setText("IVA (10%): ₲ 0");

        lblTotalIva.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalIva.setForeground(new java.awt.Color(92, 61, 46));
        lblTotalIva.setText("Total IVA: ₲ 0");

        panelTotal.setBackground(new java.awt.Color(255, 248, 240));

        lblTotalPagar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalPagar.setForeground(new java.awt.Color(92, 61, 46));
        lblTotalPagar.setText("TOTAL A PAGAR: ₲ 0");

        javax.swing.GroupLayout panelTotalLayout = new javax.swing.GroupLayout(panelTotal);
        panelTotal.setLayout(panelTotalLayout);
        panelTotalLayout.setHorizontalGroup(
            panelTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTotalLayout.createSequentialGroup()
                .addContainerGap(375, Short.MAX_VALUE)
                .addComponent(lblTotalPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        panelTotalLayout.setVerticalGroup(
            panelTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblTotalPagar)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelOrdenLayout = new javax.swing.GroupLayout(panelOrden);
        panelOrden.setLayout(panelOrdenLayout);
        panelOrdenLayout.setHorizontalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOrdenLayout.createSequentialGroup()
                            .addContainerGap(457, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblIva10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblIva5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblTotalIva, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(64, 64, 64))
                        .addComponent(panelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelOrdenLayout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelOrdenLayout.createSequentialGroup()
                                    .addGap(9, 9, 9)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(panelOrdenLayout.createSequentialGroup()
                                    .addComponent(lblTituloOrden)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(panelOrdenLayout.createSequentialGroup()
                            .addGap(33, 33, 33)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 657, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        panelOrdenLayout.setVerticalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTituloOrden)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSubtotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDescuento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIva5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIva10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalIva)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelVentas.add(panelOrden, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCambiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarActionPerformed
        objetoVentas.abrirBuscarCliente(this);
    }//GEN-LAST:event_btnCambiarActionPerformed

    private void btnConfigTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigTicketActionPerformed
        objetoVentas.abrirConfigTicket(this);
    }//GEN-LAST:event_btnConfigTicketActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        objetoVentas.abrirBuscarCliente(this);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed
        objetoVentas.abrirBuscarProducto(this);
    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void btnCobrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarActionPerformed
        objetoVentas.cobrarVenta(this);
    }//GEN-LAST:event_btnCobrarActionPerformed

    private void btnNuevaVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaVentaActionPerformed
        objetoVentas.nuevaVenta(this);
    }//GEN-LAST:event_btnNuevaVentaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelVentas;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnCambiar;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnCobrar;
    private javax.swing.JButton btnConfigTicket;
    private javax.swing.JToggleButton btnEfectivo;
    private javax.swing.JButton btnNuevaVenta;
    private javax.swing.JToggleButton btnQR;
    private javax.swing.JToggleButton btnTarjeta;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel fondoVentana;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableVentas;
    private javax.swing.JLabel lblDescuento;
    private javax.swing.JLabel lblIva10;
    private javax.swing.JLabel lblIva5;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTituloOrden;
    private javax.swing.JLabel lblTotalIva;
    private javax.swing.JLabel lblTotalPagar;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelTotal;
    // End of variables declaration//GEN-END:variables
}
