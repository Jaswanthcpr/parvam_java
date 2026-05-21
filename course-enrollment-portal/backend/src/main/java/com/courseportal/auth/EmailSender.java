package com.courseportal.auth;

public interface EmailSender {
  void sendOtp(String toEmail, String otp);
}

