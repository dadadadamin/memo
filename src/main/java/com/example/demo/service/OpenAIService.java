package com.example.demo.service;

import com.example.demo.dto.OpenAIRequest;
import com.example.demo.dto.OpenAIResponse;
import com.example.demo.model.Folder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String summarize(OpenAIRequest requestDto) {
        RestTemplate restTemplate = new RestTemplate();

        // ✅ 프론트와 동일하게 메시지 하나로 합침
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "당신은 사용자의 여행 메모를 읽고, 핵심만 한국어로 간결하게 요약해주는 AI 어시스턴트입니다. 항상 짧고 알기 쉽게 문장식 말고 단어식으로 요약해 주세요."),
                Map.of("role", "user", "content", requestDto.getTitle() + "\n" + requestDto.getContent())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);
        body.put("temperature", 0.7);

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

    public String generateAiGuide(String name, String location, LocalDate startDate, LocalDate endDate, Folder.TravelPurpose purpose) {
        RestTemplate restTemplate = new RestTemplate();

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content",
                        "당신은 사용자의 여행 목적과 일정에 맞게 명확하고 실용적인 여행 준비 체크리스트를 제공하는 AI 어시스턴트입니다. " +
                                "설명식 문장이 아닌, 항목 중심의 깔끔한 출력 형식을 사용하세요. 각 항목을 카테고리별로 나누고, 이모지를 활용해 시각적으로 구분되도록 작성하세요. " +
                                "예: 📌 준비물, ⚠️ 유의사항, 📝 팁 등. 문장은 짧고 핵심만 전달하세요."),
                Map.of("role", "user", "content", String.format(
                        "여행 이름: %s\n여행 장소: %s\n여행 기간: %s ~ %s\n여행 목적: %s\n\n" +
                                "위 정보 기반으로 사용자가 보기 편한 AI 여행 준비 가이드를 작성해주세요.",
                        name, location, startDate.toString(), endDate.toString(), purpose.name()
                ))
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);
        body.put("temperature", 0.7);

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

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getChoices().get(0).getMessage().getContent().trim();
            }
        } catch (Exception e) {
            System.out.println("⚠️ 여행 가이드 생성 실패: " + e.getMessage());
        }

        return "AI 가이드를 생성하지 못했습니다.";
    }
}