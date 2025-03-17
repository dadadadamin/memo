package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.User;
import java.util.Optional;

//데이터베이스와 직접 연결되는 인터페이스.
//JpaRepository<User, Long> 또는 CrudRepository<User, Long> 를 상속받아 데이터 조회, 저장, 삭제 등의 기능 제공.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 정보를 가져옴
}