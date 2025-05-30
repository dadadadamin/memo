package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.dto.memo.MemoRequest;
import com.example.demo.dto.memo.MemoResponse;
import com.example.demo.model.Folder;
import com.example.demo.model.Memo;
import com.example.demo.model.User;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.MemoRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public MemoService(MemoRepository memoRepository, UserRepository userRepository,
                       FolderRepository folderRepository) {
        this.memoRepository = memoRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }

    public MemoResponse createMemo(MemoRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found by email: " + email));

        Folder folder = folderRepository.findById(request.getFolderId())
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        Memo memo = new Memo();
        memo.setTitle(request.getTitle());
        memo.setContent(request.getContent());
        memo.setImageUrl(request.getImageUrl());
        memo.setStoragePath(request.getStoragePath());
        memo.setUser(user);
        memo.setFolder(folder);
        memo.setIsStarred(request.isStarred());
        Memo saved = memoRepository.save(memo);
        return convertToResponse(saved);
    }


    public MemoResponse createQuickMemo(MemoRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Folder folder = folderRepository.findByUserIdAndName(user.getId(), "default")
                .orElseGet(() -> {
                    Folder defaultFolder = new Folder();
                    defaultFolder.setName("default");
                    defaultFolder.setUser(user);
                    defaultFolder.setType("default");
                    defaultFolder.setEditable(false);
                    return folderRepository.save(defaultFolder);
                });

        Memo memo = new Memo();
        memo.setTitle(request.getTitle());
        memo.setContent(request.getContent());
        memo.setImageUrl(request.getImageUrl());
        memo.setStoragePath(request.getStoragePath());
        memo.setUser(user);
        memo.setFolder(folder);

        Memo saved = memoRepository.save(memo);
        return convertToResponse(saved);
    }


    public List<MemoResponse> getMemosByFolder(Long folderId) {
        return memoRepository.findAllByFolderId(folderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public MemoResponse updateMemo(Long id, MemoRequest request) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));

        memo.setTitle(request.getTitle());
        memo.setContent(request.getContent());
        memo.setImageUrl(request.getImageUrl());
        memo.setStoragePath(request.getStoragePath());

        Memo updated = memoRepository.save(memo);
        return convertToResponse(updated);
    }

    public void deleteMemo(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo not found"));
        memoRepository.delete(memo);
    }

    public void deleteMemosByFolderId(Long folderId) {
        List<Memo> memos = memoRepository.findAllByFolderId(folderId);
        memoRepository.deleteAll(memos);
    }

    private MemoResponse convertToResponse(Memo memo) {
        MemoResponse res = new MemoResponse();
        res.setId(memo.getId());
        res.setTitle(memo.getTitle());
        res.setContent(memo.getContent());
        res.setImageUrl(memo.getImageUrl());
        res.setStoragePath(memo.getStoragePath());
        res.setCreatedAt(memo.getCreatedAt());
        res.setUpdatedAt(memo.getUpdatedAt());


        return res;
    }

    @Transactional
    public MemoResponse moveMemo(Long memoId, Long targetFolderId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new RuntimeException("Memo not found"));

        Folder target = folderRepository.findById(targetFolderId)
                .orElseThrow(() -> new RuntimeException("Target folder not found"));

        memo.setFolder(target);
        Memo saved = memoRepository.save(memo);
        return convertToResponse(saved);
    }

    // MemoService 내부

    @Transactional
    public List<MemoResponse> getAllMemosForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Memo> memos = memoRepository.findAllByUserId(user.getId());
        return memos.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional
    public MemoResponse toggleStarred(Long memoId, String email) {
        // 사용자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 메모 조회
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new RuntimeException("메모 없음"));

        // 자신의 메모인지 확인
        if (!memo.getUser().getId().equals(user.getId())) {
            throw new SecurityException("권한 없음");
        }

        // 즐겨찾기 상태 반전
        memo.setStarred(!memo.isStarred());

        Memo updated = memoRepository.save(memo);
        return convertToResponse(updated); // 기존 Memo -> MemoResponse 변환 메서드
    }
}