package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.CategoryRequest;
import org.biker.dto.CategoryResponse;
import org.biker.model.Category;
import org.biker.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private  final CategoryService service;

    @PostMapping
    public CategoryResponse save(
            @RequestBody CategoryRequest request) {
        return  service.save(request);
    }
    @GetMapping
    public List<Category> getAll()
    {

        return service.finddAll();
    }
    @GetMapping("/{id}")
    public Category getId(@PathVariable Long id) {

        return service.findById(id);
    }


    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable Long id,
            @PathVariable CategoryRequest request
    ){
        return  service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
