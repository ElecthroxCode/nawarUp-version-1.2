package com.nawarup.api.appointment.service;

import com.nawarup.api.appointment.dto.*;
import com.nawarup.api.appointment.model.*;
import com.nawarup.api.appointment.repository.AppointmentRepository;
import com.nawarup.api.catalog.model.BeautyService;
import com.nawarup.api.catalog.model.BusinessSchedule;
import com.nawarup.api.catalog.repository.BeautyServiceRepository;
import com.nawarup.api.catalog.repository.BusinessScheduleRepository;
import com.nawarup.api.common.exception.ApiException;
import com.nawarup.api.security.AuthUser;
import com.nawarup.api.user.model.Business;
import com.nawarup.api.user.model.Customer;
import com.nawarup.api.user.model.Role;
import com.nawarup.api.user.repository.BusinessRepository;
import com.nawarup.api.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final CustomerRepository customers;
    private final BusinessRepository businesses;
    private final BeautyServiceRepository services;
    private final BusinessScheduleRepository schedules;

    @Transactional
    public AppointmentResponse create(Long customerId, CreateAppointmentRequest req) {
        Customer customer = customers.findById(customerId)
                .orElseThrow(() -> ApiException.notFound("Cliente no encontrado"));
        // Bloquea la fila del negocio: serializa reservas concurrentes del mismo negocio.
        Business business = businesses.findByIdForUpdate(req.businessId())
                .orElseThrow(() -> ApiException.notFound("Negocio no encontrado"));

        LocalDateTime start = req.startTime();
        if (!start.isAfter(LocalDateTime.now()))
            throw ApiException.badRequest("La cita debe ser en una fecha/hora futura");
        if (req.appointmentType() == AppointmentType.DELIVERY
                && (req.deliveryAddress() == null || req.deliveryAddress().isBlank()))
            throw ApiException.badRequest("La dirección es obligatoria para citas a domicilio");

        Map<Long, BeautyService> catalog = services
                .findAllById(req.items().stream().map(CreateAppointmentRequest.Item::serviceId).toList()).stream()
                .collect(Collectors.toMap(BeautyService::getId, Function.identity()));

        Appointment appt = new Appointment();
        appt.setCustomer(customer);
        appt.setBusiness(business);
        appt.setStartTime(start);
        appt.setAppointmentType(req.appointmentType());
        appt.setDeliveryAddress(req.appointmentType() == AppointmentType.DELIVERY ? req.deliveryAddress().trim() : null);
        appt.setNote(req.note());
        appt.setStatus(AppointmentStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        int minutes = 0;
        for (CreateAppointmentRequest.Item item : req.items()) {
            BeautyService s = catalog.get(item.serviceId());
            if (s == null || !s.isActive() || !s.getBusiness().getId().equals(business.getId()))
                throw ApiException.badRequest("El servicio " + item.serviceId() + " no está disponible en este negocio");
            AppointmentDetail d = new AppointmentDetail();
            d.setService(s);
            d.setCount(item.count());
            d.setSubtotal(s.getPrice().multiply(BigDecimal.valueOf(item.count())));
            appt.addDetail(d);
            total = total.add(d.getSubtotal());
            minutes += s.getDurationMinutes() * item.count();
        }
        LocalDateTime end = start.plusMinutes(minutes);
        appt.setEndTime(end);
        appt.setTotal(total);

        validateWithinSchedule(business.getId(), start, end);
        if (appointments.existsOverlap(business.getId(), start, end))
            throw ApiException.conflict("El horario seleccionado ya está ocupado");

        return AppointmentResponse.from(appointments.save(appt));
    }

    private void validateWithinSchedule(Long businessId, LocalDateTime start, LocalDateTime end) {
        if (!start.toLocalDate().equals(end.toLocalDate()))
            throw ApiException.badRequest("La cita no puede cruzar la medianoche");
        BusinessSchedule sch = schedules.findByBusinessIdAndDayOfWeek(businessId, start.getDayOfWeek())
                .orElseThrow(() -> ApiException.badRequest("El negocio no atiende ese día"));
        if (start.toLocalTime().isBefore(sch.getOpeningTime()) || end.toLocalTime().isAfter(sch.getClosingTime()))
            throw ApiException.badRequest("La cita queda fuera del horario de atención ("
                    + sch.getOpeningTime() + " - " + sch.getClosingTime() + ")");
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> mine(AuthUser user) {
        List<Appointment> list = user.role() == Role.CUSTOMER
                ? appointments.findByCustomerIdOrderByStartTimeDesc(user.id())
                : appointments.findByBusinessIdOrderByStartTimeDesc(user.id());
        return list.stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse changeStatus(Long id, AuthUser user, AppointmentStatus target) {
        Appointment a = appointments.findById(id).orElseThrow(() -> ApiException.notFound("Cita no encontrada"));
        boolean ownerCustomer = user.role() == Role.CUSTOMER && a.getCustomer().getId().equals(user.id());
        boolean ownerBusiness = user.role() == Role.BUSINESS && a.getBusiness().getId().equals(user.id());
        if (!ownerCustomer && !ownerBusiness) throw ApiException.forbidden("No puedes modificar esta cita");
        if (a.getStatus() == AppointmentStatus.CANCELLED || a.getStatus() == AppointmentStatus.COMPLETED)
            throw ApiException.conflict("La cita ya está " + a.getStatus());
        if (target == AppointmentStatus.PENDING) throw ApiException.badRequest("Estado inválido");
        if (ownerCustomer && target != AppointmentStatus.CANCELLED)
            throw ApiException.forbidden("El cliente solo puede cancelar su cita");
        a.setStatus(target);
        return AppointmentResponse.from(a);
    }

    @Transactional(readOnly = true)
    public List<BusySlot> busySlots(Long businessId, LocalDate date) {
        if (!businesses.existsById(businessId)) throw ApiException.notFound("Negocio no encontrado");
        return appointments.findActiveBetween(businessId, date.atStartOfDay(), date.plusDays(1).atStartOfDay()).stream()
                .map(a -> new BusySlot(a.getStartTime(), a.getEndTime())).toList();
    }
}
