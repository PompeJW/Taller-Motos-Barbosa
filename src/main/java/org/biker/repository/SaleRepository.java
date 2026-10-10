package org.biker.repository;

import org.biker.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Buscar ventas por cliente
    List<Sale> findByClientId(Long clientId);

    // Buscar ventas por rango de fechas
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    // Buscar por número de venta
    List<Sale> findBySaleNumberContainingIgnoreCase(String saleNumber);

    @Query("SELECT s FROM Sale s LEFT JOIN FETCH s.details WHERE s.id = :id")
    Optional<Sale> findByIdWithDetails(@Param("id") Long id);

}
