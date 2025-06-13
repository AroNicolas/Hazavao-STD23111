package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HazavaoService {

  @Value("${openai.api.key}")
  private String apiKey;

  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final OkHttpClient client = new OkHttpClient();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public String getDefinition(String teny) throws Exception {
    String json =
        "{"
            + "\"model\": \"gpt-3.5-turbo\","
            + "\"messages\": [{"
            + "  \"role\": \"user\","
            + "  \"content\": \"Hazavao amin'ny teny malagasy ny teny: "
            + teny
            + "\""
            + "}]}";

    RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

    Request request =
        new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(body)
            .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new RuntimeException("API OpenAI error: " + response);
      }

      String responseBody = response.body().string();
      JsonNode root = objectMapper.readTree(responseBody);
      String definition = root.get("choices").get(0).get("message").get("content").asText();
      return definition.trim();
    }
  }
}
