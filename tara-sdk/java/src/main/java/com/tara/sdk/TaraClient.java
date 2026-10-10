package com.tara.sdk;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Enterprise Java SDK for TARA PromptOps Platform.
 * Uses high-performance native java.net.http.HttpClient.
 */
public class TaraClient {

    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;

    private TaraClient(Builder builder) {
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl.replaceAll("/$", "");
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(builder.timeoutSeconds))
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public PromptsApi prompts() {
        return new PromptsApi();
    }

    public SecurityApi security() {
        return new SecurityApi();
    }

    public class PromptsApi {
        public String render(String promptId, Map<String, Object> variables, String version) throws IOException, InterruptedException {
            StringBuilder json = new StringBuilder();
            json.append("{\"promptId\":\"").append(promptId).append("\",");
            json.append("\"version\":\"").append(version != null ? version : "production").append("\",");
            json.append("\"variables\":{");
            int i = 0;
            for (Map.Entry<String, Object> e : variables.entrySet()) {
                if (i++ > 0) json.append(",");
                json.append("\"").append(e.getKey()).append("\":\"").append(e.getValue().toString().replace("\"", "\\\"")).append("\"");
            }
            json.append("}}");

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/prompts/render"))
                .header("Content-Type", "application/json")
                .header("X-Tara-API-Key", apiKey)
                .header("User-Agent", "Tara-Java-SDK/1.0.0")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("TARA SDK Request failed [" + response.statusCode() + "]: " + response.body());
            }
            return response.body();
        }
    }

    public class SecurityApi {
        public String scan(String text) throws IOException, InterruptedException {
            String json = "{\"text\":\"" + text.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/security/scan"))
                .header("Content-Type", "application/json")
                .header("X-Tara-API-Key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("TARA Security Scan failed [" + response.statusCode() + "]: " + response.body());
            }
            return response.body();
        }
    }

    public static class Builder {
        private String apiKey;
        private String baseUrl = "http://localhost:8080/api/v1";
        private int timeoutSeconds = 15;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder timeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public TaraClient build() {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("API Key is required to initialize TaraClient");
            }
            return new TaraClient(this);
        }
    }
}
