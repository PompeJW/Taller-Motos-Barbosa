package org.biker.repository;

import org.biker.model.AccountingEntry;
import org.biker.model.MovementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MovementCategoryRepository extends JpaRepository<MovementCategory, Long> {
    Optional<MovementCategory> findByCode(String code);
    Optional<MovementCategory> findByNameIgnoreCase(String name);
}
