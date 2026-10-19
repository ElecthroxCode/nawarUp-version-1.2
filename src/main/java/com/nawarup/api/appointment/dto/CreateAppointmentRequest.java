package com.nawarup.api.appointment.dto;

import com.nawarup.api.appointment.model.AppointmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreateAppointmentRequest(
        @NotNull Long businessId,
        @NotNull LocalDateTime startTime,
        @NotNull AppointmentType appointmentType,
        @Size(max = 255) String deliveryAddress,
        @Size(max = 500) String note,
        @NotEmpty List<@Valid Item> items) {

    public record Item(@NotNull Long serviceId, @NotNull @Min(1) Integer count) {}
}
