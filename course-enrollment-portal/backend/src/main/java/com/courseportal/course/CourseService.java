package com.courseportal.course;

import com.courseportal.course.dto.CourseDto;
import com.courseportal.model.Course;
import com.courseportal.model.EnrollmentStatus;
import com.courseportal.repo.CourseRepository;
import com.courseportal.repo.EnrollmentRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseService {
  private final CourseRepository courseRepository;
  private final EnrollmentRepository enrollmentRepository;

  public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
    this.courseRepository = courseRepository;
    this.enrollmentRepository = enrollmentRepository;
  }

  public List<CourseDto> listCourses() {
    return courseRepository.findAll().stream().map(this::toDtoWithAvailability).toList();
  }

  public CourseDto getCourse(long id) {
    Course course =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    return toDtoWithAvailability(course);
  }

  private CourseDto toDtoWithAvailability(Course course) {
    long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ENROLLED);
    int available = Math.max(0, course.getSeats() - (int) enrolledCount);
    CourseDto dto = new CourseDto();
    dto.setId(course.getId());
    dto.setName(course.getName());
    dto.setFee(course.getFee());
    dto.setDuration(course.getDuration());
    dto.setSeats(course.getSeats());
    dto.setAvailableSeats(available);
    return dto;
  }
}
