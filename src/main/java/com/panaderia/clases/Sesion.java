package com.panaderia.clases;

/**
 * Mantiene en memoria los datos del usuario que inició sesión.
 */
public final class Sesion {

    public static int idUsuario = -1;
    public static String nombre = "Admin";
    public static String rol = "ADMIN";

    private Sesion() {
    }

    public static void iniciar(int idUsuario, String nombre, String rol) {
        Sesion.idUsuario = idUsuario;
        Sesion.nombre = nombre;
        Sesion.rol = rol;
    }

    public static void limpiar() {
        idUsuario = -1;
        nombre = "Admin";
        rol = "ADMIN";
    }

    /**
     * Indica si el usuario de la sesión tiene rol de administrador.
     *
     * @return true si el rol es ADMIN
     */
    public static boolean esAdmin() {
        return rol != null && "ADMIN".equalsIgnoreCase(rol.trim());
    }

    /**
     * Indica si el usuario de la sesión es cajero.
     *
     * @return true si el rol es CAJERO
     */
    public static boolean esCajero() {
        return rol != null && "CAJERO".equalsIgnoreCase(rol.trim());
    }
}