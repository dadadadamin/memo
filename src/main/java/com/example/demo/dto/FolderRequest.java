package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class FolderRequest {
    private String name;
    private String location;       // ✅ 추가
    private LocalDate startDate;   // ✅ 추가
    private LocalDate endDate;     // ✅ 추가
    private String color;
    private String imageUrl; // ✅ 추가

}

