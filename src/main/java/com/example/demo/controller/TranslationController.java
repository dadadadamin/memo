// controller/TranslationController.java
package com.example.demo.controller;

import com.example.demo.dto.TranslateRequest;
import com.example.demo.dto.TranslateResponse;
import com.example.demo.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<TranslateResponse> translate(@RequestBody TranslateRequest request) {
        try {
            String translatedText = translationService.translateText(request.getText(), request.getTargetLanguage());
            return ResponseEntity.ok(new TranslateResponse(translatedText));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new TranslateResponse("번역 중 오류 발생: " + e.getMessage()));
        }
    }
}
