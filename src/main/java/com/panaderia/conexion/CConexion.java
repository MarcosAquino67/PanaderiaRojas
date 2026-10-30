/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.panaderia.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CConexion {

    // Configuración de parámetros de conexión a MySQL
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "2307";
    private static final String BD = "bd_panaderia_rojas";
    private static final String IP = "localhost";
    private static final String PUERTO = "3306";

    // Cadena de conexión JDBC
    private static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + BD 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection establecerConexion() {
        Connection conectar = null;
        try {
            // Cargar el driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer la conexión
            conectar = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: No se encontró el driver JDBC de MySQL.\nAsegúrate de agregar la librería 'mysql-connector-j' a tu proyecto.", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Driver no encontrado: " + e.getMessage());
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error al conectar con la base de datos 'bd_panaderia_rojas'.\nVerifica que MySQL esté iniciado y la contraseña sea correcta.", 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error SQL: " + e.getMessage());
        }
        
        return conectar;
    }
}
