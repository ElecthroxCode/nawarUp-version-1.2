package com.nawarup.api.user.dto;

import com.nawarup.api.user.model.Business;

public record BusinessResponse(Long id, String nameBusiness, String manager, String phones, String address, String email) {
    public static BusinessResponse from(Business b) {
        return new BusinessResponse(b.getId(), b.getNameBusiness(), b.getManager(), b.getPhones(), b.getAddress(), b.getEmail());
    }
}
