package org.biker.repository;

import org.biker.model.WorkOrderPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderPaymentStatusRepository extends JpaRepository<WorkOrderPaymentStatus, Long> {
}