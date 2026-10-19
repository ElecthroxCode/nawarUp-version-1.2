package com.nawarup.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterBusinessRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 150) String nameBusiness,
        @Size(max = 150) String manager,
        @Size(max = 100) String phones,
        @Size(max = 255) String address) {}
