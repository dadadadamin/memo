package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GptResponse {
    private String mode; // "summary" 또는 "recommend"
    private String summary; // 요약 모드일 때만
    private List<String> places;
    private String purpose;
    private String startDate;
    private String endDate;
}

