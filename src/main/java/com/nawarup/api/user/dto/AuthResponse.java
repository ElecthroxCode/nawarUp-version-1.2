package com.nawarup.api.user.dto;

import com.nawarup.api.user.model.Role;

public record AuthResponse(String token, Long userId, String email, Role role) {}
