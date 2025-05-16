package com.example.demo.repository;

import com.example.demo.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserId(Long userId);
    Optional<Folder> findByUserIdAndName(Long userId, String name);

}

