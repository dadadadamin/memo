package com.example.demo.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AddUserRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

//일반적인 비즈니스 로직을 처리하는 서비스 클래스.
//@Service 어노테이션이 붙어 있으며, 회원 가입, 사용자 정보 조회 등의 기능이 포함될 가능성이 높음.
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Transactional
    public Long save(AddUserRequest dto) {
        // ✅ 비밀번호 입력 확인
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수 입력 사항입니다.");
        }

        // ✅ 비밀번호 확인 입력 확인
        if (dto.getPasswordConfirm() == null || dto.getPasswordConfirm().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호 확인을 입력해주세요.");
        }

        // ✅ 비밀번호와 비밀번호 확인이 일치하는지 검증
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // ✅ 검증 통과 후 회원 저장
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .gender(dto.getGender())
                .birthDate(dto.getBirthDate())
                .job(dto.getJob())
                .build()).getId();
    }
}	
