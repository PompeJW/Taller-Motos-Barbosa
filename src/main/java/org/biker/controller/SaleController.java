package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SaleRequest;
import org.biker.dto.SaleResponse;
import org.biker.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales") // Ruta base para el módulo de ventas
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    /**
     * Registrar una nueva venta
     * Endpoint: POST /api/sales
     */
    @PostMapping
    public ResponseEntity<SaleResponse> registerSale(@RequestBody SaleRequest request) {
        SaleResponse response = saleService.registerSale(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las ventas
     * Endpoint: GET /api/sales
     */
    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        List<SaleResponse> sales = saleService.findAll();
        return ResponseEntity.ok(sales);
    }

    /**
     * Obtener ventas por cliente
     * Endpoint: GET /api/sales/client/{clientId}
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<SaleResponse>> getSalesByClient(@PathVariable Long clientId) {
        List<SaleResponse> sales = saleService.findByClient(clientId);
        return ResponseEntity.ok(sales);
    }

    /**
     * Obtener una venta específica por ID
     * Endpoint: GET /api/sales/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
        SaleResponse sale = saleService.findById(id);
        return ResponseEntity.ok(sale);
    }

}
