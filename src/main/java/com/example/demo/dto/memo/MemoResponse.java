package com.example.demo.dto.memo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String storagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean starred;

}