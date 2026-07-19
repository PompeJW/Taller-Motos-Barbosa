package org.biker.repository;

import org.biker.model.Brand;
import org.biker.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    List<SparePart> findByNameContainingIgnoreCase(String name);

    Optional<SparePart> findBySkuCode(String skuCode);

    @Query("""
       SELECT s
       FROM SparePart s
       WHERE s.stock <= s.minStock
       """)
    List<SparePart> findByStockLessThanEqual();

    boolean existsByBrand(Brand brand);

}
