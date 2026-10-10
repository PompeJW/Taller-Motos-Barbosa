package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SubCategoryRequest;
import org.biker.dto.SubCategoryResponse;
import org.biker.model.SubCategory;
import org.biker.service.SubCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategory")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService service;

    @PostMapping
    public SubCategoryResponse save(
            @RequestBody SubCategoryRequest request) {
        return service.save(request);
    }

    @GetMapping
    public List<SubCategory> getAll() {
        return service.finddAll();
    }

    @GetMapping("/category/{categoryId}")
    public List<SubCategory> findByCategory(@PathVariable Long categoryId) {
        return service.findByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public SubCategory getId(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public SubCategoryResponse update(
            @PathVariable Long id,
            @RequestBody SubCategoryRequest request
    ){
        return service.update(id, request);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
