package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

//클라이언트가 서버로 데이터를 보낼 때 사용하는 데이터 전송 객체 (DTO).
//일반적으로 회원가입 요청 등의 요청 데이터를 캡슐화
@Getter
@Setter
public class AddUserRequest {
    
    @Email(message = "올바른 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    private String password;
    
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm; // ✅ 비밀번호 확인 필드 추가
    
    @NotBlank(message = "성별을 입력해주세요.")
    @Pattern(regexp = "^(남|여)$", message = "성별은 '남' 또는 '여'만 입력 가능합니다.")
    private String gender;  // ✅ 성별 (남, 여)

    @NotBlank(message = "생년월일을 입력해주세요.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 YYYY-MM-DD 형식으로 입력해주세요.")
    private String birthDate;  // ✅ 생년월일 (YYYY-MM-DD)

    @NotBlank(message = "직업을 입력해주세요.")
    @Size(max = 50, message = "직업은 최대 50자까지 입력 가능합니다.")
    private String job;  // ✅ 직업
}