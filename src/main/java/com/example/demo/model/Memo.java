package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content; // 이모지, 긴 텍스트 허용

    private String imageUrl; // 이미지 저장 경로

    private String storagePath; // 메모 저장 위치 (폴더처럼 관리)


    @Column(name = "is_starred", nullable = false)
    private boolean starred = false; // 즐겨찾기
    // Getter
    public boolean isStarred() {
        return starred;
    }

    // Setter
    public void setIsStarred(boolean isStarred) {
        this.starred = isStarred;
    }
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    // llm 결과 컬럼 추가
    @Column(columnDefinition = "TEXT")
    private String travelInfoJson;

    @Column(length = 255)
    private String weatherInfo;

    @Column(length = 255)
    private String mapLocation;

    @Column(length = 255)
    private String reservationSuggestion;

    @Column
    private Integer estimatedCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    // 장소 마커들과의 연관관계 추가
    @OneToMany(mappedBy = "memo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MapPlace> mapPlaces = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}