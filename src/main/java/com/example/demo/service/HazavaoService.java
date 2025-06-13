package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HazavaoService {

  @Value("${openai.api.key}")
  private String apiKey;

  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private static final OkHttpClient CLIENT = new OkHttpClient();
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public String getDefinition(String teny) throws IOException {
    String json =
        MAPPER.writeValueAsString(
            Map.of(
                "model",
                "gpt-3.5-turbo",
                "messages",
                new Object[] {
                  Map.of(
                      "role", "user", "content", "Hazavao amin'ny teny malagasy ny teny: " + teny)
                }));

    RequestBody body = RequestBody.create(json, JSON);

    Request request =
        new Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer " + apiKey)
            .post(body)
            .build();

    try (Response response = CLIENT.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("API OpenAI error: " + response);
      }

        assert response.body() != null;
        JsonNode root = MAPPER.readTree(response.body().string());
      return root.path("choices").path(0).path("message").path("content").asText().trim();
    }
  }
}
