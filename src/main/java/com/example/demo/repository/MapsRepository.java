package com.example.demo.repository;

import com.example.demo.model.MapPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MapsRepository extends JpaRepository<MapPlace, Long> {

    // 특정 메모에 연결된 장소들 가져오기
    List<MapPlace> findByMemoId(Long memoId);

    // 필요하면 장소명으로 검색도 가능
    List<MapPlace> findByNameContaining(String keyword);
}