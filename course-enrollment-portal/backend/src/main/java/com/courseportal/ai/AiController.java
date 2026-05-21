package com.courseportal.ai;

import com.courseportal.ai.dto.AiChatRequest;
import com.courseportal.ai.dto.AiCoursesResponse;
import com.courseportal.ai.dto.AiSearchRequest;
import com.courseportal.model.Role;
import com.courseportal.security.JwtPrincipal;
import com.courseportal.security.SecurityUtil;
import jakarta.validation.Valid;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ai")
public class AiController {
  private final AiService aiService;

  public AiController(AiService aiService) {
    this.aiService = aiService;
  }

  @PostMapping("/chat")
  public Object chat(@Valid @RequestBody AiChatRequest request) {
    String reply = aiService.chat(request.getMessage());
    return new HashMap<>() {
      {
        put("reply", reply);
      }
    };
  }

  @PostMapping("/search")
  public AiCoursesResponse search(@Valid @RequestBody AiSearchRequest request) {
    return aiService.smartSearch(request.getQuery());
  }

  @GetMapping("/recommendations")
  public AiCoursesResponse recommendations() {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    return aiService.recommendations(principal.studentId());
  }

  @GetMapping("/profile")
  public Object profile() {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    String profile = aiService.studentProfile(principal.studentId());
    return new HashMap<>() {
      {
        put("profile", profile);
      }
    };
  }

  @GetMapping("/analytics")
  public Object analytics() {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    if (principal.role() != Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
    }
    String insights = aiService.analytics();
    return new HashMap<>() {
      {
        put("insights", insights);
      }
    };
  }

  @GetMapping("/prediction")
  public AiCoursesResponse prediction() {
    JwtPrincipal principal = SecurityUtil.requirePrincipal();
    if (principal.role() != Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
    }
    return aiService.coursesLikelyToFillSoon();
  }
}

