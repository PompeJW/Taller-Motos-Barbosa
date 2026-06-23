package org.biker.dto;

public record OrderSparePartRequest(
        String repuesto,
        Integer cantidad,
        Long sparePartId,
        Long statusId
) {



}
