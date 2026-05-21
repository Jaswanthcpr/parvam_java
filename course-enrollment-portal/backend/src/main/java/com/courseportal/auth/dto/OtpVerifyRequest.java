package com.courseportal.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class OtpVerifyRequest {
  @NotBlank
  private String identifier;

  @NotBlank
  private String otp;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}

