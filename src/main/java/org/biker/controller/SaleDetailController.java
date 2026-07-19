package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.model.SaleDetail;
import org.biker.service.SaleDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-details") // Ruta base para detalles de venta
@RequiredArgsConstructor
public class SaleDetailController {

    private final SaleDetailService saleDetailService;

    /**
     * Obtener todos los detalles de una venta específica
     * Endpoint: GET /api/sale-details/sale/{saleId}
     */
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<SaleDetail>> getDetailsBySale(@PathVariable Long saleId) {
        List<SaleDetail> details = saleDetailService.findBySaleId(saleId);
        return ResponseEntity.ok(details);
    }

    /**
     * Obtener todos los detalles donde aparece un repuesto específico
     * Endpoint: GET /api/sale-details/spare-part/{sparePartId}
     */
    @GetMapping("/spare-part/{sparePartId}")
    public ResponseEntity<List<SaleDetail>> getDetailsBySparePart(@PathVariable Long sparePartId) {
        List<SaleDetail> details = saleDetailService.findBySparePartId(sparePartId);
        return ResponseEntity.ok(details);
    }

    /**
     * Devolver productos vendidos (ejemplo: cliente devuelve parte de la compra)
     * Endpoint: POST /api/sale-details/{detailId}/return
     */
    @PostMapping("/{detailId}/return")
    public ResponseEntity<String> returnItem(
            @PathVariable Long detailId,
            @RequestParam Integer quantityToReturn) {

        saleDetailService.returnItem(detailId, quantityToReturn);
        return ResponseEntity.ok("Devolución realizada correctamente");
    }
}
