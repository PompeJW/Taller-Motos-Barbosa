package org.biker.controller;
import lombok.RequiredArgsConstructor;
import org.biker.dto.BrandRequest;
import org.biker.dto.BrandResponse;
import org.biker.model.Brand;
import org.biker.repository.BrandRepository;
import org.biker.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService service;

    @GetMapping
    public List<Brand> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Brand getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public BrandResponse save(
            @RequestBody BrandRequest request) {

        return service.save(request);
    }

    @PutMapping("/{id}")
    public BrandResponse update(
            @PathVariable Long id,
            @RequestBody BrandRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
