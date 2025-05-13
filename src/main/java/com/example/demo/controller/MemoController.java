package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.memo.MemoRequest;
import com.example.demo.dto.memo.MemoResponse;
import com.example.demo.service.MemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    // MemoController.java
    @PostMapping
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequest request, @RequestParam Long userId) {
        return ResponseEntity.ok(memoService.createMemo(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoResponse> updateMemo(@PathVariable Long id, @RequestBody MemoRequest request) {
        return ResponseEntity.ok(memoService.updateMemo(id, request));
    }
}
