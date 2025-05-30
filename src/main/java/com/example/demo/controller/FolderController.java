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
import java.time.LocalDate;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;
    private final MemoService memoService;

    @PostMapping
    public ResponseEntity<?> createFolder(@RequestBody FolderRequest folderRequest) {
        String name = folderRequest.getName();
        String location = folderRequest.getLocation();
        LocalDate startDate = folderRequest.getStartDate();
        LocalDate endDate = folderRequest.getEndDate();
        String imageUrl = folderRequest.getImageUrl();
        Folder.TravelPurpose purpose = folderRequest.getPurpose();

        Folder folder = folderService.createFolder(name, location, startDate, endDate, imageUrl, purpose);
        return ResponseEntity.ok(folder);
    }


    @PostMapping("/quick")
    public ResponseEntity<?> saveQuickMemo(@RequestBody MemoRequest request,
                                           Authentication authentication) {
        String email = authentication.getName();
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

    @PatchMapping("/{id}/color")
    public ResponseEntity<?> updateFolderColor(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        String newColor = (String) body.get("color");
        if (newColor == null || newColor.isEmpty()) {
            return ResponseEntity.badRequest().body("색상 값이 누락되었습니다.");
        }

        // imageUrl 키가 있어도 무시하거나, 명시적으로 null일 경우 초기화 목적이라고 판단
        Folder updated = folderService.updateFolderColor(id, newColor);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/image")
    public ResponseEntity<?> updateFolderImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String newPath = body.get("imagePath");
        if (newPath == null || newPath.isEmpty()) {
            return ResponseEntity.badRequest().body("이미지 경로가 누락되었습니다.");
        }

        Folder updated = folderService.updateFolderImage(id, newPath);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateFolderName(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String newName = body.get("name");
        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("새 폴더 이름이 누락되었습니다.");
        }

        Folder updated = folderService.updateFolderName(id, newName.trim());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/star")
    public ResponseEntity<Folder> toggleFolderStar(@PathVariable Long id) {
        Folder updatedFolder = folderService.toggleStarred(id);
        return ResponseEntity.ok(updatedFolder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFolderById(@PathVariable Long id) {
        Folder folder = folderService.getFolderByIdAndUserCheck(id);
        return ResponseEntity.ok(folder);
    }

}

