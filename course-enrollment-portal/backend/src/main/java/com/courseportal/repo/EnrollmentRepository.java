package com.courseportal.repo;

import com.courseportal.model.Enrollment;
import com.courseportal.model.EnrollmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
  boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);

  long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

  List<Enrollment> findByStudentIdAndStatusOrderByEnrolledAtDesc(Long studentId, EnrollmentStatus status);

  Optional<Enrollment> findByIdAndStudentId(Long id, Long studentId);
}

