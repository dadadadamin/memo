package com.example.demo.service;

import com.example.demo.dto.OpenAIRequest;
import com.example.demo.dto.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String summarize(OpenAIRequest requestDto) {
        RestTemplate restTemplate = new RestTemplate();

        // ✅ 메시지 구성
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "당신은 여행 메모를 간결하게 요약해주는 어시스턴트입니다."),
                Map.of("role", "user", "content", requestDto.getTitle() + "\n" + requestDto.getContent())
        );

        // ✅ 요청 바디 구성
        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);
        body.put("temperature", 0.7);

        // ✅ 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<OpenAIResponse> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    OpenAIResponse.class
            );

            OpenAIResponse openAiResponse = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK && openAiResponse != null &&
                    openAiResponse.getChoices() != null && !openAiResponse.getChoices().isEmpty()) {
                return openAiResponse.getChoices().get(0).getMessage().getContent().trim();
            } else {
                return "❌ 요약 결과 없음 또는 응답 형식 오류";
            }

        } catch (Exception e) {
            return "❌ 예외 발생: " + e.getMessage();
        }
    }
}