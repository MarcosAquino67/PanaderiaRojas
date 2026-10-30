/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.panaderia.formularios;

import com.panaderia.clases.CCaja;
import com.panaderia.clases.Sesion;
import javax.swing.JPanel;

/**
 *
 * @author Lilian
 */
public class MenuPrincipal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MenuPrincipal.class.getName());
    private static boolean cajaInicializada = false;

    /**
     * Creates new form MenuPrincipal
     */
    public MenuPrincipal() {
        inicializarMenu();
    }

    /**
     * Constructor usado por FormLogin tras validar credenciales.
     */
    public MenuPrincipal(int idUsuario, String nombreUsuario, String rolUsuario) {
        this();
        Sesion.iniciar(idUsuario, nombreUsuario, rolUsuario);
        lblUsuarioActivo.setText(nombreUsuario);
        lblUsuarioActivo.setIcon(com.panaderia.util.Iconos.tintado(
                "/iconos/usuario.png", new java.awt.Color(92, 61, 46), 18));
        lblUsuarioActivo.setIconTextGap(8);
        aplicarPermisos();
    }

    private void inicializarMenu() {
 initComponents();
 // 1. Define el tamaño exacto del formulario
    this.setSize(1280, 780);
    
    // 2. Centra la ventana en la pantalla del usuario
    this.setLocationRelativeTo(null);
    
    // 3. Evita que el usuario la deforme redimensionando (opcional)
    this.setResizable(false); 


        // 1. Configurar botones del menú para permitir pintar colores
        javax.swing.JButton[] botonesMenu = {
            btnDashboard, btnVentas, btnProductos, btnReportes, btnProveedores, btnClientes, btnUsuarios, btnCaja
        };

        for (javax.swing.JButton btn : botonesMenu) {
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
        }

        if (btnCerrarSesion != null) {
            btnCerrarSesion.setContentAreaFilled(false);
            btnCerrarSesion.setOpaque(true);
            btnCerrarSesion.setFocusPainted(false);
            btnCerrarSesion.setBorderPainted(false);
            btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);
        }

        // 2. Establecer 'Dashboard' como activo al iniciar
        marcarBotonActivo(btnDashboard);

        initEstilosSidebar();
        this.setLocationRelativeTo(null);

        // Barra superior de fecha y hora (JCalendar) arriba del encabezado
        javax.swing.JPanel norte = new javax.swing.JPanel(new java.awt.BorderLayout());
        norte.setOpaque(false);
        norte.add(crearBarraFechaHora(), java.awt.BorderLayout.PAGE_START);
        norte.add(panelEncabezado, java.awt.BorderLayout.CENTER);
        panelDerecha.remove(panelEncabezado);
        panelDerecha.add(norte, java.awt.BorderLayout.NORTH);

        // Carga inicial
        mostrarPanel(new FormDashboard());
    }

    /**
     * Abre el módulo de Ventas. Solo la PRIMERA vez que se abre el sistema
     * (por usuario/sesión) pide el ingreso de caja; luego entra directo.
     */
    public void abrirPanelVentas() {
        if (!cajaInicializada) {
            DlgAperturaCaja dlg = new DlgAperturaCaja(this, true);
            dlg.setVisible(true);
            if (!dlg.isCajaAbierta()) {
                return;
            }
            double monto = dlg.getMontoInicial();
            int idUsuario = Sesion.idUsuario > 0 ? Sesion.idUsuario : 1;
            CCaja ccaja = new CCaja();
            int idCaja = ccaja.obtenerCajaAbierta(idUsuario);
            if (idCaja != 0 && !ccaja.esCajaDeHoy(idCaja)) {
                ccaja.cerrarCajaAutomatica(idCaja);
                idCaja = 0;
            }
            if (idCaja == 0) {
                idCaja = ccaja.abrirCaja(idUsuario, monto);
                System.out.println("Caja abierta (id " + idCaja + ") con Gs. " + monto);
            }
            cajaInicializada = true;
        }
        mostrarPanel(new FormVentas());
    }

    private javax.swing.JPanel crearBarraFechaHora() {
        javax.swing.JPanel barra = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 14, 4));
        barra.setBackground(java.awt.Color.WHITE);
        barra.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(245, 235, 224)));

        javax.swing.JLabel lblFecha = new javax.swing.JLabel("FECHA:");
        lblFecha.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblFecha.setForeground(new java.awt.Color(92, 61, 46));

        com.toedter.calendar.JDateChooser jdc = new com.toedter.calendar.JDateChooser();
        jdc.setDate(new java.util.Date());
        jdc.setDateFormatString("dd/MM/yyyy");
        jdc.setEnabled(false);
        jdc.setFont(new java.awt.Font("Segoe UI", 0, 12));

        javax.swing.JLabel lblHora = new javax.swing.JLabel();
        lblHora.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblHora.setForeground(new java.awt.Color(230, 126, 34));
        actualizarHora(lblHora);
        new javax.swing.Timer(1000, e -> actualizarHora(lblHora)).start();

        barra.add(lblFecha);
        barra.add(jdc);
        barra.add(lblHora);
        return barra;
    }

    private void actualizarHora(javax.swing.JLabel lbl) {
        lbl.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    public void mostrarPanel(JPanel panel) {
        panelContenedor.removeAll();
        panelContenedor.setLayout(new java.awt.BorderLayout());
        panelContenedor.add(panel, java.awt.BorderLayout.CENTER);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }

    private void initEstilosSidebar() {
        java.awt.Dimension tamanoBoton = new java.awt.Dimension(200, 42);
        javax.swing.JButton[] todosLosBotones = {
            btnDashboard, btnVentas, btnProductos, btnReportes, btnProveedores, btnClientes, btnUsuarios, btnCaja, btnCerrarSesion
        };

        for (javax.swing.JButton btn : todosLosBotones) {
            btn.setPreferredSize(tamanoBoton);
        }
    }

    // MÉTODO PARA CAMBIAR EL COLOR DEL BOTÓN ACTIVO
    private void marcarBotonActivo(javax.swing.JButton botonSeleccionado) {
        javax.swing.JButton[] botonesMenu = {
            btnDashboard, btnVentas, btnProductos, btnReportes, btnProveedores, btnClientes, btnUsuarios, btnCaja
        };

        for (javax.swing.JButton btn : botonesMenu) {
            if (btn == botonSeleccionado) {
                // ACTIVO: Naranja #E67E22 y texto Blanco
                btn.setBackground(new java.awt.Color(230, 126, 34));
                btn.setForeground(java.awt.Color.WHITE);
            } else {
                // INACTIVO: Café con leche #F5EBE0 y texto Marrón #5C3D2E
                btn.setBackground(new java.awt.Color(245, 235, 224));
                btn.setForeground(new java.awt.Color(92, 61, 46));
            }
        }
    }
    // ---------------------------------------------------------------------
    // PERMISOS POR ROL
    // ---------------------------------------------------------------------

    /**
     * Aplica los permisos del rol actual: el cajero solo ve Dashboard, Ventas,
     * Clientes y Cierre de Caja.
     */
    private void aplicarPermisos() {
        boolean admin = Sesion.esAdmin();

        btnProductos.setVisible(admin);
        btnProveedores.setVisible(admin);
        btnReportes.setVisible(admin);
        btnUsuarios.setVisible(admin);

        if (!admin) {
            reconstruirSidebar();
            // El dashboard se creó antes de conocer el rol; se vuelve a cargar
            // para que también oculte sus accesos rápidos restringidos.
            mostrarPanel(new FormDashboard());
        }

        lblUsuarioActivo.setText(Sesion.nombre + "  ·  " + (admin ? "Administrador" : "Cajero"));
    }

    /**
     * Vuelve a armar el layout del sidebar con los botones visibles, para que
     * el cajero no quede con huecos de los módulos restringidos.
     */
    private void reconstruirSidebar() {
        javax.swing.JButton[] menu = {
            btnDashboard, btnVentas, btnProductos, btnReportes,
            btnProveedores, btnClientes, btnUsuarios, btnCaja
        };

        java.util.List<javax.swing.JButton> visibles = new java.util.ArrayList<>();
        for (javax.swing.JButton boton : menu) {
            if (boton.isVisible()) {
                visibles.add(boton);
            }
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panelSidebar);
        panelSidebar.setLayout(layout);

        javax.swing.GroupLayout.ParallelGroup columna =
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING);
        columna.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190,
                javax.swing.GroupLayout.PREFERRED_SIZE);

        javax.swing.GroupLayout.ParallelGroup botones =
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false);
        for (javax.swing.JButton boton : visibles) {
            botones.addComponent(boton, javax.swing.GroupLayout.DEFAULT_SIZE, 198,
                    Short.MAX_VALUE);
        }
        botones.addComponent(btnCerrarSesion, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        columna.addGroup(botones);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(columna)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout.SequentialGroup fila = layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addGap(91, 91, 91);

        for (int i = 0; i < visibles.size(); i++) {
            if (i > 0) {
                fila.addGap(18, 18, 18);
            }
            fila.addComponent(visibles.get(i), javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
        }

        fila.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14);

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fila)
        );

        panelSidebar.revalidate();
        panelSidebar.repaint();
    }

    /**
     * Valida que el usuario actual sea administrador antes de abrir un módulo
     * restringido (defensa extra por si se invoca el handler directamente).
     */
    private boolean verificarAdmin(String modulo) {
        if (Sesion.esAdmin()) {
            return true;
        }
        javax.swing.JOptionPane.showMessageDialog(this,
                "El módulo \"" + modulo + "\" está disponible solo para administradores.",
                "Acceso restringido", javax.swing.JOptionPane.WARNING_MESSAGE);
        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelSidebar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        btnProductos = new javax.swing.JButton();
        btnReportes = new javax.swing.JButton();
        btnVentas = new javax.swing.JButton();
        btnProveedores = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        btnCaja = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        panelDerecha = new javax.swing.JPanel();
        panelEncabezado = new javax.swing.JPanel();
        lblTituloModulo = new javax.swing.JLabel();
        lblUsuarioActivo = new javax.swing.JLabel();
        panelContenedor = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelSidebar.setBackground(new java.awt.Color(250, 240, 230));
        panelSidebar.setPreferredSize(new java.awt.Dimension(240, 720));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 22)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(92, 61, 46));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Panadería Rojas");

        btnDashboard.setBackground(new java.awt.Color(245, 235, 224));
        btnDashboard.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(92, 61, 46));
        btnDashboard.setText("Panel de Control");
        btnDashboard.setPreferredSize(new java.awt.Dimension(200, 42));
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnProductos.setBackground(new java.awt.Color(245, 235, 224));
        btnProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnProductos.setForeground(new java.awt.Color(92, 61, 46));
        btnProductos.setText("Productos");
        btnProductos.setPreferredSize(new java.awt.Dimension(200, 42));
        btnProductos.addActionListener(this::btnProductosActionPerformed);

        btnReportes.setBackground(new java.awt.Color(245, 235, 224));
        btnReportes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReportes.setForeground(new java.awt.Color(92, 61, 46));
        btnReportes.setText("Reportes");
        btnReportes.setPreferredSize(new java.awt.Dimension(200, 42));
        btnReportes.addActionListener(this::btnReportesActionPerformed);

        btnVentas.setBackground(new java.awt.Color(245, 235, 224));
        btnVentas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnVentas.setForeground(new java.awt.Color(92, 61, 46));
        btnVentas.setText("Ventas");
        btnVentas.setPreferredSize(new java.awt.Dimension(200, 42));
        btnVentas.addActionListener(this::btnVentasActionPerformed);

        btnProveedores.setBackground(new java.awt.Color(245, 235, 224));
        btnProveedores.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnProveedores.setForeground(new java.awt.Color(92, 61, 46));
        btnProveedores.setText("Proveedores");
        btnProveedores.setPreferredSize(new java.awt.Dimension(200, 42));
        btnProveedores.addActionListener(this::btnProveedoresActionPerformed);

        btnClientes.setBackground(new java.awt.Color(245, 235, 224));
        btnClientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClientes.setForeground(new java.awt.Color(92, 61, 46));
        btnClientes.setText("Clientes");
        btnClientes.setPreferredSize(new java.awt.Dimension(200, 42));
        btnClientes.addActionListener(this::btnClientesActionPerformed);

        btnUsuarios.setBackground(new java.awt.Color(245, 235, 224));
        btnUsuarios.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUsuarios.setForeground(new java.awt.Color(92, 61, 46));
        btnUsuarios.setText("Usuarios");
        btnUsuarios.setPreferredSize(new java.awt.Dimension(200, 42));
        btnUsuarios.addActionListener(this::btnUsuariosActionPerformed);

        btnCaja.setBackground(new java.awt.Color(245, 235, 224));
        btnCaja.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCaja.setForeground(new java.awt.Color(92, 61, 46));
        btnCaja.setText("Cierre de Caja");
        btnCaja.setPreferredSize(new java.awt.Dimension(200, 42));
        btnCaja.addActionListener(this::btnCajaActionPerformed);

        btnCerrarSesion.setBackground(new java.awt.Color(217, 136, 128));
        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCerrarSesion.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion.setText("Cerrar Sesión");

        javax.swing.GroupLayout panelSidebarLayout = new javax.swing.GroupLayout(panelSidebar);
        panelSidebar.setLayout(panelSidebarLayout);
        panelSidebarLayout.setHorizontalGroup(
            panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSidebarLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(btnProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addComponent(btnReportes, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addComponent(btnVentas, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addComponent(btnProveedores, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addComponent(btnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addComponent(btnUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addComponent(btnCaja, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addComponent(btnCerrarSesion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        panelSidebarLayout.setVerticalGroup(
            panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSidebarLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addGap(91, 91, 91)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        getContentPane().add(panelSidebar, java.awt.BorderLayout.WEST);

        panelDerecha.setBackground(new java.awt.Color(243, 244, 246));
        panelDerecha.setLayout(new java.awt.BorderLayout());

        panelEncabezado.setBackground(new java.awt.Color(255, 255, 255));
        panelEncabezado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(245, 235, 224)));
        panelEncabezado.setPreferredSize(new java.awt.Dimension(1040, 60));
        panelEncabezado.setVerifyInputWhenFocusTarget(false);

        lblTituloModulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTituloModulo.setFont(new java.awt.Font("Segoe UI Black", 1, 22)); // NOI18N
        lblTituloModulo.setForeground(new java.awt.Color(92, 61, 46));
        lblTituloModulo.setText("Panel de Control");

        lblUsuarioActivo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblUsuarioActivo.setForeground(new java.awt.Color(92, 61, 46));
        lblUsuarioActivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsuarioActivo.setText("Admin");

        javax.swing.GroupLayout panelEncabezadoLayout = new javax.swing.GroupLayout(panelEncabezado);
        panelEncabezado.setLayout(panelEncabezadoLayout);
        panelEncabezadoLayout.setHorizontalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(lblTituloModulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 378, Short.MAX_VALUE)
                .addComponent(lblUsuarioActivo, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        panelEncabezadoLayout.setVerticalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloModulo)
                    .addComponent(lblUsuarioActivo))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        panelDerecha.add(panelEncabezado, java.awt.BorderLayout.NORTH);

        panelContenedor.setBackground(new java.awt.Color(253, 251, 247));
        panelContenedor.setLayout(new java.awt.BorderLayout());
        panelDerecha.add(panelContenedor, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelDerecha, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresActionPerformed
        if (!verificarAdmin("Proveedores")) {
            return;
        }
    marcarBotonActivo(btnProveedores);
        lblTituloModulo.setText("Proveedores");
        mostrarPanel(new FormProveedores());
    }//GEN-LAST:event_btnProveedoresActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        marcarBotonActivo(btnClientes);
        lblTituloModulo.setText("Clientes");
        mostrarPanel(new FormClientes());
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        if (!verificarAdmin("Reportes")) {
            return;
        }
        marcarBotonActivo(btnReportes);
        lblTituloModulo.setText("Reportes");
        mostrarPanel(new FormReportes());
    }//GEN-LAST:event_btnReportesActionPerformed

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuariosActionPerformed
marcarBotonActivo(btnUsuarios);
        lblTituloModulo.setText("Usuarios");
        DlgCambiarContrasena dialog = new DlgCambiarContrasena(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnUsuariosActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
marcarBotonActivo(btnDashboard);
        lblTituloModulo.setText("Panel de Control");
        mostrarPanel(new FormDashboard());

            // TODO add your handling code here:
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductosActionPerformed
        if (!verificarAdmin("Productos")) {
            return;
        }
        marcarBotonActivo(btnProductos);
        lblTituloModulo.setText("Inventario de Productos");
        mostrarPanel(new FormProductos());

    }//GEN-LAST:event_btnProductosActionPerformed

    private void btnVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasActionPerformed
marcarBotonActivo(btnVentas);
    lblTituloModulo.setText("Ventas");
    abrirPanelVentas();
    }//GEN-LAST:event_btnVentasActionPerformed

    private void btnCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCajaActionPerformed
        marcarBotonActivo(btnCaja);
        lblTituloModulo.setText("Cierre de Caja");
        DlgCierreCaja dlg = new DlgCierreCaja(this, true);
        dlg.setVisible(true);
    }//GEN-LAST:event_btnCajaActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        int opcion = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar la sesión actual?", "Cerrar Sesión",
                javax.swing.JOptionPane.YES_NO_OPTION);
        if (opcion == javax.swing.JOptionPane.YES_OPTION) {
            Sesion.limpiar();
            FormLogin login = new FormLogin();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed


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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCaja;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JButton btnReportes;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JButton btnVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblTituloModulo;
    private javax.swing.JLabel lblUsuarioActivo;
    private javax.swing.JPanel panelContenedor;
    private javax.swing.JPanel panelDerecha;
    private javax.swing.JPanel panelEncabezado;
    private javax.swing.JPanel panelSidebar;
    // End of variables declaration//GEN-END:variables
}
