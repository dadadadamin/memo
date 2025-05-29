package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LLMController {

    @PostMapping("/summarize")
    public ResponseEntity<?> summarize(@RequestBody Map<String, String> request) {
        String text = request.get("text");

        RestTemplate restTemplate = new RestTemplate();
        String pythonUrl = "http://localhost:8000/summarize";
        Map<String, String> payload = Map.of("text", text);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, payload, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "LLM 서버 요청 실패", "message", e.getMessage()));
        }
    }
}
