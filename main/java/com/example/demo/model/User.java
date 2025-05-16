package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//데이터베이스의 User 테이블과 매핑되는 엔티티 클래스.
//@Entity 어노테이션이 붙어 있으며, @Id, @Column 등의 JPA 어노테이션을 사용할 가능성이 높음
// User 엔터티 클래스, DB의 users 테이블과 매핑
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용

	private static final long serialVersionUID = 1L; // serialVersionUID 추가
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT 적용
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(nullable = true)
    private String gender; // ✅ 성별

    @Column(nullable = true)
    private String birthDate; // ✅ 생년월일

    @Column(nullable = true)
    private String job; // ✅ 직업

    @Column(nullable = false)
    private String role= "ROLE_USER";// "ROLE_USER", "ROLE_ADMIN"
    
    @Builder //빌더 패턴을 사용하여 객체 생성
    public User(String email, String password, String gender, String birthDate, String job, String role) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthDate = birthDate;
        this.job = job;
        this.role = role;
        
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role)); // ✅ 고침
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired(){
        // 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked(){
        return true; // true -> 잠금되지 않음
    }
    
    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired(){
        return true; // true -> 만료되지 않음
    }
    
    // 계정 사용 가능 여부 변환
    @Override
    public boolean isEnabled(){
        return true; // true -> 사용 가능
    }
}

