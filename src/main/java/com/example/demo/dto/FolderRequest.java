package com.example.demo.dto;

import com.example.demo.model.Folder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class FolderRequest {
    private String name;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
    private String imageUrl;
    private Folder.TravelPurpose purpose;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }




}

