/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.panaderia.formularios;

import com.panaderia.clases.CUsuarios;
import com.panaderia.clases.Sesion;
import java.awt.Color;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class FormLogin extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormLogin.class.getName());

    /**
     * Creates new form FormLogin
     */
    public FormLogin() {
    initComponents();
    // Fondo de la ventana y el panel contenedor
this.getContentPane().setBackground(new java.awt.Color(253, 251, 247));
panelContenedor.setBackground(new java.awt.Color(255, 248, 240));

// Título "Panadería Rojas" y etiquetas
lblTitulo.setForeground(new java.awt.Color(92, 61, 46));
lblCedula.setForeground(new java.awt.Color(92, 61, 46));
lblPassword.setForeground(new java.awt.Color(92, 61, 46));

// Botón "Iniciar Sesión" (Naranja con texto Blanco)
estilarBoton(btnIniciarSesion, new java.awt.Color(230, 126, 34), java.awt.Color.WHITE);

btnIniciarSesion.setContentAreaFilled(false);
btnIniciarSesion.setOpaque(true);

    
    // Centrar ventana
    this.setLocationRelativeTo(null);
    
    // Configurar color de fondo general (Gris claro)
    panelFondo.setBackground(new java.awt.Color(243, 244, 246));
    
    // Aplicar bordes redondeados y sombra al panel blanco
    // (el LookAndFeel nativo maneja el aspecto visual)
    
    // Fondo blanco, bordes redondeados y margen interno para los campos
    txtUsuario.setBackground(java.awt.Color.WHITE);
    
    txtContrasena.setBackground(java.awt.Color.WHITE);
    
    // Bordes redondeados y quitar el contorno negro del botón
    // (el LookAndFeel nativo maneja el aspecto visual)
        // Array con todos tus campos de contraseña del formulario
javax.swing.JPasswordField[] camposPassword = { txtContrasena }; // pon aquí los nombres de tus variables

for (javax.swing.JPasswordField pwd : camposPassword) {
    if (pwd != null) {
        pwd.putClientProperty("JPasswordField.showRevealButton", true);
        pwd.putClientProperty("JTextField.placeholderText", "••••••••");
    }
}

    // Formatear cédula con puntos de miles automáticamente mientras se escribe
    ((javax.swing.text.AbstractDocument) txtUsuario.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (string == null) return;
            aplicarFormato(fb);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder builder = new StringBuilder(actual);
            builder.replace(offset, offset + length, text != null ? text : "");
            
            // Extraer solo los dígitos del texto ingresado
            String soloNumeros = builder.toString().replaceAll("\\D", "");
            
            // Limitar a máximo 8 dígitos (formato estándar PY)
            if (soloNumeros.length() <= 8) {
                if (soloNumeros.isEmpty()) {
                    fb.replace(0, fb.getDocument().getLength(), "", attrs);
                } else {
                    // Convertir a formato con puntos (ej: 6.533.925)
                    long numero = Long.parseLong(soloNumeros);
                    String formateado = String.format(new java.util.Locale("es", "PY"), "%,d", numero);
                    fb.replace(0, fb.getDocument().getLength(), formateado, attrs);
                }
            }
        }

        private void aplicarFormato(FilterBypass fb) throws javax.swing.text.BadLocationException {
            // Utilizado para mantener la consistencia en pegado e inserción
        }
    });

    // Al presionar Enter en la contraseña se dispara el mismo flujo que el botón
    txtContrasena.addActionListener(e -> btnIniciarSesion.doClick());
    configurarBotonMostrar();
}
    public static void estilarBoton(javax.swing.JButton btn, java.awt.Color bg, java.awt.Color fg) {
    btn.setContentAreaFilled(false);
    btn.setOpaque(true);
    btn.setBackground(bg);
    btn.setForeground(fg);
    btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JPanel();
        panelContenedor = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblCedula = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        txtContrasena = new javax.swing.JPasswordField();
        btnIniciarSesion = new javax.swing.JButton();
        btnMostrar = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(243, 244, 246));
        setResizable(false);
        setSize(new java.awt.Dimension(550, 500));

        panelFondo.setBackground(new java.awt.Color(253, 251, 247));
        panelFondo.setForeground(new java.awt.Color(243, 244, 246));

        panelContenedor.setBackground(new java.awt.Color(255, 248, 240));
        panelContenedor.setPreferredSize(new java.awt.Dimension(420, 380));

        jPanel1.setBackground(new java.awt.Color(233, 234, 237));
        jPanel1.setOpaque(false);

        lblTitulo.setFont(new java.awt.Font("Serif", 1, 26)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(92, 61, 46));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Panadería Rojas");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitulo)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        lblCedula.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblCedula.setForeground(new java.awt.Color(0, 0, 0));
        lblCedula.setText("Cedula");

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(0, 0, 0));
        lblPassword.setText("Password");

        txtUsuario.setBackground(new java.awt.Color(255, 255, 255));
        txtUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtUsuario.setForeground(new java.awt.Color(0, 0, 0));
        txtUsuario.addActionListener(this::txtUsuarioActionPerformed);

        txtContrasena.setBackground(new java.awt.Color(255, 255, 255));
        txtContrasena.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        btnIniciarSesion.setBackground(new java.awt.Color(230, 126, 34));
        btnIniciarSesion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnIniciarSesion.setForeground(new java.awt.Color(0, 0, 0));
        btnIniciarSesion.setText("Iniciar Sesión");
        btnIniciarSesion.addActionListener(this::btnIniciarSesionActionPerformed);

        btnMostrar.setBackground(new java.awt.Color(253, 251, 247));
        btnMostrar.addActionListener(this::btnMostrarActionPerformed);

        javax.swing.GroupLayout panelContenedorLayout = new javax.swing.GroupLayout(panelContenedor);
        panelContenedor.setLayout(panelContenedorLayout);
        panelContenedorLayout.setHorizontalGroup(
            panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelContenedorLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCedula, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnIniciarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario)
                    .addComponent(txtContrasena)
                    .addComponent(lblPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1, 1, 1)
                .addComponent(btnMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelContenedorLayout.setVerticalGroup(
            panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContenedorLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(btnIniciarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(panelContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(panelContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // Enter en la cédula dispara el mismo flujo que el botón
        btnIniciarSesion.doClick();
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void btnIniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarSesionActionPerformed
                                           
   // 1. Obtener la cédula sin puntos y la contraseña limpia
    String cedulaLimpia = txtUsuario.getText().replace(".", "").trim();
    String contrasena = new String(txtContrasena.getPassword()).trim();

    // 2. Validar formato de Cédula Paraguaya (mínimo 6 dígitos, máximo 8)
    if (cedulaLimpia.isEmpty() || cedulaLimpia.length() < 6) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Ingrese un número de cédula válido (entre 6 y 8 dígitos).", 
            "Cédula Incorrecta", 
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtUsuario.requestFocus();
        return;
    }

    // 3. Validar campo de contraseña
    if (contrasena.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Por favor, ingrese su contraseña.", 
            "Campo Requerido", 
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtContrasena.requestFocus();
        return;
    }

    // 4. Validar credenciales contra la base de datos
    Object[] usuario = new CUsuarios().validarCredenciales(cedulaLimpia, contrasena);

    if (usuario == null) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Cédula o contraseña incorrecta. Verifique sus datos.", 
            "Acceso Denegado", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
        txtUsuario.requestFocus();
        return;
    }

    int idUsuario = (Integer) usuario[0];
    String nombre = (String) usuario[1];
    String rol = (String) usuario[2];

    Sesion.iniciar(idUsuario, nombre, rol);

    // Abrir MenuPrincipal con el usuario logueado y cerrar Login
    MenuPrincipal menu = new MenuPrincipal(idUsuario, nombre, rol);
    menu.setVisible(true);
    this.dispose();

       // TODO add your handling code here:
    }//GEN-LAST:event_btnIniciarSesionActionPerformed

    private void btnMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarActionPerformed
        if (btnMostrar.isSelected()) {
            // Mostrar contraseña
            txtContrasena.setEchoChar((char) 0);
            btnMostrar.setIcon(com.panaderia.util.Iconos.ojoCerrado());
        } else {
            // Ocultar contraseña
            txtContrasena.setEchoChar('•');
            btnMostrar.setIcon(com.panaderia.util.Iconos.ojoAbierto());
        }
    }//GEN-LAST:event_btnMostrarActionPerformed

    private void configurarBotonMostrar() {
        btnMostrar.setText("");
        btnMostrar.setIcon(com.panaderia.util.Iconos.ojoAbierto());
        btnMostrar.setToolTipText("Mostrar / ocultar contraseña");
        btnMostrar.setBackground(new java.awt.Color(255, 248, 240));
        btnMostrar.setContentAreaFilled(false);
        btnMostrar.setOpaque(true);
        btnMostrar.setFocusPainted(false);
        btnMostrar.setBorderPainted(false);
        btnMostrar.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(230, 205, 180), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(3, 6, 3, 6)));
    }

    /**
     * @param args the command line arguments
     */ public static void main(String args[]) {
    /* Activar Nimbus para que respete los colores del diseñador visual */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception ex) {
        logger.log(java.util.logging.Level.SEVERE, null, ex);
    }

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(() -> new FormLogin().setVisible(true));
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciarSesion;
    private javax.swing.JToggleButton btnMostrar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCedula;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelContenedor;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
