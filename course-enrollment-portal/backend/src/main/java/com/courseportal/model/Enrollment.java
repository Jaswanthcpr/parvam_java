package com.courseportal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "enrollments")
public class Enrollment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @Column(name = "enrolled_at", nullable = false, updatable = false)
  private Instant enrolledAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

  @PrePersist
  void prePersist() {
    if (enrolledAt == null) {
      enrolledAt = Instant.now();
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public Instant getEnrolledAt() {
    return enrolledAt;
  }

  public void setEnrolledAt(Instant enrolledAt) {
    this.enrolledAt = enrolledAt;
  }

  public EnrollmentStatus getStatus() {
    return status;
  }

  public void setStatus(EnrollmentStatus status) {
    this.status = status;
  }
}

