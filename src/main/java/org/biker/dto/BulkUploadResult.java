package org.biker.dto;

import java.util.List;

public record BulkUploadResult(
        List<SparePartResponse> success,
        List<String> errors
) {}

