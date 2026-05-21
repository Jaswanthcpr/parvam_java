package com.courseportal.enrollment.dto;

import com.courseportal.model.EnrollmentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public class EnrollmentDto {
  private Long id;
  private Long courseId;
  private String courseName;
  private BigDecimal courseFee;
  private String courseDuration;
  private EnrollmentStatus status;
  private Instant enrolledAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCourseId() {
    return courseId;
  }

  public void setCourseId(Long courseId) {
    this.courseId = courseId;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public BigDecimal getCourseFee() {
    return courseFee;
  }

  public void setCourseFee(BigDecimal courseFee) {
    this.courseFee = courseFee;
  }

  public String getCourseDuration() {
    return courseDuration;
  }

  public void setCourseDuration(String courseDuration) {
    this.courseDuration = courseDuration;
  }

  public EnrollmentStatus getStatus() {
    return status;
  }

  public void setStatus(EnrollmentStatus status) {
    this.status = status;
  }

  public Instant getEnrolledAt() {
    return enrolledAt;
  }

  public void setEnrolledAt(Instant enrolledAt) {
    this.enrolledAt = enrolledAt;
  }
}

