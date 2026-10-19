package com.nawarup.api.catalog.dto;

import com.nawarup.api.catalog.model.BusinessSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleResponse(Long id, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
    public static ScheduleResponse from(BusinessSchedule s) {
        return new ScheduleResponse(s.getId(), s.getDayOfWeek(), s.getOpeningTime(), s.getClosingTime());
    }
}
