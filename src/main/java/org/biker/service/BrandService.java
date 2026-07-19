package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.BrandRequest;
import org.biker.dto.BrandResponse;
import org.biker.model.Brand;
import org.biker.repository.BrandRepository;
import org.biker.repository.SparePartRepository;
import org.biker.repository.WorkOrderRepository;
import org.biker.utils.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {


    private final BrandRepository brandRepository;
    private final WorkOrderRepository workOrderRepository;
    private final SparePartRepository sparePartRepository;

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.BRAND_NOT_FOUND));
    }

    public BrandResponse save(BrandRequest request) {

        Brand brand = new Brand();
        brand.setName(request.name());

        Brand saved = brandRepository.save(brand);

        BrandResponse brandResponse = new BrandResponse(
                saved.getId(),
                saved.getName()
                ); return brandResponse;
    }

    public BrandResponse update(Long id, BrandRequest request) {

        Brand existing = brandRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(Message.BRAND_NOT_FOUND));

        existing.setName(request.name());

        Brand updated = brandRepository.save(existing);

        BrandResponse brandResponse;
        brandResponse = new BrandResponse(
                updated.getId(),
                updated.getName()
                ); return brandResponse;
    }

    public void delete(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        // Validar si la marca está en uso en órdenes de trabajo
        boolean usedInWorkOrders = workOrderRepository.existsByBrand(brand);
        if (usedInWorkOrders) {
            throw new RuntimeException("No se puede eliminar la marca, está asociada a órdenes de trabajo");
        }

        // Validar si la marca está en uso en repuestos
        boolean usedInSpareParts = sparePartRepository.existsByBrand(brand);
        if (usedInSpareParts) {
            throw new RuntimeException("No se puede eliminar la marca, está asociada a repuestos");
        }

        brandRepository.delete(brand);
    }
}
