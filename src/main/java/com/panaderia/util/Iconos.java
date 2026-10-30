package com.panaderia.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Íconos usados en la interfaz.
 *
 * Los íconos de ojo (abierto / cerrado) provienen de Google Material Design
 * Icons (https://github.com/google/material-design-icons), licencia Apache 2.0.
 *
 * @author Lilian
 */
public final class Iconos {

    private static final int TAMANO = 20;

    private Iconos() {
    }

    /**
     * @return ícono de ojo abierto (contraseña visible).
     */
    public static ImageIcon ojoAbierto() {
        return cargar("/iconos/ojo_abierto.png");
    }

    /**
     * @return ícono de ojo cerrado/tachado (contraseña oculta).
     */
    public static ImageIcon ojoCerrado() {
        return cargar("/iconos/ojo_cerrado.png");
    }

    /**
     * Carga un PNG (que puede ser negro con transparencia) y lo pinta del color
     * indicado, conservando la transparencia. Útil para usar los íconos de
     * Material Design sobre fondos oscuros o de color.
     *
     * @param ruta  ruta del recurso, por ejemplo {@code /iconos/ventas.png}
     * @param color color con el que se pinta el ícono
     * @param tamano tamaño deseado en píxeles (ancho y alto)
     * @return el ícono pintado, o {@code null} si no se encuentra el recurso
     */
    public static ImageIcon tintado(String ruta, Color color, int tamano) {
        URL url = Iconos.class.getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró el ícono: " + ruta);
            return null;
        }
        ImageIcon original = new ImageIcon(url);
        int ancho = original.getIconWidth();
        int alto = original.getIconHeight();
        if (ancho <= 0 || alto <= 0) {
            return null;
        }
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.drawImage(original.getImage(), 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, ancho, alto);
        g.dispose();
        Image escalada = imagen.getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }

    private static ImageIcon cargar(String ruta) {
        URL url = Iconos.class.getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró el ícono: " + ruta);
            return null;
        }
        ImageIcon original = new ImageIcon(url);
        Image escalada = original.getImage().getScaledInstance(TAMANO, TAMANO, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }
}
