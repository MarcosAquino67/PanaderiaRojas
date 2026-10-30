# PanaderiaRojas

Sistema de gestión para una panadería, desarrollado en Java Swing con MySQL.

## Requisitos

- JDK 26 (según la configuración actual de Maven en `pom.xml`).
- MySQL Server.
- Maven, o un IDE compatible como Apache NetBeans.

## Configuración de la base de datos

1. Crea en MySQL la base `bd_panaderia_rojas` e importa el esquema/datos del proyecto.
2. Configura la contraseña de MySQL como variable de entorno **`PANADERIA_DB_PASSWORD`**. No guardes credenciales en el código ni en Git.
3. Opcionalmente puedes definir `PANADERIA_DB_USER`, `PANADERIA_DB_HOST`, `PANADERIA_DB_PORT` y `PANADERIA_DB_NAME`. Por defecto se usa el usuario `root`, `localhost`, el puerto `3306` y la base `bd_panaderia_rojas`.

En PowerShell, para la sesión actual:

```powershell
$env:PANADERIA_DB_PASSWORD = "tu_contraseña_local"
```

## Abrir y ejecutar

- En NetBeans: **File → Open Project**, selecciona la carpeta del proyecto y ejecuta `PanaderiaRojas`.
- Con Maven: `mvn clean package` y luego `mvn exec:java`.
- La clase principal es `com.panaderia.panaderiarojas.PanaderiaRojas`.

Los formularios están en `src/main/java/com/panaderia/formularios`, la lógica de negocio en `clases` y la conexión JDBC en `conexion`.
