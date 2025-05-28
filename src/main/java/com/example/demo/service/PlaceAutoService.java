package com.example.demo.service;

import com.example.demo.dto.PlaceAutoResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaceAutoService {

    @Value("${google.places.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlaceAutoResponse getAutocomplete(String input) {
        String apiUrl = String.format(
                "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&key=%s&language=ko",
                UriUtils.encode(input, StandardCharsets.UTF_8),
                apiKey
        );

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> body = response.getBody();
        if (body == null || !body.containsKey("predictions")) {
            throw new IllegalStateException("❌ Google API 응답이 비정상입니다: predictions 필드 없음");
        }

        Object predictionsRaw = body.get("predictions");

        // 타입이 JSON 배열이 맞는지 확인 후 변환
        if (predictionsRaw instanceof List<?>) {
            List<PlaceAutoResponse.Prediction> predictions = objectMapper.convertValue(
                    predictionsRaw,
                    new TypeReference<>() {}
            );
            return new PlaceAutoResponse(predictions);
        } else {
            throw new IllegalStateException("❌ predictions 형식 오류: List 아님");
        }
    }
}