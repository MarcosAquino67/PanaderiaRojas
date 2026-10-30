/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Lilian
 */
public class CUsuarios {

    /**
     * Valida las credenciales de un usuario contra la base de datos.
     *
     * @param cedula     Cédula (sin puntos) del usuario
     * @param contrasena Contraseña ingresada
     * @return Object[] {id_usuario, nombre, rol} si las credenciales son
     *         válidas, o null si la cédula/contraseña es incorrecta.
     */
    public Object[] validarCredenciales(String cedula, String contrasena) {
        String sql = "SELECT id_usuario, nombre, rol FROM usuario WHERE cedula = ? AND contrasena = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula.trim());
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("rol")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }
        return null;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param nombre     Nombre completo
     * @param cedula     Cédula de identidad (única)
     * @param contrasena Contraseña
     * @param rol        Rol (ADMIN / CAJERO)
     * @param idSexo     Referencia a la tabla sexo
     * @param idCiudad   Referencia a la tabla ciudad
     * @return true si se insertó correctamente
     */
    public boolean guardarUsuario(String nombre, String cedula, String contrasena, String rol, int idSexo, int idCiudad) {
        String sql = "INSERT INTO usuario (nombre, cedula, contrasena, rol, id_sexo, id_ciudad) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, cedula);
            ps.setString(3, contrasena);
            ps.setString(4, rol);
            ps.setInt(5, idSexo);
            ps.setInt(6, idCiudad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todos los usuarios registrados.
     *
     * @return lista de Object[] {id_usuario, nombre, cedula, rol}.
     */
    public java.util.List<Object[]> obtenerUsuarios() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT id_usuario, nombre, cedula, rol FROM usuario ORDER BY nombre";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("cedula"),
                    rs.getString("rol")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Cambia la contraseña de un usuario a partir de su cédula (reset de admin).
     *
     * @return true si se actualizó alguna fila.
     */
    public boolean cambiarContrasena(String cedula, String nueva) {
        String sql = "UPDATE usuario SET contrasena = ? WHERE cedula = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nueva);
            ps.setString(2, cedula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cambia la contraseña del propio usuario validando la contraseña actual.
     *
     * @return true si la contraseña actual era correcta y se actualizó.
     */
    public boolean cambiarContrasenaPropia(int idUsuario, String actual, String nueva) {
        String sql = "UPDATE usuario SET contrasena = ? WHERE id_usuario = ? AND contrasena = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nueva);
            ps.setInt(2, idUsuario);
            ps.setString(3, actual);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña propia: " + e.getMessage());
            return false;
        }
    }
}