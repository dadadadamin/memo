package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

public class FolderRequest {
    @Setter
    @Getter
    private String name;
    private String color;
    private String imageUrl; // ✅ 추가

}

