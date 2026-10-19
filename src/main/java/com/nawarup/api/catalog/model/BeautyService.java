package com.nawarup.api.catalog.model;

import com.nawarup.api.user.model.Business;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Entidad "Service" del MER (renombrada para no chocar con @Service de Spring). */
@Entity
@Table(name = "services")
@Getter @Setter @NoArgsConstructor
public class BeautyService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id")
    private Business business;

    @Column(name = "name_service", nullable = false)
    private String nameService;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 30;

    @Column(nullable = false)
    private boolean active = true;
}
