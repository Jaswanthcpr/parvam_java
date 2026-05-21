package com.courseportal.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GeminiClient {
  private final WebClient webClient;
  private final ObjectMapper objectMapper;
  private final String apiKey;
  private final String model;

  public GeminiClient(
      WebClient webClient,
      ObjectMapper objectMapper,
      @Value("${app.gemini.apiKey}") String apiKey,
      @Value("${app.gemini.model}") String model) {
    this.webClient = webClient;
    this.objectMapper = objectMapper;
    this.apiKey = apiKey;
    this.model = model;
  }

  public String generateText(String prompt) {
    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException("GEMINI_API_KEY is not configured");
    }
    String url =
        "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

    Map<String, Object> payload =
        Map.of(
            "contents",
            new Object[] {Map.of("role", "user", "parts", new Object[] {Map.of("text", prompt)})});

    try {
      JsonNode node =
          webClient
              .post()
              .uri(url)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(payload)
              .retrieve()
              .bodyToMono(JsonNode.class)
              .block();
      if (node == null) {
        throw new IllegalStateException("Empty AI response");
      }
      JsonNode textNode = node.at("/candidates/0/content/parts/0/text");
      return textNode.isMissingNode() ? objectMapper.writeValueAsString(node) : textNode.asText();
    } catch (WebClientResponseException e) {
      throw new IllegalStateException("AI request failed: " + e.getStatusCode().value());
    } catch (Exception e) {
      throw new IllegalStateException("AI request failed");
    }
  }
}
