package com.nawarup.api.security;

import com.nawarup.api.user.model.Role;

/** Principal autenticado extraído del JWT. */
public record AuthUser(Long id, String email, Role role) {}
