package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.SparePartRequest;
import org.biker.dto.SparePartResponse;
import org.biker.model.*;
import org.biker.repository.*;
import org.biker.utils.Message;
import org.biker.utils.QrCodeUtil;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

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
                        sp.getStatus().getName(),
                        // ✅ Aquí usas Optional
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
    }

    public SparePart findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.SPARE_PART_NOT_FOUND));
    }
    public SparePart findByCode(String code) {

        if (code == null || code.isBlank()) {
            throw new RuntimeException(Message.SPARE_PART_NOT_FOUND);
        }

        // 🔹 Normalizar el código escaneado
        String normalizedCode = code
                .trim()                  // quitar espacios y saltos
                .replace("'", "-")       // reemplazar apóstrofe por guion
                .replace("’", "-")       // reemplazar comilla curva por guion
                .replace("`", "-")       // reemplazar backtick por guion
                .toUpperCase();          // forzar mayúsculas

        System.out.println("=================================");
        System.out.println("CÓDIGO RECIBIDO DEL ESCÁNER: [" + code + "]");
        System.out.println("CÓDIGO NORMALIZADO: [" + normalizedCode + "]");
        System.out.println("=================================");

        // 1. Buscar por SKU normalizado
        Optional<SparePart> sparePart = repository.findBySkuCode(normalizedCode);

        if (sparePart.isPresent()) {
            System.out.println("PRODUCTO ENCONTRADO POR SKU: "
                    + sparePart.get().getId()
                    + " - "
                    + sparePart.get().getName());
            return sparePart.get();
        }

        // 2. Buscar por barcode normalizado como respaldo
        sparePart = repository.findByBarcode(normalizedCode);

        if (sparePart.isPresent()) {
            System.out.println("PRODUCTO ENCONTRADO POR BARCODE: "
                    + sparePart.get().getId()
                    + " - "
                    + sparePart.get().getName());
            return sparePart.get();
        }

        System.out.println("❌ NO SE ENCONTRÓ PRODUCTO PARA: [" + normalizedCode + "]");
        throw new RuntimeException(Message.SPARE_PART_NOT_FOUND);
    }


    public List<SparePartResponse> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
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
                        sp.getStatus().getName(),
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
    }

    public List<SparePartResponse> searchBySkuCode(String skuCode) {
        return repository.findBySkuCodeContainingIgnoreCase(skuCode)
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
                        sp.getStatus().getName(),
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
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
                        sp.getStatus().getName(),
                        // ✅ Aquí usas Optional
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
    }


    public List<SparePart> getLowStock() {
        return repository.findByStockLessThanEqual();
    }

    private String generateSkuCode(Category category) {
        // Prefijo: primeras 3 letras de la categoría
        String prefix = category.getName().substring(0, 3).toUpperCase();

        // Timestamp en milisegundos
        long timestamp = System.currentTimeMillis();

        // SKU final: CAT-<timestamp>
        return String.format("%s-%d", prefix, timestamp);
    }



    public SparePartResponse save(SparePartRequest request) {

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() ->
                        new RuntimeException(Message.BRAND_NOT_FOUND));

        SubCategory subCategory = subCategoryRepository.findById(request.subCategoryId())
                .orElseThrow(() -> new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        Category category = subCategory.getCategory();

        Status status = statusRepository.findById(request.statusId())
                .orElseThrow(() ->
                        new RuntimeException(Message.STATUS_NOT_FOUND));

        SparePart sparePart = new SparePart();
        sparePart.setName(request.name());
        sparePart.setDescription(request.description());
        sparePart.setPriceBuy(request.priceBuy());
        sparePart.setPriceSell(request.priceSell());
        sparePart.setStock(request.stock());
        sparePart.setMinStock(request.minStock());
        sparePart.setBrand(brand);
        sparePart.setStatus(status);
        sparePart.setSubCategory(subCategory);

        // 1️⃣ Guardar primero sin SKU
        SparePart saved = repository.save(sparePart);

        // 2️⃣ Generar SKU con categoría + ID
        String prefix = normalizePrefix(category.getName()).substring(0, 3);
        String nextCode = String.format("%s-%05d", prefix, saved.getId());
        saved.setSkuCode(nextCode);


        // 3️⃣ Generar QR
        String qrCode = QrCodeUtil.generateQrCode(nextCode);
        saved.setBarcode(qrCode);

        // 4️⃣ Guardar actualización
        SparePart updated = repository.save(saved);

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
                saved.getStatus().getName(),
                saved.getSubCategory().getName(),
                saved.getBarcode()
        );
    }

    private String normalizePrefix(String input) {
        if (input == null) return "";

        // Pasar a mayúsculas
        String normalized = input.toUpperCase();

        // Eliminar acentos y caracteres especiales
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Mantener solo letras A-Z y números
        normalized = normalized.replaceAll("[^A-Z0-9]", "");

        return normalized;
    }
    public SparePartResponse update(Long id, SparePartRequest request) {

        SparePart existing = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(Message.SPARE_PART_NOT_FOUND));

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() ->
                        new RuntimeException(Message.BRAND_NOT_FOUND));

        SubCategory subCategory = subCategoryRepository.findById(request.subCategoryId())
                .orElseThrow(() -> new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        Category category = subCategory.getCategory();


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
                updated.getStatus().getName(),
                updated.getSubCategory().getName(),
                updated.getBarcode()
        );
    }
}