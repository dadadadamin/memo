package com.example.demo.controller;

import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            System.out.println("이메일이 존재합니다");
            return ResponseEntity.ok("이메일이 존재합니다.");
        } else {
            System.out.println("이메일 존재하지 않음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록되지 않은 이메일입니다.");
        }
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            System.out.println("이메일 존재하지 않음 (비밀번호 변경 실패)");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록되지 않은 이메일입니다.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            System.out.println("비밀번호 불일치");
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        User user = userOptional.get();
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        System.out.println("비밀번호 변경 완료");
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}
