package com.nawarup.api.appointment.dto;

import com.nawarup.api.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull AppointmentStatus status) {}
