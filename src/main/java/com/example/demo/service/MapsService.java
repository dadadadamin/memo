package com.example.demo.service;

import com.example.demo.dto.map.MapPlaceDto;
import com.example.demo.dto.map.MapsRequest;
import com.example.demo.dto.map.MapsResponse;
import com.example.demo.model.MapPlace;
import com.example.demo.model.Memo;
import com.example.demo.repository.MapsRepository;
import com.example.demo.repository.MemoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapsService {

    private final OpenAIService openAIService;
    private final TranslationService translationService;
    private final MapsRepository mapsRepository;

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MapsResponse analyzeText(String rawText) {
        List<String> placeNames = openAIService.extractPlacesFromText(rawText);

        List<MapPlaceDto> dtoList = placeNames.stream()
                .map(name -> {
                    try {
                        String translated = translationService.translateText(name, "en");
                        return resolvePlaceToLatLngDto(translated);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return new MapsResponse(dtoList);
    }

    private MapPlaceDto resolvePlaceToLatLngDto(String placeName) {
        try {
            String encoded = URLEncoder.encode(placeName, StandardCharsets.UTF_8);
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encoded + "&key=" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("results")) {
                List<?> results = (List<?>) response.get("results");
                if (!results.isEmpty()) {
                    Map<?, ?> result = (Map<?, ?>) results.get(0);
                    Map<?, ?> geometry = (Map<?, ?>) result.get("geometry");
                    Map<?, ?> location = (Map<?, ?>) geometry.get("location");

                    double lat = ((Number) location.get("lat")).doubleValue();
                    double lng = ((Number) location.get("lng")).doubleValue();

                    return new MapPlaceDto(placeName, lat, lng); // ✅ 메모 없이 DTO 생성
                }
            }

            System.out.println("📍 Geocoding 요청: " + url);
            if (response != null) {
                System.out.println("📍 응답 내용: " + response);
            }
        } catch (Exception e) {
            System.out.println("❌ 좌표 변환 실패: " + placeName + " - " + e.getMessage());
        }

        return null;
    }
}