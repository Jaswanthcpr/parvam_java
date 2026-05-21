package com.courseportal.auth;

import com.courseportal.auth.dto.AuthResponse;
import com.courseportal.auth.dto.OtpRequest;
import com.courseportal.auth.dto.OtpVerifyRequest;
import com.courseportal.auth.dto.RegisterRequest;
import com.courseportal.model.Role;
import com.courseportal.model.Student;
import com.courseportal.repo.StudentRepository;
import com.courseportal.security.JwtPrincipal;
import com.courseportal.security.SecurityUtil;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final StudentRepository studentRepository;
  private final AuthService authService;

  public AuthController(StudentRepository studentRepository, AuthService authService) {
    this.studentRepository = studentRepository;
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    if ((request.getEmail() == null || request.getEmail().isBlank())
        && (request.getPhone() == null || request.getPhone().isBlank())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone is required");
    }
    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      studentRepository
          .findByEmailIgnoreCase(request.getEmail().trim().toLowerCase(Locale.ROOT))
          .ifPresent(s -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
          });
    }
    if (request.getPhone() != null && !request.getPhone().isBlank()) {
      studentRepository
          .findByPhone(request.getPhone().trim().replaceAll("\\s+", ""))
          .ifPresent(s -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered");
          });
    }

    Student student = new Student();
    student.setName(request.getName().trim());
    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      student.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
    }
    if (request.getPhone() != null && !request.getPhone().isBlank()) {
      student.setPhone(request.getPhone().trim().replaceAll("\\s+", ""));
    }
    student.setRole(Role.STUDENT);

    Student saved = studentRepository.save(student);
    return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>() {
      {
        put("studentId", saved.getId());
      }
    });
  }

  @PostMapping("/request-otp")
  public ResponseEntity<Void> requestOtp(@Valid @RequestBody OtpRequest request) {
    authService.requestOtp(request.getIdentifier(), request.getChannel());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/verify-otp")
  public AuthResponse verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
    String token = authService.verifyOtp(request.getIdentifier(), request.getOtp());
    Student student =
        request.getIdentifier().contains("@")
            ? studentRepository
                .findByEmailIgnoreCase(request.getIdentifier().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"))
            : studentRepository
                .findByPhone(request.getIdentifier().trim().replaceAll("\\s+", ""))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    return new AuthResponse(token, student.getId(), student.getRole());
  }

  @GetMapping("/me")
  public ResponseEntity<?> me() {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    Student student =
        studentRepository
            .findById(principal.studentId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    return ResponseEntity.ok(new HashMap<>() {
      {
        put("id", student.getId());
        put("name", student.getName());
        put("email", student.getEmail());
        put("phone", student.getPhone());
        put("role", student.getRole());
      }
    });
  }
}

