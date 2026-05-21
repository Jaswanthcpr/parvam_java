package com.courseportal.auth;

public interface SmsSender {
  void sendOtp(String toPhone, String otp);
}

