package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AddUserRequest;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// REST API 요청을 처리하는 컨트롤러
//JSON 형식으로 데이터를 반환하며, @GrtMapping,@PostMapping 등의 메서드를 포함
@RequiredArgsConstructor
@RestController  // @Controller 대신 @RestController 사용 (JSON 반환)
public class UserApiController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<?> signup(@Valid @RequestBody AddUserRequest request, BindingResult bindingResult) {
        // 유효성 검사 실패 시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "회원가입 실패: 입력값을 확인해주세요."
            ));
        }

        // 회원가입 수행 후 ID 저장
        Long userId = userService.save(request);

        // JSON 응답 반환
        return ResponseEntity.ok().body(Map.of(
                "id", userId,
                "email", request.getEmail(),
                "gender", request.getGender(),
                "birthDate", request.getBirthDate(),
                "job", request.getJob(),
                "message", "회원가입 성공"
            ));
        //return "redirect:/login"; // 회원 가입이 완료된 후 로그인 페이지로 이동
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
    

}