package com.courseportal.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
  private SecurityUtil() {}

  public static JwtPrincipal requirePrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal p)) {
      throw new IllegalStateException("Not authenticated");
    }
    return p;
  }
}

