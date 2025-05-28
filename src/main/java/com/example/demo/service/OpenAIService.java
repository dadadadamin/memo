package com.example.demo.service;

import com.example.demo.dto.OpenAIRequest;
import com.example.demo.dto.OpenAIResponse;
import com.example.demo.model.Folder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                Map.of("role", "system", "content",
                        "당신은 사용자의 여행 메모를 읽고, 핵심만 간결하게 요약해주는 AI 어시스턴트입니다. " +
                                "입력 언어가 한국어면 한국어로, 영어면 영어로 요약하세요. " +
                                "항상 짧고 문장식보단 단어 중심으로 요약해 주세요."),
                Map.of("role", "user", "content", requestDto.getContent())
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

    public List<String> extractPlacesFromText(String memoText) {
        RestTemplate restTemplate = new RestTemplate();

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content",
                        "다음 사용자 여행 메모에서 장소 이름만 추출해줘. " +
                                "장소는 도시, 지역, 관광 명소 등이며, 중복 없이 추출하고, JSON 배열 문자열로 응답해. " +
                                "예: [\"도쿄\", \"신주쿠\", \"하라주쿠\"]"),
                Map.of("role", "user", "content", memoText)
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);
        body.put("temperature", 0.4);

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
                String jsonArray = response.getBody().getChoices().get(0).getMessage().getContent().trim();
                // JSON 문자열 파싱
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(jsonArray, new TypeReference<List<String>>() {});
            }

        } catch (Exception e) {
            System.out.println("⚠️ 장소 추출 실패: " + e.getMessage());
        }

        return Collections.emptyList();
    }

    public String generateCaption(String title, String content) {
        RestTemplate restTemplate = new RestTemplate();

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content",
                        "당신은 여행 메모를 읽고, 짧고 센스 있는 인스타그램 스타일의 캡션을 1~2문장 또는 해시태그 형태로 추천해주는 AI입니다. " +
                                "너무 길거나 설명식 문장은 피하고, 감성적이거나 위트있는 문장 혹은 단어의 조합을 제시하세요. 입력 언어에 따라 동일한 언어로 출력하세요."),
                Map.of("role", "user", "content", title + "\n" + content)
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);
        body.put("temperature", 0.8); // 감성적인 답변 유도

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
            System.out.println("⚠️ 캡션 생성 실패: " + e.getMessage());
        }

        return "캡션을 생성하지 못했습니다.";
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