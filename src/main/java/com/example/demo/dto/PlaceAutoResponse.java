package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceAutoResponse {
    private List<Prediction> predictions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prediction {
        private String description;
        private String place_id;
    }
}