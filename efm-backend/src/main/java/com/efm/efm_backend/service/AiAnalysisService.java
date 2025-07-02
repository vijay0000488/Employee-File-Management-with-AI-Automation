package com.efm.efm_backend.service;

import com.efm.efm_backend.entity.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
public class AiAnalysisService {

    public String analyzeDocument(String content, String userQuery) throws Exception {
        String ollamaUrl = "http://localhost:11434/api/generate";
        String prompt = userQuery + "\n\n" + content;

        Map<String, Object> body = new HashMap<>();
        body.put("model", "phi3");
        body.put("prompt", prompt);

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(body);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        StringBuilder aiResult = new StringBuilder();
        restTemplate.execute(
                ollamaUrl,
                HttpMethod.POST,
                req -> {
                    req.getHeaders().add("Content-Type", "application/json");
                    req.getBody().write(requestBody.getBytes(StandardCharsets.UTF_8));
                },
                response -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().isEmpty()) {
                                JsonNode node = mapper.readTree(line);
                                if (node.has("response")) {
                                    aiResult.append(node.get("response").asText());
                                }
                            }
                        }
                    }
                    return null;
                }
        );
        return aiResult.toString();
    }




}