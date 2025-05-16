package com.example.demo.service;

import com.example.demo.model.Folder;
import com.example.demo.model.User;
import com.example.demo.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserService userService;

    public Folder createFolder(String name) {
        User user = userService.getCurrentUser();
        Folder folder = new Folder();
        folder.setName(name);
        folder.setUser(user);
        return folderRepository.save(folder);
    }

    public Folder getOrCreateDefaultFolder() {
        User user = userService.getCurrentUser();

        return folderRepository.findByUserIdAndName(user.getId(), "default")
                .orElseGet(() -> {
                    Folder folder = new Folder();
                    folder.setName("default");
                    folder.setUser(user);
                    folder.setType("default"); // 선택 사항
                    folder.setEditable(false); // 삭제 방지
                    return folderRepository.save(folder);
                });
    }


    public List<Folder> getAllFolders() {
        return folderRepository.findByUserId(userService.getCurrentUser().getId());
    }

    public void deleteFolder(Long folderId) {
        User user = userService.getCurrentUser();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폴더가 존재하지 않습니다."));

        // 현재 로그인한 유저의 폴더인지 확인
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 폴더를 삭제할 권한이 없습니다.");
        }

        folderRepository.delete(folder);
    }

}

