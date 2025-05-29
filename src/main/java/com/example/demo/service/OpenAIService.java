package com.example.demo.service;

import com.example.demo.dto.GptResponse;
import com.example.demo.dto.OpenAIRequest;
import com.example.demo.dto.OpenAIResponse;
import com.example.demo.model.Folder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

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

    @Autowired
    private TourStatService tourStatService;

    public Map<String, Object> analyzeOrRecommendTrip(OpenAIRequest requestDto) {
        try {
            String gptJson = analyzePromptText(requestDto.getContent());
            ObjectMapper mapper = new ObjectMapper();
            GptResponse gpt = mapper.readValue(gptJson, GptResponse.class);

            if ("summary".equals(gpt.getMode())) {
                return handleSummaryMode(gpt);
            } else {
                return handleRecommendMode(gpt);
            }

        } catch (JsonProcessingException e) {
            // 로그 찍고 fallback 처리
            e.printStackTrace();
            return Map.of("error", "GPT JSON 파싱 실패", "message", e.getMessage());
        }
    }



    private Map<String, Object> handleSummaryMode(GptResponse gpt) {
        List<Map<String, Object>> enriched = new ArrayList<>();

        for (String place : gpt.getPlaces()) {
            String areaCode = mapPlaceToAreaCode(place);
            Map<String, Object> placeInfo = new HashMap<>();
            placeInfo.put("place", place);
            placeInfo.put("visitorStats", tourStatService.getVisitorStats(areaCode, getToday()));
            placeInfo.put("recommendation", tourStatService.getTravelRecommendation(areaCode));
            enriched.add(placeInfo);
        }

        return Map.of(
                "mode", "summary",
                "summary", gpt.getSummary(),
                "places", enriched
        );
    }

    private Map<String, Object> handleRecommendMode(GptResponse gpt) {
        String areaCode = recommendAreaCodeByPurpose(gpt.getPurpose());

        return Map.of(
                "mode", "recommend",
                "purpose", gpt.getPurpose(),
                "recommendedArea", areaCode,
                "forecast", tourStatService.getTravelRecommendation(areaCode),
                "congestion", tourStatService.getVisitorStats(areaCode, getToday())
        );
    }
    public String mapPlaceToAreaCode(String place) {
        return switch (place) {
            case "서울" -> "1";
            case "부산" -> "6";
            case "제주" -> "39";
            case "인천" -> "2";
            case "대전" -> "3";
            case "광주" -> "5";
            case "경기" -> "31";
            default -> "1";
        };
    }

    public String recommendAreaCodeByPurpose(String purpose) {
        return switch (purpose) {
            case "자연", "휴양" -> "39";
            case "도시", "쇼핑" -> "1";
            case "맛집", "식도락" -> "6";
            default -> "1";
        };
    }

    private String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }



    public String analyzePromptText(String content) {
        RestTemplate restTemplate = new RestTemplate();

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content",
                        "당신은 사용자의 여행 메모를 분석하고, 필요하면 여행지를 추천해주는 AI입니다.\n" +
                                "- 메모가 구체적인 일정이면 장소를 추출해서 요약해 주세요.\n" +
                                "- 메모가 의도나 목적만 포함하면 적절한 국내 여행지를 추천해 주세요.\n" +
                                "- 출력 형식은 JSON으로 다음 구조를 따르세요:\n" +
                                "{\n" +
                                "  \"mode\": \"summary\" 또는 \"recommend\",\n" +
                                "  \"summary\": \"요약문 (mode가 summary일 때만)\",\n" +
                                "  \"places\": [\"서울\", \"부산\"],\n" +
                                "  \"purpose\": \"자연\",  // (recommend 모드일 때만)\n" +
                                "  \"startDate\": \"2025-06-01\",\n" +
                                "  \"endDate\": \"2025-06-03\"\n" +
                                "}"
                ),
                Map.of("role", "user", "content", content)
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);
        body.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<OpenAIResponse> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                OpenAIResponse.class
        );

        return response.getBody().getChoices().get(0).getMessage().getContent();
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
                return mapper.readValue(jsonArray, new TypeReference<List<String>>() {
                });
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
        // ✅ 필수값이 누락되면 AI 가이드 생성하지 않음
        if (name == null || location == null || startDate == null || endDate == null || purpose == null) {
            return "※ 여행 정보가 부족하여 AI 가이드를 생성할 수 없습니다.";
        }
        RestTemplate restTemplate = new RestTemplate();

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content",
                        "당신은 사용자가 입력한 여행 목적, 장소, 일정 정보를 바탕으로 **맞춤형 여행 체크리스트**를 제공하는 AI 어시스턴트입니다. " +
                                "항목은 사용자 정보에 기반해 **실질적으로 도움이 되는 내용**으로 구성하고, 단순한 보편적 추천은 피하세요. " +
                                "예: '도쿄 해변 휴양'이면 선크림, 수영복, 해양 액티비티 용품을, '파리 비즈니스 출장'이면 노트북, 프레젠테이션 자료 등을 추천하세요. " +
                                "출력은 카테고리별로 나누고, 이모지(📌, ⚠️, 📝 등)를 활용해 시각적으로 구분하세요. " +
                                "각 항목은 설명식 문장이 아닌 **간결한 키워드 형태**로 나열하며, **중복 없이 핵심만** 담아야 합니다. " +
                                "카테고리는 다음 예시처럼 구성하세요: 📌 준비물, ⚠️ 유의사항, 📝 현지 팁. 숙박, 교통, 맛집 등" +
                                "사용자의 입력에 꼭 맞는, 정제된 체크리스트를 제공하세요."
                ),
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