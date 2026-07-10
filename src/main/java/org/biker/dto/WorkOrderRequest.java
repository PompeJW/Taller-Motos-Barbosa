package org.biker.dto;

import java.math.BigDecimal;
import java.util.List;

public record WorkOrderRequest(
        Long clientId,                // opcional: si ya existe
        String customerName,          // si es nuevo
        String documento,             // opcional para nuevo cliente
        String motorcyclePlate,
        Long brandId,
        String model,
        BigDecimal labour,
        List<ObservationRequest> observations
) {}

