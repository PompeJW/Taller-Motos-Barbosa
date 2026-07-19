package org.biker.repository;

import org.biker.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Buscar ventas por cliente
    List<Sale> findByClientId(Long clientId);

    // Buscar ventas por rango de fechas
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    // Buscar por número de venta
    List<Sale> findBySaleNumberContainingIgnoreCase(String saleNumber);
}
