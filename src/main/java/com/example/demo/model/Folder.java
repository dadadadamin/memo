package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "folder")
@Getter
@Setter
@NoArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type = "normal"; // "normal" or "quick"

    @Column(name = "is_editable")
    private boolean isEditable = true;

    @Column(name = "is_starred", nullable = false)
    private boolean starred;

    @Column(name = "location")
    private String location;

    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer sortOrder = 0;

    private String color; // 예: "#FFAABB"
    @Column(name = "image_url")
    private String imageUrl; // 예: "https://.../folder1.png"
    @Column(nullable = false)
    private boolean isStarred = false; //즐겨찾기
    // Getter
    public boolean isStarred() {
        return isStarred;
    }

    // Setter
    public void setIsStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
