package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SparePartRequest;
import org.biker.dto.SparePartResponse;
import org.biker.model.SparePart;
import org.biker.service.SparePartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spare-part")
@RequiredArgsConstructor
public class SparePartController {

    private final SparePartService service;

    @GetMapping
    public List<SparePartResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SparePart getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    public List<SparePart> searchByName(
            @RequestParam String name) {

        return service.searchByName(name);
    }

    @GetMapping("/sku/{skuCode}")
    public SparePart findBySkuCode(
            @PathVariable String skuCode) {

        return service.findBySkuCode(skuCode);
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
}