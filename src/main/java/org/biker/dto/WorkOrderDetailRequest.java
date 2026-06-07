package org.biker.dto;

public record WorkOrderDetailRequest(
        Integer quantity,
        Long sparePartId
) {
}
