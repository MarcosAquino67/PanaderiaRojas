package com.panaderia.clases;

import com.panaderia.conexion.CConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controla la apertura y el cierre de la caja registradora.
 */
public class CCaja {

    /**
     * Registra la apertura de caja para el usuario dado.
     *
     * @return id de la caja creada o -1 si falla.
     */
    public int abrirCaja(int idUsuario, double montoInicial) {
        String sql = "INSERT INTO caja (id_usuario, monto_inicial, estado) VALUES (?, ?, 'ABIERTA')";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUsuario);
            ps.setDouble(2, montoInicial);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al abrir caja: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Busca la caja ABIERTA del usuario actual.
     *
     * @return idCaja o 0 si no hay caja abierta.
     */
    public int obtenerCajaAbierta(int idUsuario) {
        String sql = "SELECT id_caja FROM caja WHERE id_usuario = ? AND estado = 'ABIERTA' "
                   + "ORDER BY fecha_apertura DESC LIMIT 1";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_caja");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar caja abierta: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Datos de la caja para el diálogo de cierre.
     *
     * @return Object[] {fechaApertura, montoInicial, montoVentasEfectivo,
     *         montoFinalEsperado} o null si no encuentra la caja.
     */
    public Object[] obtenerResumenCaja(int idCaja) {
        String sql = "SELECT fecha_apertura, monto_inicial, monto_ventas_efectivo, monto_final_esperado FROM caja WHERE id_caja = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCaja);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getTimestamp("fecha_apertura"),
                        rs.getDouble("monto_inicial"),
                        rs.getDouble("monto_ventas_efectivo"),
                        rs.getDouble("monto_final_esperado")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar resumen de caja: " + e.getMessage());
        }
        return null;
    }

    /**
     * Suma las ventas en EFECTIVO realizadas desde la apertura de la caja.
     */
    public double obtenerVentasEfectivo(int idCaja) {
        String sql = "SELECT COALESCE(SUM(v.total_venta), 0) AS total "
                   + "FROM venta v WHERE v.id_forma_pago = 1 "
                   + "AND v.fecha >= (SELECT fecha_apertura FROM caja WHERE id_caja = ?)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCaja);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al sumar ventas en efectivo: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Suma TODAS las ventas realizadas desde la apertura de la caja
     * (efectivo, transferencia, tarjeta, QR).
     */
    public double obtenerVentasDelDia(int idCaja) {
        String sql = "SELECT COALESCE(SUM(v.total_venta), 0) AS total "
                   + "FROM venta v WHERE v.fecha >= (SELECT fecha_apertura FROM caja WHERE id_caja = ?)";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCaja);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al sumar ventas del día: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Indica si la caja fue abierta el día de hoy (según la fecha del servidor).
     */
    public boolean esCajaDeHoy(int idCaja) {
        String sql = "SELECT DATE(fecha_apertura) = CURDATE() AS esHoy FROM caja WHERE id_caja = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCaja);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("esHoy");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar fecha de caja: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cierra automáticamente una caja que quedó abierta de otro día,
     * dejando el resumen coherente (sin que el usuario la cierre manualmente).
     */
    public boolean cerrarCajaAutomatica(int idCaja) {
        Object[] resumen = obtenerResumenCaja(idCaja);
        if (resumen == null) {
            return false;
        }
        double inicial = resumen[1] == null ? 0 : (Double) resumen[1];
        double ventas = obtenerVentasDelDia(idCaja);
        double esperado = inicial + ventas;

        String sql = "UPDATE caja SET fecha_cierre = NOW(), monto_ventas_efectivo = ?, "
                   + "monto_final_esperado = ?, monto_final_real = ?, diferencia = 0, estado = 'CERRADA' "
                   + "WHERE id_caja = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, ventas);
            ps.setDouble(2, esperado);
            ps.setDouble(3, esperado);
            ps.setInt(4, idCaja);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cerrar caja automáticamente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra la caja calculando la diferencia entre lo esperado y lo real.
     *
     * @return true si el cierre se registró correctamente.
     */
    public boolean cerrarCaja(int idCaja, double montoInicial, double ventasEfectivo, double montoFinalReal) {
        double esperado = montoInicial + ventasEfectivo;
        double diferencia = montoFinalReal - esperado;

        String sql = "UPDATE caja SET fecha_cierre = NOW(), monto_ventas_efectivo = ?, "
                   + "monto_final_esperado = ?, monto_final_real = ?, diferencia = ?, estado = 'CERRADA' "
                   + "WHERE id_caja = ?";
        try (Connection con = CConexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, ventasEfectivo);
            ps.setDouble(2, esperado);
            ps.setDouble(3, montoFinalReal);
            ps.setDouble(4, diferencia);
            ps.setInt(5, idCaja);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cerrar caja: " + e.getMessage());
            return false;
        }
    }
}