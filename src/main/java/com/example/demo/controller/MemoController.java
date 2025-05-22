package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.memo.MemoRequest;
import com.example.demo.dto.memo.MemoResponse;
import com.example.demo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;


    // MemoController.java
    @PostMapping("/quick")
    public ResponseEntity<?> saveQuickMemo(@RequestBody MemoRequest request,
                                           Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(memoService.createQuickMemo(request, email));
    }

    @PostMapping
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequest request, Authentication authentication) {
        String email = authentication.getName(); // JWT에서 추출된 이메일
        return ResponseEntity.ok(memoService.createMemo(request, email));
    }

    @GetMapping
    public ResponseEntity<List<MemoResponse>> getMemos(@RequestParam Long folderId) {
        List<MemoResponse> memos = memoService.getMemosByFolder(folderId);
        return ResponseEntity.ok(memos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoResponse> updateMemo(@PathVariable Long id, @RequestBody MemoRequest request) {
        return ResponseEntity.ok(memoService.updateMemo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> uploadBulk(@RequestBody List<MemoRequest> memos, @RequestParam String email) {
        for (MemoRequest memo : memos) {
            memoService.createMemo(memo, email);
        }
        return ResponseEntity.ok(Map.of("message", "메모 동기화 완료"));
    }



    @PatchMapping("/{id}/move")
    public ResponseEntity<?> moveMemo(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body
    ) {
        Long targetFolderId = body.get("targetFolderId");
        MemoResponse updated = memoService.moveMemo(id, targetFolderId);
        return ResponseEntity.ok(updated);
    }

}
