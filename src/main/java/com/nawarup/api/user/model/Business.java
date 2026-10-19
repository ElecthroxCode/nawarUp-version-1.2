package com.nawarup.api.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "businesses")
@Getter @Setter @NoArgsConstructor
public class Business extends User {
    @Column(name = "name_business", nullable = false)
    private String nameBusiness;
    private String manager;
    private String phones;
    private String address;
}
