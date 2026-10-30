/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.panaderia.panaderiarojas;

import com.panaderia.formularios.FormLogin;

public class PanaderiaRojas {

    public static void main(String[] args) {
        // 1. Usar el LookAndFeel nativo del sistema operativo
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("No se pudo cargar el LookAndFeel nativo: " + ex.getMessage());
        }

        // 2. Ejecutar la interfaz en el hilo de eventos de Swing
        java.awt.EventQueue.invokeLater(() -> {
            // Abrir el formulario de Login
            new FormLogin().setVisible(true);
        });
    }
}