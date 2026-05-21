package com.courseportal.auth;

import com.courseportal.model.OtpToken;
import com.courseportal.model.Student;
import com.courseportal.repo.OtpTokenRepository;
import com.courseportal.repo.StudentRepository;
import com.courseportal.security.JwtService;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {
  private final StudentRepository studentRepository;
  private final OtpTokenRepository otpTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailSender emailSender;
  private final SmsSender smsSender;
  private final long otpExpiryMinutes;
  private final SecureRandom secureRandom = new SecureRandom();

  public AuthService(
      StudentRepository studentRepository,
      OtpTokenRepository otpTokenRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      EmailSender emailSender,
      SmsSender smsSender,
      @Value("${app.otp.expiryMinutes}") long otpExpiryMinutes) {
    this.studentRepository = studentRepository;
    this.otpTokenRepository = otpTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.emailSender = emailSender;
    this.smsSender = smsSender;
    this.otpExpiryMinutes = otpExpiryMinutes;
  }

  @Transactional
  public void requestOtp(String identifier, OtpChannel channel) {
    String normalized = normalizeIdentifier(identifier);
    Student student = findStudent(normalized);

    String otp = generateOtp();
    OtpToken token = new OtpToken();
    token.setIdentifier(normalized);
    token.setCodeHash(passwordEncoder.encode(otp));
    token.setExpiresAt(Instant.now().plusSeconds(otpExpiryMinutes * 60));
    token.setUsed(false);
    otpTokenRepository.save(token);

    if (channel == OtpChannel.EMAIL || channel == OtpChannel.EMAIL_SMS) {
      if (student.getEmail() == null || student.getEmail().isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student has no email");
      }
      emailSender.sendOtp(student.getEmail(), otp);
    }

    if (channel == OtpChannel.SMS || channel == OtpChannel.EMAIL_SMS) {
      if (student.getPhone() == null || student.getPhone().isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student has no phone");
      }
      smsSender.sendOtp(student.getPhone(), otp);
    }
  }

  @Transactional
  public String verifyOtp(String identifier, String otp) {
    String normalized = normalizeIdentifier(identifier);
    Student student = findStudent(normalized);

    OtpToken token =
        otpTokenRepository
            .findTopByIdentifierAndUsedFalseOrderByCreatedAtDesc(normalized)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not requested"));

    if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
    }
    if (!passwordEncoder.matches(otp, token.getCodeHash())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
    }

    token.setUsed(true);
    otpTokenRepository.save(token);
    return jwtService.issueToken(student.getId(), student.getRole());
  }

  private String generateOtp() {
    int code = secureRandom.nextInt(900000) + 100000;
    return Integer.toString(code);
  }

  private Student findStudent(String normalizedIdentifier) {
    if (normalizedIdentifier.contains("@")) {
      return studentRepository
          .findByEmailIgnoreCase(normalizedIdentifier)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }
    return studentRepository
        .findByPhone(normalizedIdentifier)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
  }

  private String normalizeIdentifier(String identifier) {
    String trimmed = identifier == null ? "" : identifier.trim();
    if (trimmed.contains("@")) {
      return trimmed.toLowerCase(Locale.ROOT);
    }
    return trimmed.replaceAll("\\s+", "");
  }
}

