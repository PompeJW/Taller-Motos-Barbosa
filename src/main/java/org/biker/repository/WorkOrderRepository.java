package org.biker.repository;

import org.biker.model.Status;
import org.biker.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByStatusNameNot(String statusName);

    List<WorkOrder> findByCustomerNameContainingIgnoreCase(String customerName);

    List<WorkOrder> findByMotorcyclePlateContainingIgnoreCase(String motorcyclePlate);

    List<WorkOrder> findByStatusName( String statusName);

    List<WorkOrder> findByOrderNumber( String orderNumber);


}
