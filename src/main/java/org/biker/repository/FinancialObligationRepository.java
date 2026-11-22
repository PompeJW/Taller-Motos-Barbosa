package org.biker.repository;

import org.biker.model.FinancialObligation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface FinancialObligationRepository extends JpaRepository<FinancialObligation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FinancialObligation> findWithLockById(Long id);
}
