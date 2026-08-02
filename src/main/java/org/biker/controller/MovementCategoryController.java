package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.model.MovementCategory;
import org.biker.service.MovementCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movement-category")
@RequiredArgsConstructor
public class MovementCategoryController {

    private  final MovementCategoryService service;

    @GetMapping
    public List<MovementCategory> getAll()
    {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MovementCategory getId(@PathVariable Long id) {
        return service.findById(id);
    }



}
