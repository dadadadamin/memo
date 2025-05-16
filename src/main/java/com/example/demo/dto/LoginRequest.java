package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor  // ✅ 기본 생성자 추가 (JSON 바인딩 문제 해결)
@AllArgsConstructor // ✅ 모든 필드를 포함하는 생성자 추가
@ToString           // ✅ 디버깅을 위한 toString() 추가
public class LoginRequest {
    private String email;
    private String password;
}