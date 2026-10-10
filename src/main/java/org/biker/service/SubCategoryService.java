package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SubCategoryRequest;
import org.biker.dto.SubCategoryResponse;
import org.biker.model.Category;
import org.biker.model.SubCategory;
import org.biker.repository.CategoryRepository;
import org.biker.repository.SparePartRepository;
import org.biker.repository.SubCategoryRepository;
import org.biker.utils.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private  final SubCategoryRepository subCategoryRepository;
    private  final CategoryRepository categoryRepository;

    private final SparePartRepository sparePartRepository;

    public List<SubCategory> finddAll() {
        return subCategoryRepository.findAll();
    }
    public  SubCategory findById(Long id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));
    }

    public List<SubCategory> findByCategory(Long categoryId) {
        return subCategoryRepository.findByCategory_Id(categoryId);
    }

    public SubCategoryResponse save(SubCategoryRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new RuntimeException(Message.CATEGORY_NOT_FOUND));

        SubCategory subCategory = new SubCategory();
        subCategory.setCategory(category);
        subCategory.setName((request.name()));

        SubCategory saved = subCategoryRepository.save(subCategory);

        SubCategoryResponse subCategoryResponse = new SubCategoryResponse(
                saved.getId(),
                saved.getCategory().getName(),
                saved.getName()
        );
        return  subCategoryResponse;
    }

    public SubCategoryResponse update(Long id, SubCategoryRequest request) {

        SubCategory existing = subCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new RuntimeException(Message.CATEGORY_NOT_FOUND));

        existing.setCategory(category);
        existing.setName(request.name());
        SubCategory update = subCategoryRepository.save(existing);

        SubCategoryResponse categoryResponse;
        categoryResponse = new SubCategoryResponse(
                update.getId(),
                update.getCategory().getName(),
                update.getName()
        );
        return categoryResponse;

    }

    public void delete(Long id) {
        SubCategory existing = subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        // 🔹 Validar con join en SparePart
        if (sparePartRepository.existsBySubCategory(existing)) {
            throw new RuntimeException("No se puede eliminar: la subcategoría tiene repuestos asociados");
        }

        subCategoryRepository.delete(existing);
    }


}
