package org.biker.repository;

import org.biker.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    // Buscar detalles por venta
    List<SaleDetail> findBySaleId(Long saleId);

    // Buscar detalles por repuesto
    List<SaleDetail> findBySparePartId(Long sparePartId);
}
