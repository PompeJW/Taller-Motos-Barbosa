package org.biker.dto;

import org.biker.model.Brand;
import org.biker.model.WorkOrderDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record WorkOrderResponse(
         Long id,
         String orderNumber,
         LocalDateTime createdAt,
         LocalDateTime updatedAt,
         String customerName,
         Brand brand,
         String model,
         List<ObservationRequest> observations,
         BigDecimal totalValue,
         BigDecimal labour,
         Long status,
         List<WorkOrderDetail> details
) {
}
