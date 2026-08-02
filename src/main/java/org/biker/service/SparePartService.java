package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SparePartRequest;
import org.biker.dto.SparePartResponse;
import org.biker.model.*;
import org.biker.repository.*;
import org.biker.utils.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SparePartService {

    private final SparePartRepository repository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final StatusRepository statusRepository;

    public List<SparePartResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(sp -> new SparePartResponse(
                        sp.getId(),
                        sp.getName(),
                        sp.getDescription(),
                        sp.getSkuCode(),
                        sp.getPriceBuy(),
                        sp.getPriceSell(),
                        sp.getStock(),
                        sp.getMinStock(),
                        sp.getBrand().getName(),
                        sp.getCategory().getName(),
                        sp.getStatus().getName(),
                        sp.getSubCategory().getName()
                ))
                .toList();
    }

    public SparePart findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.SPARE_PART_NOT_FOUND));
    }

    public List<SparePart> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public SparePart findBySkuCode(String skuCode) {

        return repository.findBySkuCodeContainingIgnoreCase(skuCode)
                .orElseThrow(() ->
                        new RuntimeException(Message.SKU_NOT_FOUND));
    }

    public List<SparePartResponse> search(String query) {
        return repository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query)
                .stream()
                .map(sp -> new SparePartResponse(
                        sp.getId(),
                        sp.getName(),
                        sp.getDescription(),
                        sp.getSkuCode(),
                        sp.getPriceBuy(),
                        sp.getPriceSell(),
                        sp.getStock(),
                        sp.getMinStock(),
                        sp.getBrand().getName(),
                        sp.getCategory().getName(),
                        sp.getStatus().getName(),
                        sp.getSubCategory().getName()
                ))
                .toList();
    }


    public List<SparePart> getLowStock() {
        return repository.findByStockLessThanEqual();
    }

    public SparePartResponse save(SparePartRequest request) {

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() ->
                        new RuntimeException(Message.BRAND_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new RuntimeException(Message.CATEGORY_NOT_FOUND));

        SubCategory subCategory = subCategoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        Status status = statusRepository.findById(request.statusId())
                .orElseThrow(() ->
                        new RuntimeException(Message.STATUS_NOT_FOUND));

        SparePart sparePart = new SparePart();

        sparePart.setName(request.name());
        sparePart.setDescription(request.description());
        sparePart.setSkuCode(request.skuCode());
        sparePart.setPriceBuy(request.priceBuy());
        sparePart.setPriceSell(request.priceSell());
        sparePart.setStock(request.stock());
        sparePart.setMinStock(request.minStock());
        sparePart.setBrand(brand);
        sparePart.setCategory(category);
        sparePart.setStatus(status);
        sparePart.setSubCategory(subCategory);

        SparePart saved = repository.save(sparePart);

        return new SparePartResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getSkuCode(),
                saved.getPriceBuy(),
                saved.getPriceSell(),
                saved.getStock(),
                saved.getMinStock(),
                saved.getBrand().getName(),
                saved.getCategory().getName(),
                saved.getStatus().getName(),
                saved.getSubCategory().getName()
        );
    }

    public SparePartResponse update(Long id, SparePartRequest request) {

        SparePart existing = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(Message.SPARE_PART_NOT_FOUND));

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() ->
                        new RuntimeException(Message.BRAND_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new RuntimeException(Message.CATEGORY_NOT_FOUND));

        SubCategory subCategory = subCategoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        Status status = statusRepository.findById(request.statusId())
                .orElseThrow(() ->
                        new RuntimeException(Message.STATUS_NOT_FOUND));

        existing.setName(request.name());
        existing.setDescription(request.description());
        existing.setSkuCode(request.skuCode());
        existing.setPriceBuy(request.priceBuy());
        existing.setPriceSell(request.priceSell());
        existing.setStock(request.stock());
        existing.setMinStock(request.minStock());
        existing.setBrand(brand);
        existing.setCategory(category);
        existing.setStatus(status);
        existing.setSubCategory(subCategory);

        SparePart updated = repository.save(existing);

        return new SparePartResponse(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                updated.getSkuCode(),
                updated.getPriceBuy(),
                updated.getPriceSell(),
                updated.getStock(),
                updated.getMinStock(),
                updated.getBrand().getName(),
                updated.getCategory().getName(),
                updated.getStatus().getName(),
                updated.getSubCategory().getName()
        );
    }
}