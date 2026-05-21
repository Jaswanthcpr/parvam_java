package com.courseportal.security;

import com.courseportal.model.Role;

public record JwtPrincipal(long studentId, Role role) {}

