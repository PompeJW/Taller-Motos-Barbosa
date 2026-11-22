package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.BulkUploadResult;
import org.biker.dto.SparePartRequest;
import org.biker.dto.SparePartResponse;
import org.biker.model.SparePart;
import org.biker.service.PrinterService;
import org.biker.service.SparePartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/spare-part")
@RequiredArgsConstructor
public class SparePartController {

    private final SparePartService service;
    private final org.biker.service.PrintQueueService printQueueService;

    private final org.biker.service.InventoryExcelReportService inventoryExcelReportService;

    @GetMapping(value = "/inventory-report.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadInventoryReport() throws java.io.IOException {
        byte[] data = inventoryExcelReportService.generate();
        String filename = "inventario-biker-" + java.time.LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(data.length)
                .body(data);
    }

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
    public ResponseEntity<java.util.Map<String, Object>> printQr(
            @PathVariable Long id, @RequestParam(defaultValue = "1") int copies) {
        SparePart part = service.findById(id);
        long jobId = printQueueService.queueQr(part, copies);
        return ResponseEntity.accepted().body(java.util.Map.of("status", "PENDING", "jobId", jobId));
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


    @PostMapping("/bulk-upload")
    public ResponseEntity<BulkUploadResult> bulkUpload(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.bulkUpload(file));
    }



}