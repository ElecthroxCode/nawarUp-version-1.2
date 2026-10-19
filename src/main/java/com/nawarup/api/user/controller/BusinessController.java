package com.nawarup.api.user.controller;

import com.nawarup.api.common.exception.ApiException;
import com.nawarup.api.user.dto.BusinessResponse;
import com.nawarup.api.user.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Consulta pública de negocios (para que los clientes exploren). */
@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {
    private final BusinessRepository businesses;

    @GetMapping
    public List<BusinessResponse> list() {
        return businesses.findAll().stream().map(BusinessResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BusinessResponse get(@PathVariable Long id) {
        return businesses.findById(id).map(BusinessResponse::from)
                .orElseThrow(() -> ApiException.notFound("Negocio no encontrado"));
    }
}
