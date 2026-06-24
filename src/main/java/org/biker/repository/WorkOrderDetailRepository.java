package org.biker.repository;

import org.biker.model.WorkOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderDetailRepository extends JpaRepository<WorkOrderDetail, Long> {
}
