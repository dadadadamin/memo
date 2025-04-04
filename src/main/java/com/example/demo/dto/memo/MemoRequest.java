package com.example.demo.dto.memo;

public class MemoRequest {
    private String title;
    private String content;
    private String imageUrl;
    private String storagePath;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStoragePath() {
        return storagePath;
    }

    // Setter 도 필요하다면 같이 정의!
}