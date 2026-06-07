package org.biker.dto;

import java.math.BigDecimal;
import java.util.List;

public record WorkOrderRequest(

        String customerName,
        String motorcyclePlate,
        Long brandId,
        String model,
        BigDecimal labour,
        List<ObservationRequest> observations
) {
}
