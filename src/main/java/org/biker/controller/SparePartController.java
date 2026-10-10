package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SparePartRequest;
import org.biker.dto.SparePartResponse;
import org.biker.model.SparePart;
import org.biker.service.PrinterService;
import org.biker.service.SparePartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spare-part")
@RequiredArgsConstructor
public class SparePartController {

    private final SparePartService service;
    private final PrinterService printerService;

    @GetMapping
    public List<SparePartResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SparePart getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search2")
    public List<SparePartResponse> searchByName(
            @RequestParam String name) {

        return service.searchByName(name);
    }
    @GetMapping("/searchSku")
    public List<SparePartResponse> searchBySku(
            @RequestParam String skuCode) {

        return service.searchBySkuCode(skuCode);
    }



    @GetMapping("/search")
    public List<SparePartResponse> search(@RequestParam String query) {
        return service.search(query);
    }



    @GetMapping("/low-stock")
    public List<SparePart> lowStock() {

        return service.getLowStock();
    }

    @PostMapping
    public SparePartResponse save(
            @RequestBody SparePartRequest request) {

        return service.save(request);
    }

    @PutMapping("/{id}")
    public SparePartResponse update(
            @PathVariable Long id,
            @RequestBody SparePartRequest request) {

        return service.update(id, request);
    }

    @PostMapping("/{id}/print-qr")
    public ResponseEntity<Void> printQr(@PathVariable Long id) {
        SparePart sparePart = service.findById(id);

        try {
            // ahora solo imprime una vez, tamaño fijo 3x3 cm
            printerService.printQr(sparePart.getBarcode());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/code/{code}")
    public SparePartResponse findByCode(@PathVariable String code) {
        SparePart sparePart = service.findByCode(code);
        return new SparePartResponse(
                sparePart.getId(),
                sparePart.getName(),
                sparePart.getDescription(),
                sparePart.getSkuCode(),
                sparePart.getPriceBuy(),
                sparePart.getPriceSell(),
                sparePart.getStock(),
                sparePart.getMinStock(),
                sparePart.getBrand().getName(),
                sparePart.getStatus().getName(),
                sparePart.getSubCategory() != null ? sparePart.getSubCategory().getName() : "Sin subcategoría",
                sparePart.getBarcode()
        );
    }


}