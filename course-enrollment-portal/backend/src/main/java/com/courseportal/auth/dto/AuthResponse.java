package com.courseportal.auth.dto;

import com.courseportal.model.Role;

public class AuthResponse {
  private String token;
  private long studentId;
  private Role role;

  public AuthResponse() {}

  public AuthResponse(String token, long studentId, Role role) {
    this.token = token;
    this.studentId = studentId;
    this.role = role;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public long getStudentId() {
    return studentId;
  }

  public void setStudentId(long studentId) {
    this.studentId = studentId;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}

