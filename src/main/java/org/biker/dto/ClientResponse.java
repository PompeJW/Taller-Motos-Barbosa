package org.biker.dto;

public record ClientResponse(
        Long id,
        String clientName,
        String documento
) {}
