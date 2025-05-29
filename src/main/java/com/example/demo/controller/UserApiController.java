
package com.example.demo.controller;

import java.util.Map;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.security.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AddUserRequest;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            return userService.findUserIdByEmail(email);
        } else {
            throw new IllegalStateException("인증된 사용자를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> signup(@Valid @RequestBody AddUserRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("회원가입 실패: 입력값을 확인해주세요.");
            return ResponseEntity.badRequest().body(Map.of(
                    "message", errorMessages
            ));
        }

        Long userId = userService.save(request);

        return ResponseEntity.ok().body(Map.of(
                "id", userId,
                "email", request.getEmail(),
                "gender", request.getGender(),
                "birthDate", request.getBirthDate(),
                "job", request.getJob(),
                "message", "회원가입 성공"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        // 🔍 1. 유효성 검사 실패 시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("입력값이 유효하지 않습니다.");
            return ResponseEntity.badRequest().body(Map.of("message", errorMessages));
        }

        try {
            // 🔐 2. 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 🪪 3. 토큰 생성 및 사용자 조회
            String token = userService.login(request);
            User user = userService.findByEmail(request.getEmail());

            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "token", token,
                    "email", user.getEmail(),
                    "role", user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "이메일 또는 비밀번호가 일치하지 않습니다."
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(
                request, response, SecurityContextHolder.getContext().getAuthentication()
        );
        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        userService.deleteUserById(userId);
        session.invalidate();

        return ResponseEntity.ok("회원 탈퇴 성공");
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);

        if (deleted) {
            return ResponseEntity.ok("ID " + id + " 계정 삭제 완료");
        } else {
            return ResponseEntity.status(404).body("해당 ID의 사용자를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUserInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("사용자 정보를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "gender", user.getGender(),
                "birthDate", user.getBirthDate(),
                "job", user.getJob()
        ));
    }

} 