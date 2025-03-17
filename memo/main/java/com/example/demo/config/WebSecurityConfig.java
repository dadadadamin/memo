package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.service.UserDetailService;

import lombok.RequiredArgsConstructor;

// spring security 설정을 담당
//로그인. 인증 및 권한 부여 관련설정이 포함될 가능성이 높음.
//@configuration 어노테이션이 붙어있을것
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**"); // 정적 리소스 허용
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성 (Spring Security 5.7 이상)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (실습용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/signup", "/user").permitAll() // 로그인, 회원가입 요청 허용
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
            )
            .formLogin(form -> form
                .loginPage("/login") // 로그인 페이지 경로 설정
                .defaultSuccessUrl("/articles", true) // 로그인 성공 시 이동할 페이지
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login") // 로그아웃 성공 후 이동할 페이지
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 정책 설정
            );

        return http.build();
    }

    // 인증 관리자 설정
    @Bean
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(List.of(provider));
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}