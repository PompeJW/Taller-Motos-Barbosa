package org.biker.repository;

import org.biker.model.AccountingEntry;
import org.biker.model.MovementCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementCategoryRepository extends JpaRepository<MovementCategory, Long> {
}
