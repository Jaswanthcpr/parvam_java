package com.courseportal.ai.dto;

import com.courseportal.course.dto.CourseDto;
import java.util.List;

public class AiCoursesResponse {
  private List<CourseDto> courses;
  private String note;

  public AiCoursesResponse() {}

  public AiCoursesResponse(List<CourseDto> courses, String note) {
    this.courses = courses;
    this.note = note;
  }

  public List<CourseDto> getCourses() {
    return courses;
  }

  public void setCourses(List<CourseDto> courses) {
    this.courses = courses;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}

