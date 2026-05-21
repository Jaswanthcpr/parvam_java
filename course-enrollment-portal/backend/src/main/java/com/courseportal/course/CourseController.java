package com.courseportal.course;

import com.courseportal.course.dto.CourseDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
  private final CourseService courseService;

  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  @GetMapping
  public List<CourseDto> list() {
    return courseService.listCourses();
  }

  @GetMapping("/{id}")
  public CourseDto get(@PathVariable long id) {
    return courseService.getCourse(id);
  }
}

