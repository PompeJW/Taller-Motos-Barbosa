package org.biker.dto;

public record OrderSparePartResponse (
        Long id,
        String repuesto,
        Integer cantidad,
        String sparePartId,
        String status
){
}
