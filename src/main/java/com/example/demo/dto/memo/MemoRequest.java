package com.example.demo.dto.memo;

public class MemoRequest {
    private String title;
    private String content;
    private String imageUrl;
    private String storagePath;
    private Long folderId;

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
    public Long getFolderId() {
        return folderId;
    }
    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }
    private boolean isStarred;

    public boolean isStarred() {
        return isStarred;
    }


}