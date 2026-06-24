package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.BrandRequest;
import org.biker.dto.CategoryRequest;
import org.biker.dto.CategoryResponse;
import org.biker.model.Category;
import org.biker.repository.CategoryRepository;
import org.biker.utils.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private  final CategoryRepository categoryRepository;

    public List<Category> finddAll() {
        return categoryRepository.findAll();
    }
    public  Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.CATEGORY_NOT_FOUND));
    }

    public CategoryResponse save(CategoryRequest request) {
        Category category = new Category();
        category.setName((request.name()));

        Category saved = categoryRepository.save(category);

        CategoryResponse categoryResponse = new CategoryResponse(
                saved.getId(),
                saved.getName()
        );
        return  categoryResponse;
    }
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new  RuntimeException(Message.CATEGORY_NOT_FOUND));

        existing.setName(request.name());
        Category update = categoryRepository.save(existing);

        CategoryResponse categoryResponse;
        categoryResponse = new CategoryResponse(
                update.getId(),
                update.getName()
        );
        return categoryResponse;

    }
}
