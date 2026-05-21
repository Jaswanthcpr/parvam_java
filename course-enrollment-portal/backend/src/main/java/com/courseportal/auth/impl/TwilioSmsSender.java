package com.courseportal.auth.impl;

import com.courseportal.auth.SmsSender;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsSender implements SmsSender {
  private final String accountSid;
  private final String authToken;
  private final String fromNumber;

  public TwilioSmsSender(
      @Value("${app.twilio.accountSid:}") String accountSid,
      @Value("${app.twilio.authToken:}") String authToken,
      @Value("${app.twilio.fromNumber:}") String fromNumber) {
    this.accountSid = accountSid;
    this.authToken = authToken;
    this.fromNumber = fromNumber;
  }

  @Override
  public void sendOtp(String toPhone, String otp) {
    if (accountSid.isBlank() || authToken.isBlank() || fromNumber.isBlank()) {
      throw new IllegalStateException("Twilio is not configured");
    }
    Twilio.init(accountSid, authToken);
    Message.creator(new PhoneNumber(toPhone), new PhoneNumber(fromNumber), "Your Course Portal OTP is: " + otp)
        .create();
  }
}

