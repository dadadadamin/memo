package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.dto.memo.MemoRequest;
import com.example.demo.dto.memo.MemoResponse;
import com.example.demo.model.Memo;
import com.example.demo.model.User;
import com.example.demo.repository.MemoRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    public MemoService(MemoRepository memoRepository, UserRepository userRepository) {
        this.memoRepository = memoRepository;
        this.userRepository = userRepository;
    }

    public MemoResponse createMemo(MemoRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Memo memo = new Memo();
        memo.setTitle(request.getTitle());
        memo.setContent(request.getContent());
        memo.setImageUrl(request.getImageUrl());
        memo.setStoragePath(request.getStoragePath());
        memo.setUser(user);

        Memo saved = memoRepository.save(memo);
        return convertToResponse(saved);
    }

    public List<MemoResponse> getUserMemos(Long userId) {
        return memoRepository.findAllByUserId(userId)
                .stream()
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
}