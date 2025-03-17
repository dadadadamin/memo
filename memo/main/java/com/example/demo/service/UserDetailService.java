package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
//Spring Security의 사용자 인증 관련 서비스.
//UserDetailsService 인터페이스를 구현하여, 사용자 정보를 가져오는 메서드 (loadUserByUsername)를 포함.
//Spring Security에서 로그인 시 유저 정보를 조회할 때 사용됨.
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 사용자 이름(email)으로 사용자 정보를 가져오는 메소드
    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
