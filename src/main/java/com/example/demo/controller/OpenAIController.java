package com.example.demo.controller;

import com.example.demo.dto.OpenAIRequest;
import com.example.demo.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    private final OpenAIService openAIService;

    @Autowired
    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarizeMemo(@RequestBody OpenAIRequest request) {
        String result = openAIService.summarize(request);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/caption")
    public ResponseEntity<String> generateCaption(@RequestBody OpenAIRequest request) {
        String result = openAIService.generateCaption(request.getTitle(), request.getContent());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeMemo(@RequestBody OpenAIRequest request) {
        Map<String, Object> result = openAIService.analyzeOrRecommendTrip(request);
        return ResponseEntity.ok(result);
    }


}