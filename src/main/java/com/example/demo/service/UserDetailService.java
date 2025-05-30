package com.example.demo.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
//Spring Security의 사용자 인증 관련 서비스.
//UserDetailsService 인터페이스를 구현하여, 사용자 정보를 가져오는 메서드 (loadUserByUsername)를 포함.
//Spring Security에서 로그인 시 유저 정보를 조회할 때 사용됨.
//로그인 사용자 정보 로드


@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailService(UserRepository userRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🚀 [DEBUG] loadUserByUsername() 호출됨");
        System.out.println("🚀 [DEBUG] 전달된 email 값: [" + email + "]");

        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ [ERROR] 이메일 값이 null 또는 빈 값입니다.");
            throw new UsernameNotFoundException("이메일 값이 비어 있습니다.");
        }


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        System.out.println("DB에서 찾은 사용자 이메일: " + user.getEmail());
        System.out.println("DB 저장된 비밀번호: " + user.getPassword());


        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}