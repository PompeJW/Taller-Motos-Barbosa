package org.biker.dto;

public record AddSparePartRequest(
        Long sparePartId,
        Integer quantity
) {
}
