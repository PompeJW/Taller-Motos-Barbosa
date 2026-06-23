package org.biker.dto;

import org.biker.model.Brand;
import org.biker.model.WorkOrder;
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
         Long brandId,
         String brandName,
         String model,
         List<ObservationResponse> observations,
         BigDecimal totalValue,
         BigDecimal labour,
         Long statusId,
         String statusName,
         List<WorkOrderDetailResponse> details
) {

    public static WorkOrderResponse toResponse(WorkOrder wo) {

        return new WorkOrderResponse(
                wo.getId(),
                wo.getOrderNumber(),
                wo.getCreatedAt(),
                wo.getUpdatedAt(),
                wo.getCustomerName(),

                wo.getBrand().getId(),
                wo.getBrand().getName(),

                wo.getModel(),


                wo.getObservations() == null ? List.of() :
                        wo.getObservations()
                                .stream()
                                .map(o -> new ObservationResponse(
                                        o.getId(),
                                        o.getDescription()
                                ))
                                .toList(),

                wo.getTotalValue(),
                wo.getLabour(),

                wo.getStatus().getId(),
                wo.getStatus().getName(),

                wo.getDetails() == null ? List.of() :
                        wo.getDetails()
                                .stream()
                                .map(WorkOrderResponse::toDetailResponse)
                                .toList()
        );
    }
    public static WorkOrderDetailResponse toDetailResponse(WorkOrderDetail d) {

        return new WorkOrderDetailResponse(
                d.getId(),
                d.getQuantity(),
                d.getUnitPrice(),
                d.getWorkOrder().getId(),
                d.getSparePart().getId()
        );
    }




}