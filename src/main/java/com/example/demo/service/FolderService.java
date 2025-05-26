package com.example.demo.service;

import com.example.demo.dto.FolderRequest;
import com.example.demo.model.Folder;
import com.example.demo.model.User;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserService userService;

    private final UserRepository userRepository;

    private final MemoService memoService;



//    public Folder createFolder(String name) {
//        User user = userService.getCurrentUser();
//        Folder folder = new Folder();
//        folder.setName(name);
//        folder.setUser(user);
//        return folderRepository.save(folder);
    public Folder createFolder(String name, String location, LocalDate startDate, LocalDate endDate, String imageUrl) {
        try {
            User user = userService.getCurrentUser();

            Folder folder = new Folder();
            folder.setName(name);
            folder.setLocation(location);
            folder.setStartDate(startDate);
            folder.setEndDate(endDate);
            folder.setImageUrl(imageUrl); // ✅ 이미지 URL 추가
            folder.setStarred(false); // ✅ 누락 방지용
            folder.setUser(user);

            return folderRepository.save(folder);
        } catch (Exception e) {
            System.out.println("❌ 폴더 생성 중 예외 발생: " + e.getMessage());
            e.printStackTrace(); // 전체 스택트레이스 출력
            throw e; // 예외 다시 던져서 403 유지 (혹은 500으로 처리해도 됨)
        }
    }

//    public Folder getOrCreateDefaultFolder() {
//        User user = userService.getCurrentUser();
//
//        return folderRepository.findByUserIdAndName(user.getId(), "default")
//                .orElseGet(() -> {
//                    Folder folder = new Folder();
//                    folder.setName("default");
//                    folder.setUser(user);
//                    folder.setType("default"); // 선택 사항
//                    folder.setEditable(false); // 삭제 방지
//                    return folderRepository.save(folder);
//                });
//    }


    public List<Folder> getAllFolders() {
        return folderRepository.findByUserId(userService.getCurrentUser().getId());
    }


    @Transactional

    public void deleteFolder(Long folderId) {
        User user = userService.getCurrentUser();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폴더가 존재하지 않습니다."));

        // 현재 로그인한 유저의 폴더인지 확인
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 폴더를 삭제할 권한이 없습니다.");
        }

        // 📌 메모 먼저 삭제
        memoService.deleteMemosByFolderId(folderId);

        folderRepository.delete(folder);
    }

    private Folder getFolderByIdAndUserCheck(Long folderId) {
        User user = userService.getCurrentUser();
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폴더가 존재하지 않습니다."));

        if (!folder.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 폴더를 수정할 권한이 없습니다.");
        }

        return folder;
    }

    @Transactional
    public Folder updateFolderColor(Long folderId, String newColor) {
        Folder folder = getFolderByIdAndUserCheck(folderId);
        folder.setColor(newColor);
        folder.setImageUrl(null); // 이미지 제거 처리 (색상으로 대체 시)
        return folderRepository.save(folder);
    }

    @Transactional
    public Folder updateFolderImage(Long folderId, String imagePath) {
        Folder folder = getFolderByIdAndUserCheck(folderId);
        folder.setImageUrl(imagePath);
        return folderRepository.save(folder);
    }

    @Transactional
    public Folder updateFolderName(Long folderId, String newName) {
        Folder folder = getFolderByIdAndUserCheck(folderId);
        folder.setName(newName);
        return folderRepository.save(folder);
    }

    @Transactional
    public Folder toggleStarred(Long folderId) {
        Folder folder = getFolderByIdAndUserCheck(folderId); // 권한 확인 포함

        folder.setStarred(!folder.isStarred()); // 현재 상태 반전
        return folderRepository.save(folder);
    }


}

