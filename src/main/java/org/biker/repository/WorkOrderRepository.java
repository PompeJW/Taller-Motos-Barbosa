package org.biker.repository;

import org.biker.model.Status;
import org.biker.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
}
