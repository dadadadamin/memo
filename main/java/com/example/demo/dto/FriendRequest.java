package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendRequest {
    private Long friendId;        // 친구의 ID
    private String friendEmail;   // 친구의 이메일
    private String status;        // 친구 상태 (accepted, pending, rejected 등)
}