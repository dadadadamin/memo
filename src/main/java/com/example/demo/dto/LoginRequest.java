package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor  //  기본 생성자 추가 (JSON 바인딩 문제 해결)
@AllArgsConstructor //  모든 필드를 포함하는 생성자 추가
@ToString           //  디버깅을 위한 toString() 추가
public class LoginRequest {
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}