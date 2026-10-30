# PanaderiaRojas

Sistema de gestión para una panadería, desarrollado en Java Swing con MySQL.

## Requisitos

- JDK 26 (según la configuración actual de Maven en `pom.xml`).
- MySQL Server.
- Maven, o un IDE compatible como Apache NetBeans.

## Configuración de la base de datos

1. Crea en MySQL la base `bd_panaderia_rojas` e importa el esquema/datos del proyecto.
2. La conexión local está configurada en `src/main/java/com/panaderia/conexion/CConexion.java`. Ajusta allí usuario, contraseña, host, puerto y base de datos según tu instalación.

## Abrir y ejecutar

- En NetBeans: **File → Open Project**, selecciona la carpeta del proyecto y ejecuta `PanaderiaRojas`.
- Con Maven: `mvn clean package` y luego `mvn exec:java`.
- La clase principal es `com.panaderia.panaderiarojas.PanaderiaRojas`.

Los formularios están en `src/main/java/com/panaderia/formularios`, la lógica de negocio en `clases` y la conexión JDBC en `conexion`.
