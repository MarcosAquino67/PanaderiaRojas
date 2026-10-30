package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controla la configuración del ticket de venta.
 */
public class CConfiguracion {

    /**
     * Obtiene la configuración del ticket. Si no existe ninguna fila usa
     * valores por defecto.
     *
     * @return Object[] {nombreNegocio, direccion, telefono, mensajePie}
     */
    public Object[] obtenerConfigTicket() {
        String sql = "SELECT nombre_negocio, direccion, telefono, mensaje_pie FROM config_ticket WHERE id_config = 1";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Object[]{
                    rs.getString("nombre_negocio"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("mensaje_pie")
                };
            }
        } catch (SQLException e) {
            System.err.println("Error al leer configuración del ticket: " + e.getMessage());
        }
        return new Object[]{"Panaderia Rojas", "Av. Principal s/n", "021 000 000", "Gracias por su compra"};
    }

    /**
     * Guarda la configuración del ticket (fila 1).
     *
     * @return true si se actualizó correctamente.
     */
    public boolean guardarConfigTicket(String nombreNegocio, String direccion, String telefono, String mensajePie) {
        String sql = "INSERT INTO config_ticket (id_config, nombre_negocio, direccion, telefono, mensaje_pie) "
                   + "VALUES (1, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE nombre_negocio = VALUES(nombre_negocio), "
                   + "direccion = VALUES(direccion), telefono = VALUES(telefono), mensaje_pie = VALUES(mensaje_pie)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreNegocio);
            ps.setString(2, direccion);
            ps.setString(3, telefono);
            ps.setString(4, mensajePie);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar configuración del ticket: " + e.getMessage());
            return false;
        }
    }
}