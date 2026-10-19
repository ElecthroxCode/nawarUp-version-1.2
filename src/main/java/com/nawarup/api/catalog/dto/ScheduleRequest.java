package com.nawarup.api.catalog.dto;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRequest(@NotNull DayOfWeek dayOfWeek, @NotNull LocalTime openingTime, @NotNull LocalTime closingTime) {}
