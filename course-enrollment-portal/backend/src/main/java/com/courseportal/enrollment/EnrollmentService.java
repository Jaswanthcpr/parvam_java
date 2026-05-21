package com.courseportal.enrollment;

import com.courseportal.enrollment.dto.EnrollmentDto;
import com.courseportal.enrollment.dto.MyEnrollmentsResponse;
import com.courseportal.model.Course;
import com.courseportal.model.Enrollment;
import com.courseportal.model.EnrollmentStatus;
import com.courseportal.model.Student;
import com.courseportal.repo.CourseRepository;
import com.courseportal.repo.EnrollmentRepository;
import com.courseportal.repo.StudentRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnrollmentService {
  private final EnrollmentRepository enrollmentRepository;
  private final CourseRepository courseRepository;
  private final StudentRepository studentRepository;

  public EnrollmentService(
      EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, StudentRepository studentRepository) {
    this.enrollmentRepository = enrollmentRepository;
    this.courseRepository = courseRepository;
    this.studentRepository = studentRepository;
  }

  @Transactional
  public EnrollmentDto enroll(long studentId, long courseId) {
    if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ENROLLED)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already enrolled");
    }

    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

    long activeCount = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.ENROLLED);
    if (activeCount >= course.getSeats()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No seats available");
    }

    Student student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

    Enrollment enrollment = new Enrollment();
    enrollment.setStudent(student);
    enrollment.setCourse(course);
    enrollment.setStatus(EnrollmentStatus.ENROLLED);

    Enrollment saved = enrollmentRepository.save(enrollment);
    return toDto(saved);
  }

  @Transactional
  public EnrollmentDto cancel(long studentId, long enrollmentId) {
    Enrollment enrollment =
        enrollmentRepository
            .findByIdAndStudentId(enrollmentId, studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

    enrollment.setStatus(EnrollmentStatus.CANCELED);
    return toDto(enrollmentRepository.save(enrollment));
  }

  @Transactional(readOnly = true)
  public MyEnrollmentsResponse myEnrollments(long studentId) {
    List<Enrollment> active = enrollmentRepository.findByStudentIdAndStatusOrderByEnrolledAtDesc(studentId, EnrollmentStatus.ENROLLED);
    List<EnrollmentDto> dtos = active.stream().map(this::toDto).toList();
    BigDecimal total = active.stream().map(e -> e.getCourse().getFee()).reduce(BigDecimal.ZERO, BigDecimal::add);
    return new MyEnrollmentsResponse(dtos, total);
  }

  private EnrollmentDto toDto(Enrollment e) {
    EnrollmentDto dto = new EnrollmentDto();
    dto.setId(e.getId());
    dto.setCourseId(e.getCourse().getId());
    dto.setCourseName(e.getCourse().getName());
    dto.setCourseFee(e.getCourse().getFee());
    dto.setCourseDuration(e.getCourse().getDuration());
    dto.setStatus(e.getStatus());
    dto.setEnrolledAt(e.getEnrolledAt());
    return dto;
  }
}

