package org.biker.repository;
import org.biker.model.InventoryPurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface InventoryPurchaseHistoryRepository extends JpaRepository<InventoryPurchaseHistory,Long> {
    List<InventoryPurchaseHistory> findBySparePart_IdOrderByCreatedAtDescIdDesc(Long sparePartId);
}
