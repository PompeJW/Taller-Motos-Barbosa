package org.biker.repository;

import org.biker.model.Brand;
import org.biker.model.Category;
import org.biker.model.SparePart;
import org.biker.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    List<SparePart> findByNameContainingIgnoreCase(String name);

    List<SparePart> findBySkuCodeContainingIgnoreCase(String skuCode);

    List<SparePart> findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(String name, String skuCode);

    @Query("""
       SELECT s
       FROM SparePart s
       WHERE s.stock <= s.minStock
       """)
    List<SparePart> findByStockLessThanEqual();

    boolean existsByBrand(Brand brand);

    // ✅ corregido: ya no existe category en SparePart
    Optional<SparePart> findTopBySubCategory_CategoryOrderByIdDesc(Category category);

    // 🔹 Nuevo método: buscar repuestos por subcategoría
    List<SparePart> findBySubCategory(SubCategory subCategory);

    // 🔹 O si solo quieres validar existencia (más eficiente)
    boolean existsBySubCategory(SubCategory subCategory);
    boolean existsBySubCategory_Category(Category category);

    Optional<SparePart> findBySkuCode(String skuCode);
    Optional<SparePart> findByBarcode(String barcode);


}
