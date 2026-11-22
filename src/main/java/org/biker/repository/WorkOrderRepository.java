package org.biker.repository;

import org.biker.model.Brand;
import org.biker.model.Status;
import org.biker.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WorkOrder w where w.id = :id")
    Optional<WorkOrder> findByIdForUpdate(@Param("id") Long id);


    List<WorkOrder> findByStatusNameNot(String statusName);

    List<WorkOrder> findByCustomerNameContainingIgnoreCase(String customerName);

    List<WorkOrder> findByMotorcyclePlateContainingIgnoreCase(String motorcyclePlate);

    List<WorkOrder> findByStatusName( String statusName);

    List<WorkOrder> findByOrderNumberContainingIgnoreCase(String orderNumber);

    boolean existsByBrand(Brand brand);


}
