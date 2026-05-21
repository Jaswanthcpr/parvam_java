package com.courseportal.enrollment.dto;

import java.math.BigDecimal;
import java.util.List;

public class MyEnrollmentsResponse {
  private List<EnrollmentDto> enrollments;
  private BigDecimal totalFee;

  public MyEnrollmentsResponse() {}

  public MyEnrollmentsResponse(List<EnrollmentDto> enrollments, BigDecimal totalFee) {
    this.enrollments = enrollments;
    this.totalFee = totalFee;
  }

  public List<EnrollmentDto> getEnrollments() {
    return enrollments;
  }

  public void setEnrollments(List<EnrollmentDto> enrollments) {
    this.enrollments = enrollments;
  }

  public BigDecimal getTotalFee() {
    return totalFee;
  }

  public void setTotalFee(BigDecimal totalFee) {
    this.totalFee = totalFee;
  }
}

