package com.example.demo.controller;


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



    @PostMapping("/quick")
    public ResponseEntity<?> saveQuickMemo(@RequestBody MemoRequest request,
                                           Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(memoService.createQuickMemo(request, email));
    }

    @PostMapping
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequest request, Authentication authentication) {
        String email = authentication.getName();
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

    @PatchMapping("/{id}/move")
    public ResponseEntity<?> moveMemo(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body
    ) {
        Long targetFolderId = body.get("targetFolderId");
        MemoResponse updated = memoService.moveMemo(id, targetFolderId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MemoResponse>> getAllMemos(Authentication authentication) {
        String email = authentication.getName();
        List<MemoResponse> memos = memoService.getAllMemosForUser(email);
        return ResponseEntity.ok(memos);
    }

    @PatchMapping("/{id}/star")
    public ResponseEntity<MemoResponse> toggleMemoStarred(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        MemoResponse response = memoService.toggleStarred(id, email);
        return ResponseEntity.ok(response);
    }
}
