package com.nawarup.api.appointment.dto;

import com.nawarup.api.appointment.model.Appointment;
import com.nawarup.api.appointment.model.AppointmentStatus;
import com.nawarup.api.appointment.model.AppointmentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AppointmentResponse(Long id, Long businessId, String businessName, Long customerId, String customerName,
                                  LocalDateTime startTime, LocalDateTime endTime, BigDecimal total, String note,
                                  AppointmentType appointmentType, String deliveryAddress, AppointmentStatus status,
                                  List<Item> items) {

    public record Item(Long serviceId, String serviceName, Integer count, BigDecimal subtotal) {}

    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(a.getId(), a.getBusiness().getId(), a.getBusiness().getNameBusiness(),
                a.getCustomer().getId(), a.getCustomer().getFullName(), a.getStartTime(), a.getEndTime(),
                a.getTotal(), a.getNote(), a.getAppointmentType(), a.getDeliveryAddress(), a.getStatus(),
                a.getDetails().stream()
                        .map(d -> new Item(d.getService().getId(), d.getService().getNameService(), d.getCount(), d.getSubtotal()))
                        .toList());
    }
}
