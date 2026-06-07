package org.biker.dto;

import java.math.BigDecimal;

public record WorkOrderDetailResponse(
       Long id,
       Integer quantity,
       BigDecimal unitPrice,
       Long workOrder,
       Long sparePartId
) {
}
