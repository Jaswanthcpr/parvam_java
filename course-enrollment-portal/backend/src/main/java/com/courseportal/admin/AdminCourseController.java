package com.courseportal.admin;

import com.courseportal.admin.dto.CourseUpsertRequest;
import com.courseportal.course.dto.CourseDto;
import com.courseportal.model.Course;
import com.courseportal.repo.CourseRepository;
import com.courseportal.repo.EnrollmentRepository;
import com.courseportal.model.EnrollmentStatus;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/courses")
public class AdminCourseController {
  private final CourseRepository courseRepository;
  private final EnrollmentRepository enrollmentRepository;

  public AdminCourseController(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
    this.courseRepository = courseRepository;
    this.enrollmentRepository = enrollmentRepository;
  }

  @GetMapping
  public List<CourseDto> list() {
    return courseRepository.findAll().stream().map(this::toDto).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CourseDto create(@Valid @RequestBody CourseUpsertRequest request) {
    Course c = new Course();
    c.setName(request.getName().trim());
    c.setFee(request.getFee());
    c.setDuration(request.getDuration().trim());
    c.setSeats(request.getSeats());
    return toDto(courseRepository.save(c));
  }

  @PutMapping("/{id}")
  public CourseDto update(@PathVariable long id, @Valid @RequestBody CourseUpsertRequest request) {
    Course c =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    c.setName(request.getName().trim());
    c.setFee(request.getFee());
    c.setDuration(request.getDuration().trim());
    c.setSeats(request.getSeats());
    return toDto(courseRepository.save(c));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    if (!courseRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
    }
    courseRepository.deleteById(id);
  }

  private CourseDto toDto(Course course) {
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

