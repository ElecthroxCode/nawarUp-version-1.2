package com.nawarup.api.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceRequest(
        @NotBlank @Size(max = 150) String nameService,
        String description,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @Min(5) Integer durationMinutes) {}
