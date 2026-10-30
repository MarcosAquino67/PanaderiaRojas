-- Relaciona cada venta con la configuración del ticket utilizada.
-- La configuración existente usa id_config = 1; ventas históricas mantienen
-- esa configuración al agregarse la columna con DEFAULT 1.
USE bd_panaderia_rojas;

ALTER TABLE venta
    ADD COLUMN id_config INT NOT NULL DEFAULT 1 AFTER id_forma_pago,
    ADD INDEX idx_venta_id_config (id_config),
    ADD CONSTRAINT fk_venta_config_ticket
        FOREIGN KEY (id_config)
        REFERENCES config_ticket (id_config)
        ON DELETE RESTRICT
        ON UPDATE CASCADE;
