package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    private Integer sortOrder = 0;

    private String color; // 예: "#FFAABB"
    private String imageUrl; // 예: "https://.../folder1.png"

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
