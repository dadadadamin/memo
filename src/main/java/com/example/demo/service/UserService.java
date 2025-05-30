package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.Token;
import com.example.demo.repository.TokenRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AddUserRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;


@RequiredArgsConstructor //생성자 자동 생성
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public Long findUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."))
                .getId();
    }
    @Transactional
    public Long save(AddUserRequest dto) {

        String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());


        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수 입력 사항입니다.");
        }


        if (dto.getPasswordConfirm() == null || dto.getPasswordConfirm().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호 확인을 입력해주세요.");
        }


        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }


        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .gender(dto.getGender())
                .birthDate(dto.getBirthDate())
                .job(dto.getJob())
                .role("ROLE_USER")
                .build()).getId();
    }
    @Transactional
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }


        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());


        Token token = new Token();
        token.setToken(jwt);
        token.setUser(user);
        token.setExpired(false);
        token.setRevoked(false);

        tokenRepository.save(token);
        System.out.println("✅ 토큰 저장 시도: " + jwt);
        return jwt;
    }
    @Transactional
    public boolean deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
            return user.getId();
        }

        throw new RuntimeException("인증된 사용자가 아닙니다.");
    }
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        }

        throw new RuntimeException("인증된 사용자가 아닙니다.");
    }
}	
