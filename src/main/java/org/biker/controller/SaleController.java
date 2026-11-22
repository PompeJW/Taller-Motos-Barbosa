package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SaleRequest;
import org.biker.dto.SaleResponse;
import org.biker.service.PrinterService;
import org.biker.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales") // Ruta base para el módulo de ventas
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final PrinterService printerService;
    private final org.biker.service.PrintQueueService printQueueService;

    /**
     * Registrar una nueva venta
     * Endpoint: POST /api/sales
     */
    @PostMapping
    public ResponseEntity<SaleResponse> registerSale(@RequestBody SaleRequest request) {
        SaleResponse response = saleService.registerSale(request);
        return ResponseEntity.ok(response);
    }
    // ✅ Endpoint opcional para imprimir
    @PostMapping("/{id}/print")
    public ResponseEntity<Void> printSale(@PathVariable Long id) throws Exception {
        SaleResponse response = saleService.findByIdResponse(id); // método que devuelve SaleResponse
        printQueueService.requeue(id);
        return ResponseEntity.ok().build();
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
        SaleResponse sale = saleService.findByIdResponse(id);
        return ResponseEntity.ok(sale);
    }

}
