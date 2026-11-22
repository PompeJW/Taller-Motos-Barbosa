package org.biker.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RequiredStatusInitializer {
    @Bean
    @Order(0)
    ApplicationRunner ensureRequiredCatalogs(JdbcTemplate jdbc) {
        return args -> {
            for (String name : new String[]{"Cotizado", "En Progreso", "Pendiente Pago", "Finalizado", "Detenido", "Inventariado", "Descontinuado"}) {
                jdbc.update("INSERT INTO status (name) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM status WHERE lower(trim(name)) = lower(?))", name, name);
            }
            // Los nombres corresponden a la semántica usada por los servicios actuales.
            for (String name : new String[]{"Abonado", "Pagado", "Pendiente Pago"}) {
                jdbc.update("INSERT INTO work_order_payment_status (status) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM work_order_payment_status WHERE lower(trim(status)) = lower(?))", name, name);
            }
            // Categorías obligatorias utilizadas por SaleService y PaymentService.
            jdbc.update("INSERT INTO movement_category (name, code) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM movement_category WHERE code = ?)", "Venta de repuestos", "VENTA_REPUESTOS", "VENTA_REPUESTOS");
            jdbc.update("INSERT INTO movement_category (name, code) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM movement_category WHERE code = ?)", "Servicios de taller", "SERVICIOS_TALLER", "SERVICIOS_TALLER");
            // No insertar métodos de pago sin conocer la política comercial: se gestionan desde la aplicación.
        };
    }
}
