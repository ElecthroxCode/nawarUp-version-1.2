package com.nawarup.api.user.service;

import com.nawarup.api.common.exception.ApiException;
import com.nawarup.api.security.JwtService;
import com.nawarup.api.user.dto.*;
import com.nawarup.api.user.model.*;
import com.nawarup.api.user.repository.BusinessRepository;
import com.nawarup.api.user.repository.CustomerRepository;
import com.nawarup.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final CustomerRepository customers;
    private final BusinessRepository businesses;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional
    public AuthResponse registerCustomer(RegisterCustomerRequest r) {
        Customer c = new Customer();
        fillUser(c, r.email(), r.password(), Role.CUSTOMER);
        c.setFullName(r.fullName().trim());
        c.setPhone(r.phone());
        c.setAddress(r.address());
        return toResponse(customers.save(c));
    }

    @Transactional
    public AuthResponse registerBusiness(RegisterBusinessRequest r) {
        Business b = new Business();
        fillUser(b, r.email(), r.password(), Role.BUSINESS);
        b.setNameBusiness(r.nameBusiness().trim());
        b.setManager(r.manager());
        b.setPhones(r.phones());
        b.setAddress(r.address());
        return toResponse(businesses.save(b));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(normalize(r.email()))
                .filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
                .orElseThrow(() -> ApiException.unauthorized("Credenciales inválidas"));
        return toResponse(u);
    }

    private void fillUser(User u, String email, String rawPassword, Role role) {
        String e = normalize(email);
        if (users.existsByEmail(e)) throw ApiException.conflict("El email ya está registrado");
        u.setEmail(e);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setRole(role);
    }

    private String normalize(String email) { return email.trim().toLowerCase(); }

    private AuthResponse toResponse(User u) {
        return new AuthResponse(jwt.generate(u), u.getId(), u.getEmail(), u.getRole());
    }
}
