package com.courseportal.auth.dto;

import com.courseportal.auth.OtpChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OtpRequest {
  @NotBlank
  private String identifier;

  @NotNull
  private OtpChannel channel;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public OtpChannel getChannel() {
    return channel;
  }

  public void setChannel(OtpChannel channel) {
    this.channel = channel;
  }
}

