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
//@Service // ✅ 이걸 제거하고 `WebSecurityConfig`에서 @Bean으로 관리

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // ✅ 비밀번호 검증을 위한 인코더 추가

    @Autowired
    public UserDetailService(UserRepository userRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // ✅ 주입받기
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

        // ✅ 여기에서 matches()를 사용하여 비밀번호 비교 테스트
//        String rawPassword = "1234567"; // 사용자가 입력한 비밀번호 (테스트용)
//        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
//        System.out.println("비밀번호 일치 여부: " + matches); // ✅ true가 출력되어야 정상

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // ✅ 암호화된 비밀번호를 그대로 반환해야 matches()가 정상 동작
                .authorities(user.getRole())
                .build();
    }
}