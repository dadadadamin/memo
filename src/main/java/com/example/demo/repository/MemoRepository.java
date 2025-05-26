package com.example.demo.repository;

import com.example.demo.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface    MemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findAllByFolderId(Long folderId);



    List<Memo> findAllByUserId(Long id);
}