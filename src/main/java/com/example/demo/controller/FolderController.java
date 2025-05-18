package com.example.demo.controller;

import com.example.demo.dto.FolderRequest;
import com.example.demo.dto.memo.MemoRequest;
import com.example.demo.model.Folder;
import com.example.demo.service.FolderService;
import com.example.demo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;
    private final MemoService memoService;


    @PostMapping
    public ResponseEntity<?> createFolder(@RequestBody FolderRequest folderRequest) {
        String name = folderRequest.getName();
        Folder folder = folderService.createFolder(name); // ✅ 폴더 생성
        return ResponseEntity.ok(folder); // ✅ 응답 반환
    }

    @PostMapping("/quick")
    public ResponseEntity<?> saveQuickMemo(@RequestBody MemoRequest request,
                                           Authentication authentication) {
        String email = authentication.getName(); // ✅ JWT에서 추출한 사용자 email
        return ResponseEntity.ok(memoService.createQuickMemo(request, email));
    }


    @GetMapping
    public ResponseEntity<?> listFolders() {
        return ResponseEntity.ok(folderService.getAllFolders());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return ResponseEntity.ok().build();
    }
}

