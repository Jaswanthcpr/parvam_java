package com.courseportal.enrollment;

import com.courseportal.enrollment.dto.EnrollRequest;
import com.courseportal.enrollment.dto.EnrollmentDto;
import com.courseportal.enrollment.dto.MyEnrollmentsResponse;
import com.courseportal.security.JwtPrincipal;
import com.courseportal.security.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
  private final EnrollmentService enrollmentService;

  public EnrollmentController(EnrollmentService enrollmentService) {
    this.enrollmentService = enrollmentService;
  }

  @GetMapping("/me")
  public MyEnrollmentsResponse me() {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    return enrollmentService.myEnrollments(principal.studentId());
  }

  @PostMapping
  public EnrollmentDto enroll(@Valid @RequestBody EnrollRequest request) {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    return enrollmentService.enroll(principal.studentId(), request.getCourseId());
  }

  @DeleteMapping("/{id}")
  public EnrollmentDto cancel(@PathVariable long id) {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    return enrollmentService.cancel(principal.studentId(), id);
  }
}

