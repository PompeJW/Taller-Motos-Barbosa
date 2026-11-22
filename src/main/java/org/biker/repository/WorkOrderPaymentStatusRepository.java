package org.biker.repository;

import org.biker.model.WorkOrderPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderPaymentStatusRepository extends JpaRepository<WorkOrderPaymentStatus, Long> {
    java.util.Optional<WorkOrderPaymentStatus> findFirstByStatusIgnoreCase(String status);
}