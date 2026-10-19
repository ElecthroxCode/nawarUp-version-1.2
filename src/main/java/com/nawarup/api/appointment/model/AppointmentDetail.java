package com.nawarup.api.appointment.model;

import com.nawarup.api.catalog.model.BeautyService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "appointment_details")
@Getter @Setter @NoArgsConstructor
public class AppointmentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id")
    private BeautyService service;

    /** Cantidad (p. ej. número de personas / unidades del servicio). */
    @Column(name = "count", nullable = false)
    private int count;

    @Column(nullable = false)
    private BigDecimal subtotal;
}
