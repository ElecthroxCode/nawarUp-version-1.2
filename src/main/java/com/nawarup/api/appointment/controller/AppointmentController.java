package com.nawarup.api.appointment.controller;

import com.nawarup.api.appointment.dto.*;
import com.nawarup.api.appointment.service.AppointmentService;
import com.nawarup.api.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService service;

    @PostMapping("/appointments")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@AuthenticationPrincipal AuthUser user,
                                      @Valid @RequestBody CreateAppointmentRequest req) {
        return service.create(user.id(), req);
    }

    /** Citas del usuario autenticado (cliente: las suyas; negocio: las de su agenda). */
    @GetMapping("/appointments/me")
    public List<AppointmentResponse> mine(@AuthenticationPrincipal AuthUser user) {
        return service.mine(user);
    }

    @PatchMapping("/appointments/{id}/status")
    public AppointmentResponse changeStatus(@AuthenticationPrincipal AuthUser user, @PathVariable Long id,
                                            @Valid @RequestBody StatusRequest req) {
        return service.changeStatus(id, user, req.status());
    }

    /** Pública: franjas ocupadas de un negocio en un día, para pintar disponibilidad en el front. */
    @GetMapping("/businesses/{businessId}/availability")
    public List<BusySlot> availability(@PathVariable Long businessId,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.busySlots(businessId, date);
    }
}
