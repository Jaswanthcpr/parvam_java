package com.courseportal.security;

import com.courseportal.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey secretKey;
  private final long expiryMinutes;

  public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiryMinutes}") long expiryMinutes) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expiryMinutes = expiryMinutes;
  }

  public String issueToken(long studentId, Role role) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(expiryMinutes * 60);

    return Jwts.builder()
        .subject(Long.toString(studentId))
        .claim("role", role.name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(secretKey)
        .compact();
  }

  public JwtPrincipal parse(String token) {
    Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    long studentId = Long.parseLong(claims.getSubject());
    Role role = Role.valueOf(String.valueOf(claims.get("role")));
    return new JwtPrincipal(studentId, role);
  }
}

