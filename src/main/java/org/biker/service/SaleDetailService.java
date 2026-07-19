package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.model.SaleDetail;
import org.biker.model.SparePart;
import org.biker.repository.SaleDetailRepository;
import org.biker.repository.SparePartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final SparePartRepository sparePartRepository;

    /**
     * Obtener todos los detalles de una venta específica
     */
    public List<SaleDetail> findBySaleId(Long saleId) {
        return saleDetailRepository.findBySaleId(saleId);
    }

    /**
     * Obtener todos los detalles donde aparece un repuesto específico
     * (útil para reportes de productos más vendidos)
     */
    public List<SaleDetail> findBySparePartId(Long sparePartId) {
        return saleDetailRepository.findBySparePartId(sparePartId);
    }

    /**
     * Devolver productos vendidos (ejemplo: cliente devuelve parte de la compra)
     */
    public void returnItem(Long detailId, Integer quantityToReturn) {
        SaleDetail detail = saleDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));

        if (quantityToReturn <= 0) {
            throw new RuntimeException("La cantidad a devolver debe ser mayor a cero");
        }

        if (quantityToReturn > detail.getQuantity()) {
            throw new RuntimeException("No se puede devolver más de lo vendido");
        }

        // Actualizar stock del repuesto
        SparePart sparePart = detail.getSparePart();
        sparePart.setStock(sparePart.getStock() + quantityToReturn);

        // Actualizar cantidad del detalle
        int newQuantity = detail.getQuantity() - quantityToReturn;
        if (newQuantity == 0) {
            // Si ya no queda nada, eliminar el detalle
            saleDetailRepository.delete(detail);
        } else {
            detail.setQuantity(newQuantity);
            detail.setSubtotal(detail.getUnitPrice().multiply(
                    java.math.BigDecimal.valueOf(newQuantity)
            ));
            saleDetailRepository.save(detail);
        }
    }
}
