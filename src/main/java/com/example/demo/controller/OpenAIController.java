package com.example.demo.controller;

import com.example.demo.dto.OpenAIRequest;
import com.example.demo.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ✅ 캡션 추천 엔드포인트
    @PostMapping("/caption")
    public ResponseEntity<String> generateCaption(@RequestBody OpenAIRequest request) {
        String result = openAIService.generateCaption(request.getTitle(), request.getContent());
        return ResponseEntity.ok(result);
    }

}