package com.courseportal.ai;

import com.courseportal.ai.dto.AiCoursesResponse;
import com.courseportal.course.dto.CourseDto;
import com.courseportal.model.Course;
import com.courseportal.model.Enrollment;
import com.courseportal.model.EnrollmentStatus;
import com.courseportal.model.Student;
import com.courseportal.repo.CourseRepository;
import com.courseportal.repo.EnrollmentRepository;
import com.courseportal.repo.StudentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AiService {
  private final GeminiClient geminiClient;
  private final ObjectMapper objectMapper;
  private final CourseRepository courseRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final StudentRepository studentRepository;

  public AiService(
      GeminiClient geminiClient,
      ObjectMapper objectMapper,
      CourseRepository courseRepository,
      EnrollmentRepository enrollmentRepository,
      StudentRepository studentRepository) {
    this.geminiClient = geminiClient;
    this.objectMapper = objectMapper;
    this.courseRepository = courseRepository;
    this.enrollmentRepository = enrollmentRepository;
    this.studentRepository = studentRepository;
  }

  @Transactional(readOnly = true)
  public String chat(String message) {
    List<Course> courses = courseRepository.findAll();
    String prompt =
        "You are a helpful assistant for a course enrollment portal. Answer questions about courses, fees, duration, seat availability, and enrollment rules.\n"
            + "Courses:\n"
            + courses.stream()
                .sorted(Comparator.comparingLong(Course::getId))
                .map(c -> "id=" + c.getId() + ", name=" + c.getName() + ", fee=" + c.getFee() + ", duration=" + c.getDuration() + ", seats=" + c.getSeats())
                .collect(Collectors.joining("\n"))
            + "\n"
            + "User message: "
            + message;
    return geminiClient.generateText(prompt);
  }

  @Transactional(readOnly = true)
  public AiCoursesResponse smartSearch(String query) {
    List<Course> courses = courseRepository.findAll();
    String prompt =
        "You are a course search assistant. Pick the best matching course IDs from the list.\n"
            + "User query: "
            + query
            + "\n"
            + "Courses:\n"
            + courses.stream()
                .sorted(Comparator.comparingLong(Course::getId))
                .map(
                    c ->
                        "id="
                            + c.getId()
                            + ", name="
                            + c.getName()
                            + ", fee="
                            + c.getFee()
                            + ", duration="
                            + c.getDuration()
                            + ", seats="
                            + c.getSeats())
                .collect(Collectors.joining("\n"))
            + "\n"
            + "Return STRICT JSON only: {\"courseIds\":[1,2],\"note\":\"...\"}. Choose up to 5.";

    String ai = geminiClient.generateText(prompt);
    ParsedCourseIds parsed = parseCourseIds(ai, "courseIds");
    List<CourseDto> dtos = toDtosByIds(parsed.ids());
    return new AiCoursesResponse(dtos, parsed.note());
  }

  @Transactional(readOnly = true)
  public AiCoursesResponse recommendations(long studentId) {
    Student student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

    List<Enrollment> active =
        enrollmentRepository.findByStudentIdAndStatusOrderByEnrolledAtDesc(studentId, EnrollmentStatus.ENROLLED);

    Set<Long> enrolledCourseIds = active.stream().map(e -> e.getCourse().getId()).collect(Collectors.toSet());
    List<Course> courses = courseRepository.findAll();

    String prompt =
        "You are a course recommender. Recommend next courses for the student.\n"
            + "Student name: "
            + student.getName()
            + "\n"
            + "Already enrolled course IDs: "
            + enrolledCourseIds
            + "\n"
            + "Available courses:\n"
            + courses.stream()
                .sorted(Comparator.comparingLong(Course::getId))
                .map(
                    c ->
                        "id="
                            + c.getId()
                            + ", name="
                            + c.getName()
                            + ", fee="
                            + c.getFee()
                            + ", duration="
                            + c.getDuration())
                .collect(Collectors.joining("\n"))
            + "\n"
            + "Return STRICT JSON only: {\"courseIds\":[...],\"note\":\"...\"}. Do not include already enrolled IDs. Choose up to 5.";

    String ai = geminiClient.generateText(prompt);
    ParsedCourseIds parsed = parseCourseIds(ai, "courseIds");
    List<Long> filtered = parsed.ids().stream().filter(id -> !enrolledCourseIds.contains(id)).toList();
    return new AiCoursesResponse(toDtosByIds(filtered), parsed.note());
  }

  @Transactional(readOnly = true)
  public String studentProfile(long studentId) {
    Student student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    List<Enrollment> active =
        enrollmentRepository.findByStudentIdAndStatusOrderByEnrolledAtDesc(studentId, EnrollmentStatus.ENROLLED);

    String prompt =
        "Generate a concise learning profile for a student based on enrolled courses.\n"
            + "Student: "
            + student.getName()
            + "\n"
            + "Enrolled courses:\n"
            + active.stream().map(e -> e.getCourse().getName()).collect(Collectors.joining(", "))
            + "\n"
            + "Return plain text with bullet points.";

    return geminiClient.generateText(prompt);
  }

  @Transactional(readOnly = true)
  public String analytics() {
    List<Course> courses = courseRepository.findAll();
    Map<Long, Long> counts =
        courses.stream()
            .collect(
                Collectors.toMap(
                    Course::getId,
                    c -> enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ENROLLED)));

    String prompt =
        "You are an analytics assistant for a course portal.\n"
            + "Active enrollment counts by course:\n"
            + courses.stream()
                .sorted(Comparator.comparingLong(Course::getId))
                .map(c -> "id=" + c.getId() + ", name=" + c.getName() + ", activeEnrollments=" + counts.get(c.getId()))
                .collect(Collectors.joining("\n"))
            + "\n"
            + "Return actionable insights (most enrolled, trends, seat utilization suggestions) in plain text.";

    return geminiClient.generateText(prompt);
  }

  @Transactional(readOnly = true)
  public AiCoursesResponse coursesLikelyToFillSoon() {
    List<Course> courses = courseRepository.findAll();
    StringBuilder stats = new StringBuilder();
    for (Course c : courses) {
      long active = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ENROLLED);
      double ratio = c.getSeats() == 0 ? 0.0 : (double) active / (double) c.getSeats();
      stats.append("id=").append(c.getId())
          .append(", name=").append(c.getName())
          .append(", seats=").append(c.getSeats())
          .append(", active=").append(active)
          .append(", fillRatio=").append(String.format(Locale.ROOT, "%.2f", ratio))
          .append("\n");
    }

    String prompt =
        "Predict which courses are likely to become full soon based on current fill ratios.\n"
            + "Course stats:\n"
            + stats
            + "Return STRICT JSON only: {\"courseIds\":[...],\"note\":\"...\"}. Choose up to 5 highest risk courses.";

    String ai = geminiClient.generateText(prompt);
    ParsedCourseIds parsed = parseCourseIds(ai, "courseIds");
    return new AiCoursesResponse(toDtosByIds(parsed.ids()), parsed.note());
  }

  private List<CourseDto> toDtosByIds(List<Long> ids) {
    if (ids.isEmpty()) {
      return List.of();
    }
    Map<Long, Course> byId =
        courseRepository.findAllById(ids).stream().collect(Collectors.toMap(Course::getId, c -> c));
    List<CourseDto> result = new ArrayList<>();
    for (Long id : ids) {
      Course c = byId.get(id);
      if (c != null) {
        CourseDto dto = new CourseDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setFee(c.getFee());
        dto.setDuration(c.getDuration());
        dto.setSeats(c.getSeats());
        long enrolled = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ENROLLED);
        dto.setAvailableSeats(Math.max(0, c.getSeats() - (int) enrolled));
        result.add(dto);
      }
    }
    return result;
  }

  private ParsedCourseIds parseCourseIds(String aiText, String arrayField) {
    try {
      JsonNode node = objectMapper.readTree(aiText);
      JsonNode idsNode = node.path(arrayField);
      List<Long> ids = new ArrayList<>();
      if (idsNode.isArray()) {
        for (JsonNode n : idsNode) {
          if (n.canConvertToLong()) {
            ids.add(n.asLong());
          }
        }
      }
      String note = node.path("note").isTextual() ? node.path("note").asText() : "";
      return new ParsedCourseIds(ids, note);
    } catch (Exception e) {
      return new ParsedCourseIds(List.of(), aiText);
    }
  }

  private record ParsedCourseIds(List<Long> ids, String note) {}
}
