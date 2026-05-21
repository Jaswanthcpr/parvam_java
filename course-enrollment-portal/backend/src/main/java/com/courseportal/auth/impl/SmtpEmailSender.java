package com.courseportal.auth.impl;

import com.courseportal.auth.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailSender implements EmailSender {
  private final JavaMailSender mailSender;
  private final String from;

  public SmtpEmailSender(JavaMailSender mailSender, @Value("${spring.mail.username:}") String from) {
    this.mailSender = mailSender;
    this.from = from;
  }

  @Override
  public void sendOtp(String toEmail, String otp) {
    if (from == null || from.isBlank()) {
      throw new IllegalStateException("SMTP username not configured");
    }
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(toEmail);
    message.setSubject("Your Course Portal OTP");
    message.setText("Your OTP is: " + otp + "\nIt expires in 5 minutes.");
    mailSender.send(message);
  }
}

