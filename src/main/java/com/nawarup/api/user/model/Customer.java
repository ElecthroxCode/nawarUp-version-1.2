package com.nawarup.api.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor
public class Customer extends User {
    @Column(name = "full_name", nullable = false)
    private String fullName;
    private String phone;
    private String address;
}
