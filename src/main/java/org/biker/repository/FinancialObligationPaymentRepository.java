package org.biker.repository;

import org.biker.model.FinancialObligationPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinancialObligationPaymentRepository extends JpaRepository<FinancialObligationPayment, Long> {
    List<FinancialObligationPayment> findByObligationIdOrderByPaidAtDesc(Long obligationId);
}
