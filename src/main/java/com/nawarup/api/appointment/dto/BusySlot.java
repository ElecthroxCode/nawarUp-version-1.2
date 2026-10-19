package com.nawarup.api.appointment.dto;

import java.time.LocalDateTime;

public record BusySlot(LocalDateTime start, LocalDateTime end) {}
