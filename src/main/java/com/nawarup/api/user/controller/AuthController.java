package com.nawarup.api.user.controller;

import com.nawarup.api.user.dto.*;
import com.nawarup.api.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @PostMapping("/register/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerCustomer(@Valid @RequestBody RegisterCustomerRequest req) {
        return auth.registerCustomer(req);
    }

    @PostMapping("/register/business")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerBusiness(@Valid @RequestBody RegisterBusinessRequest req) {
        return auth.registerBusiness(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req);
    }
}
