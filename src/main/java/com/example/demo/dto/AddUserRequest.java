package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
//클라이언트가 서버로 데이터를 보낼 때 사용하는 데이터 전송 객체 (DTO).
//일반적으로 회원가입 요청 등의 요청 데이터를 캡슐화
// 사용자 생성 요청을 처리하는 DTO, 클라이언트 - 서버간 통신
@Getter
@Setter
public class AddUserRequest {
    
    @Email(message = "올바른 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    private String password;
    
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    private String gender;

    private String birthDate;


    @Size(max = 50, message = "직업은 최대 50자까지 입력 가능합니다.")
    private String job;
}
